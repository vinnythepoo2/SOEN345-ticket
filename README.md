# SOEN345 Ticket - Event Management System

A comprehensive Android application for managing events and ticket reservations with role-based access control (admin vs. customer users).

## Overview

SOEN345 Ticket is an Android-based event management platform that enables users to browse, search, and reserve tickets for various events. Administrators can create, edit, and manage event listings with real-time seat availability tracking.

## Features

### Customer Features
- **User Registration & Authentication**: Secure account creation and login using Firebase Authentication
- **Event Browsing**: View all available events with detailed information
- **Event Search & Filtering**: Search events by title and filter by category
- **Event Details**: View comprehensive event information including description, location, date, pricing, and availability
- **Ticket Reservations**: Reserve tickets for events with real-time seat availability
- **Reservation Management**: View and cancel existing reservations
- **Reservation Confirmation**: Get immediate confirmation upon successful reservation

### Admin Features
- **Event Creation**: Create new events with detailed specifications
- **Event Management**: Edit event details and availability
- **Event Cancellation**: Cancel events with automatic reservation status updates
- **Dashboard**: Centralized admin dashboard for managing all events
- **Seat Management**: Real-time tracking and updates of available seats

## Architecture

The application follows a **lightweight layered architecture** consisting of three main layers:

```
┌─────────────────────────────────────────────────┐
│         Presentation Layer (Activities)         │
│  UI Components, Navigation, User Interactions   │
└─────────────────┬───────────────────────────────┘
                  │
┌─────────────────▼───────────────────────────────┐
│    Data Access Layer (Repositories)             │
│  Firebase Data Operations, Business Logic       │
└─────────────────┬───────────────────────────────┘
                  │
┌─────────────────▼───────────────────────────────┐
│   Backend Services (Firebase)                   │
│  Authentication & Realtime Database             │
└─────────────────────────────────────────────────┘
```

### Key Components

- **Activities**: Handle UI presentation and user interactions
- **Repositories**: Manage data access and communicate with Firebase
- **Models**: Plain Java data objects (`User`, `Event`, `Reservation`)
- **Firebase**: Cloud-based authentication and real-time database for data persistence

### Database Structure

The application uses **Firebase Realtime Database** with three main collections:

- **users**: Stores user profiles and roles
- **events**: Contains event listings and availability information
- **reservations**: Tracks user reservations and their statuses

See [docs/database-design.md](docs/database-design.md) for detailed database schema.

## Prerequisites

- Android Studio (latest version)
- Android SDK 21 (Android 5.0) or higher
- Java Development Kit (JDK 11 or higher)
- Firebase project configured with Authentication and Realtime Database
- Internet connection for Firebase operations

## Setup & Installation

### 1. Clone the Repository

```bash
git clone <repository-url>
cd SOEN345-ticket
```

### 2. Configure Firebase

1. Create a Firebase project at [Firebase Console](https://console.firebase.google.com)
2. Enable Firebase Authentication (Email/Password)
3. Enable Firebase Realtime Database
4. Download the `google-services.json` file from Firebase Console
5. Place `google-services.json` in the `app/` directory

### 3. Build the Project

```bash
# Using Gradle wrapper
./gradlew build

# Or via Android Studio
# Build → Make Project
```

### 4. Run the Application

```bash
# Using Gradle wrapper
./gradlew installDebug

# Or via Android Studio
# Run → Run 'app'
```

## Project Structure

```
SOEN345-ticket/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/soen345_ticket/
│   │   │   │   ├── activities/          # UI Activities
│   │   │   │   ├── models/              # Data Models
│   │   │   │   ├── repositories/        # Data Access Layer
│   │   │   │   ├── services/            # Business Logic
│   │   │   │   └── utils/               # Utility Classes
│   │   │   └── res/
│   │   │       ├── layout/              # XML UI Layouts
│   │   │       ├── drawable/            # Drawable Resources
│   │   │       ├── values/              # String & Color Resources
│   │   │       └── menu/                # Menu Definitions
│   │   ├── test/                        # Unit Tests
│   │   └── androidTest/                 # Instrumented Tests
│   └── build.gradle.kts
├── docs/                                # Project Documentation
│   ├── architecture.md
│   ├── database-design.md
│   ├── use-cases/
│   └── sequence/
├── gradle/                              # Gradle Configuration
└── build.gradle.kts
```

## Key Activities

| Activity | Purpose |
|----------|---------|
| `SplashActivity` | Initial loading screen and authentication state routing |
| `LoginActivity` | User login interface |
| `RegisterActivity` | New user account creation |
| `MainActivity` | Customer dashboard with event listing |
| `EventDetailActivity` | Detailed event information view |
| `ReservationActivity` | Ticket reservation interface |
| `MyReservationsActivity` | View and manage user reservations |
| `AdminDashboardActivity` | Admin event management dashboard |
| `AddEditEventActivity` | Create or edit event details |

## Authentication & Authorization

- Users authenticate via Firebase Authentication (Email/Password)
- Role-based access control on app startup:
  - **Admin users** are directed to `AdminDashboardActivity`
  - **Customer users** are directed to `MainActivity`
- User roles are stored in the database as part of user profiles

## Build Configuration

The project uses **Gradle** with Kotlin DSL for build configuration:

- Minimum SDK: API 21 (Android 5.0)
- Target SDK: Latest stable Android version
- Java Compatibility: Java 11+

For detailed build settings, see [app/build.gradle.kts](app/build.gradle.kts)

## Documentation

Additional project documentation can be found in the `docs/` directory:

- [Architecture Documentation](docs/architecture.md) - Detailed architecture overview and high-level diagrams
- [Database Design](docs/database-design.md) - Firebase database schema and relationships
- [Use Case Diagrams](docs/use-cases/) - System use cases for customers and admins
- [Sequence Diagrams](docs/sequence/) - Interaction sequences for key operations

## Testing

### Unit Tests
Located in `app/src/test/java/` for testing business logic

### Instrumented Tests
Located in `app/src/androidTest/java/` for Android-specific testing

Run tests using:
```bash
./gradlew test              # Unit tests
./gradlew connectedAndroidTest  # Instrumented tests
```

