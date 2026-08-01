package com.parkinglot.service;

import com.parkinglot.dao.ParkingSpotDAO;
import com.parkinglot.dao.TicketDAO;
import com.parkinglot.dao.VehicleDAO;
import com.parkinglot.model.*;
import com.parkinglot.strategy.FeeStrategy;
import com.parkinglot.strategy.HourlyFeeStrategy;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Singleton Orchestrator for the Parking Lot System.
 * Manages floors, spots, vehicle entry/exit, fee calculation, and DB persistence sync.
 */
public class ParkingLot {

    private static ParkingLot instance;

    private final String name;
    private final List<ParkingFloor> floors;
    private final Map<String, Ticket> activeTickets;
    private final List<Ticket> ticketHistory;

    private FeeStrategy feeStrategy;

    // DAOs
    private final VehicleDAO vehicleDAO;
    private final ParkingSpotDAO spotDAO;
    private final TicketDAO ticketDAO;

    private ParkingLot(String name) {
        this.name = name;
        this.floors = new ArrayList<>();
        this.activeTickets = new ConcurrentHashMap<>();
        this.ticketHistory = Collections.synchronizedList(new ArrayList<>());
        this.feeStrategy = new HourlyFeeStrategy();

        this.vehicleDAO = new VehicleDAO();
        this.spotDAO = new ParkingSpotDAO();
        this.ticketDAO = new TicketDAO();
    }

    public static synchronized ParkingLot getInstance() {
        if (instance == null) {
            instance = new ParkingLot("Central City Parking Garage");
            instance.initializeDefaultFloors(3, 10); // Default: 3 floors, 10 spots per floor
        }
        return instance;
    }

    public void setFeeStrategy(FeeStrategy feeStrategy) {
        if (feeStrategy != null) {
            this.feeStrategy = feeStrategy;
        }
    }

    /**
     * Initializes default parking lot floors and spots.
     */
    public void initializeDefaultFloors(int numFloors, int spotsPerFloor) {
        floors.clear();
        for (int i = 1; i <= numFloors; i++) {
            ParkingFloor floor = new ParkingFloor(i);
            int compactSpots = (int) (spotsPerFloor * 0.3); // 30% Compact
            int regularSpots = (int) (spotsPerFloor * 0.5); // 50% Regular
            int largeSpots = spotsPerFloor - compactSpots - regularSpots; // 20% Large

            int spotIdCounter = 1;
            for (int c = 0; c < compactSpots; c++) {
                String id = "F" + i + "-S" + (spotIdCounter++);
                ParkingSpot spot = new ParkingSpot(id, i, SpotType.COMPACT);
                floor.addSpot(spot);
                spotDAO.saveOrUpdateSpot(spot);
            }
            for (int r = 0; r < regularSpots; r++) {
                String id = "F" + i + "-S" + (spotIdCounter++);
                ParkingSpot spot = new ParkingSpot(id, i, SpotType.REGULAR);
                floor.addSpot(spot);
                spotDAO.saveOrUpdateSpot(spot);
            }
            for (int l = 0; l < largeSpots; l++) {
                String id = "F" + i + "-S" + (spotIdCounter++);
                ParkingSpot spot = new ParkingSpot(id, i, SpotType.LARGE);
                floor.addSpot(spot);
                spotDAO.saveOrUpdateSpot(spot);
            }
            floors.add(floor);
        }
    }

    /**
     * Parks a vehicle by finding the first available matching spot across all floors.
     */
    public synchronized Ticket parkVehicle(Vehicle vehicle) {
        SpotType requiredSpotType = vehicle.getRequiredSpotType();

        // Search for nearest available spot across floors
        for (ParkingFloor floor : floors) {
            Optional<ParkingSpot> availableSpotOpt = floor.findAvailableSpot(requiredSpotType);
            if (availableSpotOpt.isPresent()) {
                ParkingSpot spot = availableSpotOpt.get();
                spot.assignVehicle(vehicle);

                String ticketId = "TKT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                LocalDateTime entryTime = LocalDateTime.now();

                Ticket ticket = new Ticket(ticketId, vehicle.getLicensePlate(), vehicle.getVehicleType(), 
                                           spot.getSpotId(), spot.getFloorNumber(), entryTime);

                activeTickets.put(ticketId, ticket);
                ticketHistory.add(ticket);

                // Persist via DAOs
                vehicleDAO.saveVehicle(vehicle);
                spotDAO.saveOrUpdateSpot(spot);
                ticketDAO.saveTicket(ticket);

                return ticket;
            }
        }

        System.out.println("[Warning] No available " + requiredSpotType + " spots currently in the parking lot!");
        return null;
    }

    /**
     * Unparks a vehicle given a valid ticket ID, calculates fee, and frees the spot.
     */
    public synchronized Ticket unparkVehicle(String ticketId) {
        Ticket ticket = activeTickets.get(ticketId);
        if (ticket == null || ticket.isClosed()) {
            System.out.println("[Error] Invalid or already closed Ticket ID: " + ticketId);
            return null;
        }

        // Find the spot
        ParkingSpot targetSpot = null;
        for (ParkingFloor floor : floors) {
            for (ParkingSpot spot : floor.getSpots()) {
                if (spot.getSpotId().equals(ticket.getSpotId())) {
                    targetSpot = spot;
                    break;
                }
            }
        }

        if (targetSpot != null) {
            targetSpot.removeVehicle();
            spotDAO.saveOrUpdateSpot(targetSpot);
        }

        LocalDateTime exitTime = LocalDateTime.now();
        double fee = feeStrategy.calculateFee(ticket.getEntryTime(), exitTime, ticket.getVehicleType());

        ticket.closeTicket(exitTime, fee);
        activeTickets.remove(ticketId);

        // Update DB via DAO
        ticketDAO.updateTicketExit(ticket);

        return ticket;
    }

    public Ticket getActiveTicket(String ticketId) {
        return activeTickets.get(ticketId);
    }

    public Collection<Ticket> getActiveTickets() {
        return activeTickets.values();
    }

    public List<Ticket> getTicketHistory() {
        return new ArrayList<>(ticketHistory);
    }

    public TicketDAO getTicketDAO() {
        return ticketDAO;
    }

    public void displayAvailabilitySummary() {
        System.out.println("\n==========================================================");
        System.out.println("            PARKING LOT AVAILABILITY SUMMARY              ");
        System.out.println("==========================================================");
        System.out.println("Parking Lot: " + name);
        System.out.println("Total Floors: " + floors.size());
        System.out.println("----------------------------------------------------------");
        
        for (ParkingFloor floor : floors) {
            System.out.println("Floor " + floor.getFloorNumber() + ":");
            for (SpotType type : SpotType.values()) {
                long free = floor.getAvailableSpotCount(type);
                long total = floor.getTotalSpotCount(type);
                System.out.printf("   - %-8s Spots: %d / %d Available\n", type, free, total);
            }
        }
        System.out.println("==========================================================\n");
    }
}
