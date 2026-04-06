# Project Architecture

This project follows a lightweight layered architecture:

- **Presentation layer**: Android `Activity` classes (UI + navigation + user interactions)
- **Data access layer**: Repository classes wrapping Firebase access
- **Domain/data models**: Plain Java model classes (`User`, `Event`, `Reservation`)
- **Backend services**: Firebase Authentication and Realtime Database

## High-Level Diagram

```mermaid
flowchart TB
    subgraph UI[Presentation Layer - Activities]
        Splash[SplashActivity]
        Login[LoginActivity]
        Register[RegisterActivity]

        Main[MainActivity\nCustomer Dashboard]
        EventDetail[EventDetailActivity]
        ReservationAct[ReservationActivity]
        MyReservations[MyReservationsActivity]

        Admin[AdminDashboardActivity]
        AddEdit[AddEditEventActivity]
    end

    subgraph Repo[Data Access Layer - Repositories]
        UserRepo[UserRepository]
        EventRepo[EventRepository]
        ReservationRepo[ReservationRepository]
    end

    subgraph Model[Models]
        UserM[User]
        EventM[Event]
        ReservationM[Reservation]
    end

    subgraph Firebase[Firebase]
        Auth[Firebase Auth]
        DB[(Realtime Database)]
        UsersNode[/users/]
        EventsNode[/events/]
        ReservationsNode[/reservations/]
    end

    Splash --> Login
    Splash --> Main
    Splash --> Admin

    Login --> Main
    Login --> Admin
    Register --> Main
    Register --> Admin

    Main --> EventDetail --> ReservationAct --> Main
    Main --> MyReservations

    Admin --> AddEdit --> Admin

    Login --> UserRepo
    Register --> UserRepo
    Splash --> UserRepo
    Main --> UserRepo
    Admin --> UserRepo

    Main --> EventRepo
    Admin --> EventRepo
    AddEdit --> EventRepo

    ReservationAct --> ReservationRepo
    MyReservations --> ReservationRepo

    UserRepo --> UserM
    EventRepo --> EventM
    ReservationRepo --> ReservationM

    UserRepo --> Auth
    UserRepo --> DB
    EventRepo --> DB
    ReservationRepo --> DB

    DB --> UsersNode
    DB --> EventsNode
    DB --> ReservationsNode
```

## Package Structure

- `app/src/main/java/com/example/soen345_ticket/activities` -> UI and navigation
- `app/src/main/java/com/example/soen345_ticket/repositories` -> Firebase data operations
- `app/src/main/java/com/example/soen345_ticket/models` -> shared data objects
- `app/src/main/res/layout` -> XML UI layouts

## Key Notes

- Role-based routing is handled after authentication (`admin` vs `customer`).
- Reservation creation/cancellation updates seat counts using Firebase transactions.
- Lists are rendered from Firebase queries using FirebaseUI Recycler adapters.
