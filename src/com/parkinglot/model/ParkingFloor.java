package com.parkinglot.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Represents a single floor in the Parking Lot containing multiple spots.
 */
public class ParkingFloor {
    private final int floorNumber;
    private final List<ParkingSpot> spots;

    public ParkingFloor(int floorNumber) {
        this.floorNumber = floorNumber;
        this.spots = new ArrayList<>();
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public void addSpot(ParkingSpot spot) {
        spots.add(spot);
    }

    public List<ParkingSpot> getSpots() {
        return Collections.unmodifiableList(spots);
    }

    /**
     * Finds the first available spot matching the requested spot type on this floor.
     */
    public Optional<ParkingSpot> findAvailableSpot(SpotType spotType) {
        return spots.stream()
                .filter(spot -> !spot.isOccupied() && spot.getSpotType() == spotType)
                .findFirst();
    }

    public long getAvailableSpotCount(SpotType spotType) {
        return spots.stream()
                .filter(spot -> !spot.isOccupied() && spot.getSpotType() == spotType)
                .count();
    }

    public long getTotalSpotCount(SpotType spotType) {
        return spots.stream()
                .filter(spot -> spot.getSpotType() == spotType)
                .count();
    }
}
