# Use Case Diagram Overview

```mermaid
flowchart LR
    User[Customer / User]
    Admin[Administrator]
    EmailSMS[Email/SMS Gateway]

    subgraph System[SOEN345 Ticketing System]
        UC_Register((Register Account\nusing Email or Phone))
        UC_View((View Available Events))
        UC_Search((Search Events))
        UC_Filter((Filter Events\nby Date/Location/Category))
        UC_Reserve((Make Reservation))
        UC_CancelRes((Cancel Reservation))
        UC_Confirm((Send Confirmation\nEmail or SMS))

        UC_AddEvent((Add New Event))
        UC_EditEvent((Edit Existing Event))
        UC_CancelEvent((Cancel Event))
    end

    User --> UC_Register
    User --> UC_View
    User --> UC_Search
    User --> UC_Filter
    User --> UC_Reserve
    User --> UC_CancelRes

    Admin --> UC_AddEvent
    Admin --> UC_EditEvent
    Admin --> UC_CancelEvent

    UC_Reserve -. includes .-> UC_Confirm
    UC_CancelRes -. may trigger .-> UC_Confirm
    UC_CancelEvent -. may trigger .-> UC_Confirm

    EmailSMS --> UC_Confirm
```

## Covered Functional Requirements

### Users

- Register using email or phone number
- View a list of available events
- Search and filter events by date, location, or category
- Cancel reservations
- Receive confirmations via email or SMS

### Administrators

- Add new event
- Edit an existing event
- Cancel an event
