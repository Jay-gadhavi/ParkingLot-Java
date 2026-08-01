package com.parkinglot.dao;

import com.parkinglot.model.Ticket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Data Access Object (DAO) for Ticket persistence and history reports in MySQL.
 */
public class TicketDAO {

    public void saveTicket(Ticket ticket) {
        DBConnection db = DBConnection.getInstance();
        if (!db.isDbAvailable()) {
            return;
        }

        String sql = "INSERT INTO tickets (ticket_id, license_plate, spot_id, floor_number, entry_time, status) " +
                     "VALUES (?, ?, ?, ?, ?, 'ACTIVE')";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, ticket.getTicketId());
            stmt.setString(2, ticket.getLicensePlate());
            stmt.setString(3, ticket.getSpotId());
            stmt.setInt(4, ticket.getFloorNumber());
            stmt.setTimestamp(5, Timestamp.valueOf(ticket.getEntryTime()));
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("[DAO Error] Failed to save ticket: " + e.getMessage());
        }
    }

    public void updateTicketExit(Ticket ticket) {
        DBConnection db = DBConnection.getInstance();
        if (!db.isDbAvailable()) {
            return;
        }

        String sql = "UPDATE tickets SET exit_time = ?, fee = ?, status = 'CLOSED' WHERE ticket_id = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, Timestamp.valueOf(ticket.getExitTime()));
            stmt.setDouble(2, ticket.getFee());
            stmt.setString(3, ticket.getTicketId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("[DAO Error] Failed to update ticket exit: " + e.getMessage());
        }
    }

    /**
     * Executes a relational JOIN query across tickets, vehicles, and parking_spots
     * to generate a detailed historical report.
     */
    public void printParkingHistoryReport() {
        DBConnection db = DBConnection.getInstance();
        if (!db.isDbAvailable()) {
            System.out.println("[DB Note] MySQL is not connected. Enable MySQL to query relational DB history.");
            return;
        }

        String sql = "SELECT t.ticket_id, t.license_plate, v.vehicle_type, t.spot_id, t.floor_number, " +
                     "t.entry_time, t.exit_time, t.fee, t.status " +
                     "FROM tickets t " +
                     "JOIN vehicles v ON t.license_plate = v.license_plate " +
                     "JOIN parking_spots s ON t.spot_id = s.spot_id " +
                     "ORDER BY t.entry_time DESC";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("\n----------------------------------------------------------------------------------");
            System.out.println("                         PARKING HISTORY REPORT (MySQL JOIN)                       ");
            System.out.println("----------------------------------------------------------------------------------");
            System.out.printf("%-38s | %-10s | %-6s | %-6s | %-5s | %-8s | %-7s\n", 
                              "Ticket ID", "Plate", "Type", "Spot", "Floor", "Fee", "Status");
            System.out.println("----------------------------------------------------------------------------------");

            boolean found = false;
            while (rs.next()) {
                found = true;
                String ticketId = rs.getString("ticket_id");
                String plate = rs.getString("license_plate");
                String type = rs.getString("vehicle_type");
                String spotId = rs.getString("spot_id");
                int floor = rs.getInt("floor_number");
                double fee = rs.getDouble("fee");
                String status = rs.getString("status");

                System.out.printf("%-38s | %-10s | %-6s | %-6s | %-5d | $%-7.2f | %-7s\n",
                                  ticketId, plate, type, spotId, floor, fee, status);
            }

            if (!found) {
                System.out.println("No parking history records found in database.");
            }
            System.out.println("----------------------------------------------------------------------------------\n");

        } catch (SQLException e) {
            System.err.println("[DAO Error] Failed to fetch history report: " + e.getMessage());
        }
    }
}
