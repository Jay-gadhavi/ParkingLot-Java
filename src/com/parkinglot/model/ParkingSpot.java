package com.parkinglot.model;

/**
 * Represents a single parking spot on a floor.
 */
public class ParkingSpot {
    private final String spotId;
    private final int floorNumber;
    private final SpotType spotType;
    private boolean isOccupied;
    private Vehicle currentVehicle;

    public ParkingSpot(String spotId, int floorNumber, SpotType spotType) {
        this.spotId = spotId;
        this.floorNumber = floorNumber;
        this.spotType = spotType;
        this.isOccupied = false;
        this.currentVehicle = null;
    }

    public String getSpotId() {
        return spotId;
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public SpotType getSpotType() {
        return spotType;
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public Vehicle getCurrentVehicle() {
        return currentVehicle;
    }

    public void setOccupied(boolean occupied) {
        isOccupied = occupied;
    }

    public synchronized boolean assignVehicle(Vehicle vehicle) {
        if (isOccupied) {
            return false;
        }
        if (vehicle.getRequiredSpotType() != spotType) {
            return false;
        }
        this.currentVehicle = vehicle;
        this.isOccupied = true;
        return true;
    }

    public synchronized void removeVehicle() {
        this.currentVehicle = null;
        this.isOccupied = false;
    }

    @Override
    public String toString() {
        return "Spot[" + spotId + " | Floor " + floorNumber + " | " + spotType + " | " + (isOccupied ? "OCCUPIED" : "FREE") + "]";
    }
}
