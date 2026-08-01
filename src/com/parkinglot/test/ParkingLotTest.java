package com.parkinglot.test;

import com.parkinglot.model.*;
import com.parkinglot.service.ParkingLot;
import com.parkinglot.strategy.HourlyFeeStrategy;

import java.time.LocalDateTime;

/**
 * Automated Verification Test for Parking Lot LLD system.
 */
public class ParkingLotTest {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("  RUNNING AUTOMATED VERIFICATION SUITE           ");
        System.out.println("==================================================");

        ParkingLot parkingLot = ParkingLot.getInstance();

        // 1. Test Singleton Pattern
        ParkingLot secondInstance = ParkingLot.getInstance();
        assert (parkingLot == secondInstance) : "Singleton assertion failed!";
        System.out.println("[PASS] Singleton pattern verified (same memory reference).");

        // 2. Test Parking Bike
        Vehicle bike = new Bike("KA-01-HH-1234");
        Ticket bikeTicket = parkingLot.parkVehicle(bike);
        assert (bikeTicket != null) : "Bike parking failed!";
        assert (bikeTicket.getVehicleType() == VehicleType.BIKE) : "Vehicle type mismatch!";
        System.out.println("[PASS] Bike parked successfully: " + bikeTicket.getTicketId() + " at " + bikeTicket.getSpotId());

        // 3. Test Parking Car
        Vehicle car = new Car("MH-12-PQ-9999");
        Ticket carTicket = parkingLot.parkVehicle(car);
        assert (carTicket != null) : "Car parking failed!";
        assert (carTicket.getVehicleType() == VehicleType.CAR) : "Vehicle type mismatch!";
        System.out.println("[PASS] Car parked successfully: " + carTicket.getTicketId() + " at " + carTicket.getSpotId());

        // 4. Test Strategy Pattern Fee Calculation
        HourlyFeeStrategy feeStrategy = new HourlyFeeStrategy();
        LocalDateTime entry = LocalDateTime.now().minusHours(2).minusMinutes(30); // 2.5 hours -> rounded to 3 hours
        LocalDateTime exit = LocalDateTime.now();
        
        double carFee = feeStrategy.calculateFee(entry, exit, VehicleType.CAR);
        assert (carFee == 60.0) : "Hourly fee calculation incorrect for Car!"; // 3 * $20 = $60
        System.out.println("[PASS] Fee Strategy pattern verified. 2.5 hours for Car = $" + carFee + " (Expected $60.00)");

        // 5. Test Unparking
        Ticket unparkedCarTicket = parkingLot.unparkVehicle(carTicket.getTicketId());
        assert (unparkedCarTicket != null && unparkedCarTicket.isClosed()) : "Unparking failed!";
        System.out.println("[PASS] Car unparked successfully. Ticket closed with fee: $" + unparkedCarTicket.getFee());

        System.out.println("\n==================================================");
        System.out.println("   ALL AUTOMATED VERIFICATION TESTS PASSED!       ");
        System.out.println("==================================================");
    }
}
