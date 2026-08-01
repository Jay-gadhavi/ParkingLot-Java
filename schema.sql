-- MySQL Schema Definition for Parking Lot System
-- Database: parking_lot_db

CREATE DATABASE IF NOT EXISTS parking_lot_db;
USE parking_lot_db;

-- 1. Table for stored vehicles
CREATE TABLE IF NOT EXISTS vehicles (
    license_plate VARCHAR(20) PRIMARY KEY,
    vehicle_type VARCHAR(10) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Table for parking spots
CREATE TABLE IF NOT EXISTS parking_spots (
    spot_id VARCHAR(20) PRIMARY KEY,
    floor_number INT NOT NULL,
    spot_type VARCHAR(10) NOT NULL,
    is_occupied BOOLEAN DEFAULT FALSE
);

-- 3. Table for parking tickets
CREATE TABLE IF NOT EXISTS tickets (
    ticket_id VARCHAR(36) PRIMARY KEY,
    license_plate VARCHAR(20) NOT NULL,
    spot_id VARCHAR(20) NOT NULL,
    floor_number INT NOT NULL,
    entry_time TIMESTAMP NOT NULL,
    exit_time TIMESTAMP NULL,
    fee DOUBLE NULL,
    status VARCHAR(10) DEFAULT 'ACTIVE',
    FOREIGN KEY (license_plate) REFERENCES vehicles(license_plate),
    FOREIGN KEY (spot_id) REFERENCES parking_spots(spot_id)
);
