package com.parkinglot;

import com.parkinglot.model.*;
import com.parkinglot.service.ParkingLot;

import java.util.Collection;
import java.util.List;
import java.util.Scanner;

/**
 * Console CLI Entry Point for the Parking Lot System.
 * Menu-driven interface for interacting with core parking lot operations.
 */
public class Main {

    public static void main(String[] args) {
        ParkingLot parkingLot = ParkingLot.getInstance();
        Scanner scanner = new Scanner(System.in);

        System.out.println("==================================================================");
        System.out.println("     WELCOME TO THE OBJECT-ORIENTED PARKING LOT MANAGEMENT SYSTEM ");
        System.out.println("==================================================================");

        boolean running = true;
        while (running) {
            printMenu();
            System.out.print("Enter choice (1-6): ");
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                    handleParkVehicle(parkingLot, scanner);
                    break;
                case "2":
                    handleUnparkVehicle(parkingLot, scanner);
                    break;
                case "3":
                    parkingLot.displayAvailabilitySummary();
                    break;
                case "4":
                    handleViewTicket(parkingLot, scanner);
                    break;
                case "5":
                    handleViewHistory(parkingLot, scanner);
                    break;
                case "6":
                    running = false;
                    System.out.println("\nThank you for using Parking Lot System. Goodbye!");
                    break;
                default:
                    System.out.println("\n[Error] Invalid selection. Please enter a number between 1 and 6.");
            }
        }

        scanner.close();
    }

    private static void printMenu() {
        System.out.println("\n---------------- MAIN MENU ----------------");
        System.out.println("1. Park a Vehicle");
        System.out.println("2. Unpark a Vehicle");
        System.out.println("3. Check Spot Availability");
        System.out.println("4. View Active Ticket Details");
        System.out.println("5. View Parking History Reports");
        System.out.println("6. Exit System");
        System.out.println("-------------------------------------------");
    }

    private static void handleParkVehicle(ParkingLot parkingLot, Scanner scanner) {
        System.out.println("\n--- PARK VEHICLE ---");
        System.out.print("Enter Vehicle License Plate (e.g. MH-12-AB-1234): ");
        String plate = scanner.nextLine().trim();
        if (plate.isEmpty()) {
            System.out.println("[Error] License plate cannot be empty.");
            return;
        }

        System.out.println("Select Vehicle Type:");
        System.out.println("  1. Bike (Compact Spot)");
        System.out.println("  2. Car (Regular Spot)");
        System.out.println("  3. Truck (Large Spot)");
        System.out.print("Enter option (1-3): ");
        String typeChoice = scanner.nextLine().trim();

        Vehicle vehicle;
        switch (typeChoice) {
            case "1":
                vehicle = new Bike(plate);
                break;
            case "2":
                vehicle = new Car(plate);
                break;
            case "3":
                vehicle = new Truck(plate);
                break;
            default:
                System.out.println("[Error] Invalid vehicle type selection.");
                return;
        }

        Ticket ticket = parkingLot.parkVehicle(vehicle);
        if (ticket != null) {
            System.out.println("\n[SUCCESS] Vehicle parked successfully!");
            System.out.println("----------------------------------------");
            System.out.println(ticket);
            System.out.println("----------------------------------------");
        }
    }

    private static void handleUnparkVehicle(ParkingLot parkingLot, Scanner scanner) {
        System.out.println("\n--- UNPARK VEHICLE ---");
        System.out.print("Enter Ticket ID (e.g. TKT-12345678): ");
        String ticketId = scanner.nextLine().trim();

        Ticket ticket = parkingLot.unparkVehicle(ticketId);
        if (ticket != null) {
            System.out.println("\n[SUCCESS] Vehicle unparked successfully!");
            System.out.println("----------------------------------------");
            System.out.println(ticket);
            System.out.println("----------------------------------------");
        }
    }

    private static void handleViewTicket(ParkingLot parkingLot, Scanner scanner) {
        System.out.println("\n--- VIEW TICKET ---");
        System.out.print("Enter Ticket ID: ");
        String ticketId = scanner.nextLine().trim();

        Ticket ticket = parkingLot.getActiveTicket(ticketId);
        if (ticket != null) {
            System.out.println("\n----------------------------------------");
            System.out.println(ticket);
            System.out.println("----------------------------------------");
        } else {
            System.out.println("[Info] Active ticket not found in memory. Checking active list...");
            Collection<Ticket> activeTickets = parkingLot.getActiveTickets();
            if (activeTickets.isEmpty()) {
                System.out.println("No currently active tickets in memory.");
            } else {
                System.out.println("Active Ticket IDs in System:");
                activeTickets.forEach(t -> System.out.println(" - " + t.getTicketId() + " (" + t.getLicensePlate() + ")"));
            }
        }
    }

    private static void handleViewHistory(ParkingLot parkingLot, Scanner scanner) {
        System.out.println("\n--- PARKING HISTORY REPORT ---");
        System.out.println("Select Report Source:");
        System.out.println("  1. In-Memory Session History");
        System.out.println("  2. MySQL Database History (Relational JOIN Query)");
        System.out.print("Enter option (1-2): ");
        String choice = scanner.nextLine().trim();

        if ("1".equals(choice)) {
            List<Ticket> history = parkingLot.getTicketHistory();
            if (history.isEmpty()) {
                System.out.println("No parking history recorded yet in this session.");
            } else {
                System.out.println("\n------------------------------------------------------------------");
                System.out.printf("%-12s | %-12s | %-6s | %-6s | %-8s | %-7s\n",
                                  "Ticket ID", "Plate", "Type", "Spot", "Fee", "Status");
                System.out.println("------------------------------------------------------------------");
                for (Ticket t : history) {
                    System.out.printf("%-12s | %-12s | %-6s | %-6s | $%-7.2f | %-7s\n",
                                      t.getTicketId(), t.getLicensePlate(), t.getVehicleType(), 
                                      t.getSpotId(), t.getFee(), (t.isClosed() ? "CLOSED" : "ACTIVE"));
                }
                System.out.println("------------------------------------------------------------------\n");
            }
        } else if ("2".equals(choice)) {
            parkingLot.getTicketDAO().printParkingHistoryReport();
        } else {
            System.out.println("[Error] Invalid report selection.");
        }
    }
}
