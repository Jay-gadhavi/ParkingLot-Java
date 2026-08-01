package com.parkinglot.strategy;

import com.parkinglot.model.VehicleType;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Hourly-based fee strategy implementation.
 * Charges flat hourly rates based on vehicle type (Bike: $10/hr, Car: $20/hr, Truck: $30/hr).
 * Partial hours are rounded up to full hours (minimum 1 hour).
 */
public class HourlyFeeStrategy implements FeeStrategy {

    private static final double BIKE_RATE_PER_HOUR = 10.0;
    private static final double CAR_RATE_PER_HOUR = 20.0;
    private static final double TRUCK_RATE_PER_HOUR = 30.0;

    @Override
    public double calculateFee(LocalDateTime entryTime, LocalDateTime exitTime, VehicleType vehicleType) {
        if (entryTime == null || exitTime == null || exitTime.isBefore(entryTime)) {
            throw new IllegalArgumentException("Invalid entry/exit timestamp for fee calculation.");
        }

        Duration duration = Duration.between(entryTime, exitTime);
        long minutes = duration.toMinutes();
        
        // Round up to nearest hour, minimum 1 hour
        long hours = (long) Math.ceil((double) minutes / 60.0);
        if (hours == 0) {
            hours = 1;
        }

        double rate;
        switch (vehicleType) {
            case BIKE:
                rate = BIKE_RATE_PER_HOUR;
                break;
            case CAR:
                rate = CAR_RATE_PER_HOUR;
                break;
            case TRUCK:
                rate = TRUCK_RATE_PER_HOUR;
                break;
            default:
                rate = CAR_RATE_PER_HOUR;
        }

        return hours * rate;
    }
}
