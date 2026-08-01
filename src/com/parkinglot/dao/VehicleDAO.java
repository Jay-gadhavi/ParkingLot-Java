package com.parkinglot.dao;

import com.parkinglot.model.Vehicle;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Data Access Object (DAO) for Vehicle persistence operations in MySQL.
 */
public class VehicleDAO {

    public void saveVehicle(Vehicle vehicle) {
        DBConnection db = DBConnection.getInstance();
        if (!db.isDbAvailable()) {
            return; // Fallback handled in-memory
        }

        String sql = "INSERT INTO vehicles (license_plate, vehicle_type) VALUES (?, ?) " +
                     "ON DUPLICATE KEY UPDATE vehicle_type = VALUES(vehicle_type)";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, vehicle.getLicensePlate());
            stmt.setString(2, vehicle.getVehicleType().name());
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("[DAO Error] Failed to save vehicle: " + e.getMessage());
        }
    }
}
