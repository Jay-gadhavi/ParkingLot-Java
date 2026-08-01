# Object-Oriented Parking Lot System (Java & MySQL)

A production-grade, extensible **Low-Level Design (LLD)** implementation of a Multi-Floor Parking Lot System built with **Java** and **MySQL**.

This project is tailored specifically for **Josh Technology Group (JTG)** software developer technical rounds, demonstrating core OOP principles, GoF Design Patterns, clean code practices, and relational database persistence via JDBC.

---

## Technical Features & Architectural Highlights

### 1. Object-Oriented Design Principles
- **Abstraction & Inheritance**: `Vehicle` abstract base class extended by `Car`, `Bike`, and `Truck` subclasses.
- **Encapsulation**: Strict private member fields with public getters, thread-safe spot allocations, and immutable enums (`VehicleType`, `SpotType`).
- **Polymorphism**: Dynamic spot type mapping and strategy-based fee calculation algorithms.

### 2. Design Patterns Implemented
- **Strategy Pattern (`FeeStrategy`)**: Isolates fee calculation algorithms (`HourlyFeeStrategy`). Makes adding new fee structures (e.g., flat daily rate, peak-hour pricing) seamless without modifying core business logic.
- **Singleton Pattern (`ParkingLot`, `DBConnection`)**: Guarantees a single system instance for managing floors and thread-safe database connections.
- **Data Access Object (DAO) Pattern (`VehicleDAO`, `ParkingSpotDAO`, `TicketDAO`)**: Decouples business logic from MySQL JDBC persistence calls.

### 3. Database Persistence (MySQL & In-Memory Fallback)
- **Relational Schema**: Normalized tables (`vehicles`, `parking_spots`, `tickets`) with primary keys, foreign keys, and status flags.
- **Relational SQL JOIN Queries**: Generates parking history reports by joining `tickets ⨝ vehicles ⨝ parking_spots`.
- **Graceful Fallback**: Automatically degrades to session in-memory mode if MySQL connection is unavailable.

---

## Project Structure

```
ParkingLot Java/
├── schema.sql                         # MySQL DDL Schema script
├── run.ps1                            # PowerShell compile & run script
├── README.md                          # Project & Interview Guide
├── src/
│   └── com/
│       └── parkinglot/
│           ├── Main.java              # Interactive Console CLI
│           ├── model/
│           │   ├── Vehicle.java       # Abstract Base Class
│           │   ├── Car.java           # Subclass
│           │   ├── Bike.java          # Subclass
│           │   ├── Truck.java         # Subclass
│           │   ├── VehicleType.java   # Enum (BIKE, CAR, TRUCK)
│           │   ├── SpotType.java      # Enum (COMPACT, REGULAR, LARGE)
│           │   ├── ParkingSpot.java   # Spot entity
│           │   ├── ParkingFloor.java  # Floor manager
│           │   └── Ticket.java        # Parking ticket entity
│           ├── strategy/
│           │   ├── FeeStrategy.java   # Strategy Interface
│           │   └── HourlyFeeStrategy.java # Hourly Rate Implementation
│           ├── dao/
│           │   ├── DBConnection.java  # JDBC Singleton Manager
│           │   ├── VehicleDAO.java    # Vehicle DB Operations
│           │   ├── ParkingSpotDAO.java# ParkingSpot DB Operations
│           │   └── TicketDAO.java     # Ticket DB & SQL JOIN Operations
│           ├── service/
│           │   └── ParkingLot.java    # Singleton Orchestrator
│           └── test/
│               └── ParkingLotTest.java# Automated Test Suite
```

---

## Database Setup (`schema.sql`)

To run with MySQL persistence:
1. Open MySQL workbench or CLI.
2. Execute the commands in [schema.sql](file:///c:/Users/jayga/Desktop/ParkingLot%20Java/schema.sql):
   ```sql
   CREATE DATABASE IF NOT EXISTS parking_lot_db;
   USE parking_lot_db;
   ```
3. Ensure DB credentials in `com.parkinglot.dao.DBConnection` match your local MySQL configuration.

---

## How to Run

### Option 1: Run Interactive CLI (PowerShell)
```powershell
.\run.ps1
```

### Option 2: Run Automated Verification Tests
```powershell
javac -d bin (Get-ChildItem -Recurse -Filter *.java src).FullName
java -ea -cp bin com.parkinglot.test.ParkingLotTest
```

---

## Interview Talking Points for JTG

When presenting this project in a technical interview:

1. **Why Abstract Class for Vehicle instead of Enum?**
   > *"I chose an abstract `Vehicle` class over a simple enum because real-world vehicles carry entity-specific attributes (license plate, owner info) and behaviors. It adheres to the Open-Closed Principle (OCP)—if we want to add an `ElectricCar` with charging status later, we extend `Vehicle` without touching existing code."*

2. **Why Strategy Pattern for Fees?**
   > *"Fee rules vary by location, duration, or vehicle type. By encapsulating fee calculation inside a `FeeStrategy` interface, we can swap `HourlyFeeStrategy` with `PeakHourFeeStrategy` or `FlatRateFeeStrategy` at runtime without altering the `ParkingLot` orchestrator."*

3. **How is thread safety handled?**
   > *"Spot assignment methods (`assignVehicle`, `removeVehicle`) and Singleton initializations use synchronization to avoid race conditions when multiple vehicles attempt to park simultaneously."*

4. **Database Design & Persistence**:
   > *"I implemented the DAO pattern using JDBC to persist entry/exit records. When generating history reports, a multi-table SQL `JOIN` brings together ticket details, vehicle type metadata, and spot floor allocations."*
