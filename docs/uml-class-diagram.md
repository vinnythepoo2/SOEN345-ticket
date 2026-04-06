# UML Class Diagram

```mermaid
classDiagram
    direction TB

    class AppCompatActivity

    class EntryMainActivity {
        +onCreate(Bundle)
    }

    class SplashActivity {
        -UserRepository userRepository
        +onCreate(Bundle)
    }

    class LoginActivity {
        -ActivityLoginBinding binding
        -UserRepository userRepository
        -FirebaseAuth auth
        +onCreate(Bundle)
    }

    class RegisterActivity {
        -ActivityRegisterBinding binding
        -UserRepository userRepository
        -FirebaseAuth auth
        +onCreate(Bundle)
    }

    class MainActivity {
        -ActivityMainBinding binding
        -EventRepository eventRepository
        -UserRepository userRepository
        +onCreate(Bundle)
        +onStart()
        +onStop()
    }

    class EventDetailActivity {
        -ActivityEventDetailBinding binding
        -Event event
        +onCreate(Bundle)
    }

    class ReservationActivity {
        -ActivityReservationBinding binding
        -Event event
        -ReservationRepository reservationRepository
        -UserRepository userRepository
        +onCreate(Bundle)
    }

    class MyReservationsActivity {
        -ActivityMyReservationsBinding binding
        -ReservationRepository reservationRepository
        -UserRepository userRepository
        +onCreate(Bundle)
        +onStart()
        +onStop()
    }

    class AdminDashboardActivity {
        -ActivityAdminDashboardBinding binding
        -EventRepository eventRepository
        -UserRepository userRepository
        +onCreate(Bundle)
        +onStart()
        +onStop()
    }

    class AddEditEventActivity {
        -ActivityAddEditEventBinding binding
        -EventRepository eventRepository
        -Event event
        -boolean isEditMode
        +onCreate(Bundle)
    }

    class UserRepository {
        -FirebaseAuth auth
        -DatabaseReference db
        +saveUser(User) Task~Void~
        +getCurrentUser() Task~DataSnapshot~
        +getCurrentUserId() String
        +logout() void
    }

    class EventRepository {
        -DatabaseReference db
        +addEvent(Event) Task~Void~
        +updateEvent(Event) Task~Void~
        +deleteEvent(String) Task~Void~
        +getAllEventsForAdminQuery() Query
        +getEventsQuery() Query
    }

    class ReservationRepository {
        -DatabaseReference db
        +createReservation(Reservation, int) Task~Void~
        +cancelReservation(Reservation) Task~Void~
        +getReservationsQueryByUser(String) Query
    }

    class User {
        -String userId
        -String fullName
        -String email
        -String phone
        -String role
    }

    class Event {
        -String eventId
        -String title
        -String description
        -String category
        -String location
        -String date
        -int totalSeats
        -int availableSeats
        -double price
        -boolean isCancelled
    }

    class Reservation {
        -String reservationId
        -String userId
        -String eventId
        -String eventTitle
        -String reservationDate
        -int quantity
        -String status
    }

    AppCompatActivity <|-- EntryMainActivity
    AppCompatActivity <|-- SplashActivity
    AppCompatActivity <|-- LoginActivity
    AppCompatActivity <|-- RegisterActivity
    AppCompatActivity <|-- MainActivity
    AppCompatActivity <|-- EventDetailActivity
    AppCompatActivity <|-- ReservationActivity
    AppCompatActivity <|-- MyReservationsActivity
    AppCompatActivity <|-- AdminDashboardActivity
    AppCompatActivity <|-- AddEditEventActivity

    SplashActivity --> UserRepository
    LoginActivity --> UserRepository
    RegisterActivity --> UserRepository
    MainActivity --> UserRepository
    AdminDashboardActivity --> UserRepository
    ReservationActivity --> UserRepository
    MyReservationsActivity --> UserRepository

    MainActivity --> EventRepository
    AdminDashboardActivity --> EventRepository
    AddEditEventActivity --> EventRepository

    ReservationActivity --> ReservationRepository
    MyReservationsActivity --> ReservationRepository

    EventDetailActivity --> Event
    ReservationActivity --> Event
    AddEditEventActivity --> Event

    UserRepository --> User
    EventRepository --> Event
    ReservationRepository --> Reservation

    MainActivity ..> EventDetailActivity : navigates
    EventDetailActivity ..> ReservationActivity : navigates
    MainActivity ..> MyReservationsActivity : navigates
    LoginActivity ..> MainActivity : role=customer
    LoginActivity ..> AdminDashboardActivity : role=admin
    RegisterActivity ..> MainActivity : role=customer
    RegisterActivity ..> AdminDashboardActivity : role=admin
    SplashActivity ..> LoginActivity : unauthenticated
    SplashActivity ..> MainActivity : customer
    SplashActivity ..> AdminDashboardActivity : admin
    AdminDashboardActivity ..> AddEditEventActivity : navigates
    EntryMainActivity ..> SplashActivity : redirects
```

## Notes

- `EntryMainActivity` in this diagram represents `com.example.soen345_ticket.MainActivity` (placeholder launcher redirect).
- `MainActivity` in this diagram represents `com.example.soen345_ticket.activities.MainActivity` (customer dashboard).
- External Firebase and Android framework classes are intentionally abstracted to keep the diagram readable.
