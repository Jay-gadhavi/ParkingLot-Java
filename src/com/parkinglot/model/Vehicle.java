package com.parkinglot.model;

/**
 * Abstract class representing a generic Vehicle.
 * Demonstrates Object-Oriented Abstraction and Encapsulation.
 */
public abstract class Vehicle {
    private final String licensePlate;
    private final VehicleType vehicleType;

    public Vehicle(String licensePlate, VehicleType vehicleType) {
        if (licensePlate == null || licensePlate.trim().isEmpty()) {
            throw new IllegalArgumentException("License plate cannot be null or empty.");
        }
        this.licensePlate = licensePlate.trim().toUpperCase();
        this.vehicleType = vehicleType;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    /**
     * Map vehicle type to corresponding required parking spot type.
     */
    public SpotType getRequiredSpotType() {
        switch (vehicleType) {
            case BIKE:
                return SpotType.COMPACT;
            case CAR:
                return SpotType.REGULAR;
            case TRUCK:
                return SpotType.LARGE;
            default:
                throw new IllegalStateException("Unknown vehicle type: " + vehicleType);
        }
    }

    @Override
    public String toString() {
        return vehicleType + " [" + licensePlate + "]";
    }
}
