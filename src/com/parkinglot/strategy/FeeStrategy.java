package com.parkinglot.strategy;

import com.parkinglot.model.VehicleType;
import java.time.LocalDateTime;

/**
 * Strategy interface for calculating parking fees.
 * Demonstrates the GoF Strategy Pattern for extensible fee calculation algorithms.
 */
public interface FeeStrategy {
    double calculateFee(LocalDateTime entryTime, LocalDateTime exitTime, VehicleType vehicleType);
}
