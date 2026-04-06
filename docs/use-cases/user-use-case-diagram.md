# User Use Case Diagram

```mermaid
flowchart LR
    User[Customer / User]
    EmailSMS[Email/SMS Gateway]

    subgraph System[Ticketing System - User Scope]
        UC_Register((Register Account))
        UC_RegisterEmail((Register via Email))
        UC_RegisterPhone((Register via Phone))

        UC_View((View Available Events))
        UC_Search((Search Events))
        UC_Filter((Filter Events))
        UC_FilterDate((Filter by Date))
        UC_FilterLocation((Filter by Location))
        UC_FilterCategory((Filter by Category))

        UC_Reserve((Make Reservation))
        UC_CancelRes((Cancel Reservation))
        UC_Confirm((Receive Confirmation\nEmail or SMS))
    end

    User --> UC_Register
    User --> UC_View
    User --> UC_Search
    User --> UC_Filter
    User --> UC_Reserve
    User --> UC_CancelRes

    UC_Register -. extends .-> UC_RegisterEmail
    UC_Register -. extends .-> UC_RegisterPhone

    UC_Filter -. extends .-> UC_FilterDate
    UC_Filter -. extends .-> UC_FilterLocation
    UC_Filter -. extends .-> UC_FilterCategory

    UC_Reserve -. includes .-> UC_Confirm
    UC_CancelRes -. may trigger .-> UC_Confirm

    EmailSMS --> UC_Confirm
```

## Use Cases (User)

- `UC1`: Register account (email or phone)
- `UC2`: View available events
- `UC3`: Search events
- `UC4`: Filter events by date/location/category
- `UC5`: Cancel reservation
- `UC6`: Receive confirmation via email/SMS
