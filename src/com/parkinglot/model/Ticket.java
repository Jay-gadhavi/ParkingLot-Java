package com.parkinglot.model;

import java.time.LocalDateTime;

/**
 * Represents a parking ticket generated upon vehicle entry.
 */
public class Ticket {
    private final String ticketId;
    private final String licensePlate;
    private final VehicleType vehicleType;
    private final String spotId;
    private final int floorNumber;
    private final LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private double fee;
    private boolean isClosed;

    public Ticket(String ticketId, String licensePlate, VehicleType vehicleType, String spotId, int floorNumber, LocalDateTime entryTime) {
        this.ticketId = ticketId;
        this.licensePlate = licensePlate;
        this.vehicleType = vehicleType;
        this.spotId = spotId;
        this.floorNumber = floorNumber;
        this.entryTime = entryTime;
        this.exitTime = null;
        this.fee = 0.0;
        this.isClosed = false;
    }

    public String getTicketId() {
        return ticketId;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public String getSpotId() {
        return spotId;
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public LocalDateTime getExitTime() {
        return exitTime;
    }

    public double getFee() {
        return fee;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void closeTicket(LocalDateTime exitTime, double fee) {
        this.exitTime = exitTime;
        this.fee = fee;
        this.isClosed = true;
    }

    @Override
    public String toString() {
        return "Ticket ID: " + ticketId +
                "\nVehicle: " + licensePlate + " (" + vehicleType + ")" +
                "\nSpot: " + spotId + " (Floor " + floorNumber + ")" +
                "\nEntry Time: " + entryTime +
                "\nExit Time: " + (exitTime != null ? exitTime : "N/A (Active)") +
                "\nFee: " + (isClosed ? "$" + String.format("%.2f", fee) : "Pending") +
                "\nStatus: " + (isClosed ? "CLOSED" : "ACTIVE");
    }
}
