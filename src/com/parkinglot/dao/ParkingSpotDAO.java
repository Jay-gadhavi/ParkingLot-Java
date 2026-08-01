package com.parkinglot.dao;

import com.parkinglot.model.ParkingSpot;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Data Access Object (DAO) for ParkingSpot persistence operations in MySQL.
 */
public class ParkingSpotDAO {

    public void saveOrUpdateSpot(ParkingSpot spot) {
        DBConnection db = DBConnection.getInstance();
        if (!db.isDbAvailable()) {
            return;
        }

        String sql = "INSERT INTO parking_spots (spot_id, floor_number, spot_type, is_occupied) VALUES (?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE is_occupied = VALUES(is_occupied)";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, spot.getSpotId());
            stmt.setInt(2, spot.getFloorNumber());
            stmt.setString(3, spot.getSpotType().name());
            stmt.setBoolean(4, spot.isOccupied());
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("[DAO Error] Failed to update spot: " + e.getMessage());
        }
    }
}
