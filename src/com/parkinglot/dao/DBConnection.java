package com.parkinglot.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton database connection manager utilizing JDBC.
 * Provides thread-safe connection handling and auto-detects MySQL availability.
 */
public class DBConnection {

    private static DBConnection instance;
    private Connection connection;

    private static final String URL = "jdbc:mysql://localhost:3306/parking_lot_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "password"; // Update credentials as per local setup

    private boolean dbAvailable = false;

    private DBConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
            this.dbAvailable = true;
            System.out.println("[DB] Connected to MySQL database successfully.");
        } catch (ClassNotFoundException e) {
            this.dbAvailable = false;
        } catch (SQLException e) {
            this.dbAvailable = false;
        }
    }

    public static synchronized DBConnection getInstance() {
        if (instance == null || instance.isClosed()) {
            instance = new DBConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    public boolean isDbAvailable() {
        return dbAvailable && !isClosed();
    }

    private boolean isClosed() {
        try {
            return connection == null || connection.isClosed();
        } catch (SQLException e) {
            return true;
        }
    }
}
