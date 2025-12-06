# Smart Library Management System (CLI) - ESU Kandy

University: ESU Kandy  
Student ID (UML prefix): **K2559603**

This is a Maven-based Java 17 project implementing a **Smart Library Management System** with a **Command Line Interface (CLI)**.

## Features

- Book Management (add, update, remove)
- User Management (add, remove)
- Borrow / Return books with due dates
- Reservation system with automatic notification when a reserved book is returned
- Overdue fine calculation based on membership type
- Notifications for:
  - Due soon
  - Overdue
  - Reservation availability
- Reports:
  - Most borrowed books
  - Active borrowers
  - Overdue books
- CLI UX with colours and emojis (where supported)

## Design Patterns Used

- **Observer Pattern**  
  `NotificationService` is the subject; `User` implements `NotificationObserver`.  
  Used for sending due, overdue, and reservation-available notifications.

- **Strategy Pattern**  
  `FineCalculationStrategy` interface with `StudentFineStrategy`, `FacultyFineStrategy`, and `GuestFineStrategy`.  
  Selected via `FineCalculationContext.strategyFor(MembershipType)`.

- **Builder Pattern**  
  `Book` has an inner `Book.Builder` used to create complex book objects with optional fields (tags, reviews, edition, featured/recommended/specialEdition flags).

- **State Pattern**  
  `BookState` interface with `AvailableState`, `BorrowedState`, `ReservedState`.  
  Encapsulates rules for borrowing, returning, reserving depending on current book state.

- **Decorator Pattern**  
  `BookView` interface with `BasicBookView` and decorators:  
  `FeaturedBookDecorator`, `RecommendedBookDecorator`, `SpecialEditionBookDecorator`.  
  Used to render book titles in the CLI with optional features.

- **Command Pattern**  
  `Command` interface and concrete commands:  
  `BorrowBookCommand`, `ReturnBookCommand`, `ReserveBookCommand`, `CancelReservationCommand`.  
  `CommandHistory` keeps a stack of executed commands and supports `undoLast()`.

## Build & Run

### Prerequisites

- Java 17+
- Maven 3+

### Build

```bash
mvn clean package
```

### Run

```bash
mvn exec:java -Dexec.mainClass=edu.esu.kandy.slms.cli.SmartLibraryApp
```

or run the generated JAR:

```bash
java -jar target/smart-library-management-1.0-SNAPSHOT.jar
```

## Running Tests

```bash
mvn test
```

## UML Class Diagrams

See `docs/uml-mermaid.md` and `docs/uml-plantuml.puml`.  
All classes in the diagram are prefixed with your student ID **K2559603_** as required.
