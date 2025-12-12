classDiagram
    %% =========================
    %% CORE DOMAIN CLASSES
    %% =========================
    class K2559603_Book {
        -id: String
        -title: String
        -author: String
        -category: String
        -isbn: String
        -tags: List<String>
        -reviews: List<String>
        -edition: String
        -featured: boolean
        -recommended: boolean
        -specialEdition: boolean
        -borrowHistory: List<K2559603_BorrowTransaction>
        -reservations: List<K2559603_Reservation>
        -state: K2559603_BookState

        +K2559603_Book(builder: K2559603_Book_Builder)
        +getId(): String
        +getTitle(): String
        +getAuthor(): String
        +getCategory(): String
        +getIsbn(): String
        +getTags(): List<String>
        +getReviews(): List<String>
        +getEdition(): String
        +isFeatured(): boolean
        +isRecommended(): boolean
        +isSpecialEdition(): boolean
        +addBorrowTransaction(tx: K2559603_BorrowTransaction): void
        +getBorrowHistory(): List<K2559603_BorrowTransaction>
        +getReservations(): List<K2559603_Reservation>
        +addReservation(reservation: K2559603_Reservation): void
        +removeReservation(reservation: K2559603_Reservation): void
        +hasActiveReservations(): boolean
        +getState(): K2559603_BookState
        +setState(state: K2559603_BookState): void
    }

    class K2559603_Book_Builder {
        -id: String
        -title: String
        -author: String
        -category: String
        -isbn: String
        -tags: List<String>
        -reviews: List<String>
        -edition: String
        -featured: boolean
        -recommended: boolean
        -specialEdition: boolean

        +K2559603_Book_Builder(id: String, title: String, author: String, category: String, isbn: String)
        +tags(tags: List<String>): K2559603_Book_Builder
        +reviews(reviews: List<String>): K2559603_Book_Builder
        +edition(edition: String): K2559603_Book_Builder
        +featured(featured: boolean): K2559603_Book_Builder
        +recommended(recommended: boolean): K2559603_Book_Builder
        +specialEdition(specialEdition: boolean): K2559603_Book_Builder
        +build(): K2559603_Book
    }

    class K2559603_User {
        -id: String
        -name: String
        -email: String
        -contactNumber: String
        -membershipType: K2559603_MembershipType
        -borrowHistory: List<K2559603_BorrowTransaction>

        +K2559603_User(id: String, name: String, email: String, contactNumber: String, membershipType: K2559603_MembershipType)
        +getId(): String
        +getName(): String
        +getEmail(): String
        +getContactNumber(): String
        +getMembershipType(): K2559603_MembershipType
        +getBorrowHistory(): List<K2559603_BorrowTransaction>
        +addBorrowTransaction(tx: K2559603_BorrowTransaction): void
        +update(event: K2559603_NotificationEvent): void
    }

    class K2559603_BorrowTransaction {
        -id: String
        -book: K2559603_Book
        -user: K2559603_User
        -borrowDate: LocalDate
        -dueDate: LocalDate
        -returnDate: LocalDate
        -finePaid: double

        +K2559603_BorrowTransaction(id: String, book: K2559603_Book, user: K2559603_User, borrowDate: LocalDate, dueDate: LocalDate)
        +getId(): String
        +getBook(): K2559603_Book
        +getUser(): K2559603_User
        +getBorrowDate(): LocalDate
        +getDueDate(): LocalDate
        +getReturnDate(): LocalDate
        +setReturnDate(returnDate: LocalDate): void
        +getFinePaid(): double
        +setFinePaid(finePaid: double): void
        +isReturned(): boolean
    }

    class K2559603_Reservation {
        -id: String
        -book: K2559603_Book
        -user: K2559603_User
        -reservedDate: LocalDate
        -active: boolean

        +K2559603_Reservation(id: String, book: K2559603_Book, user: K2559603_User, reservedDate: LocalDate)
        +getId(): String
        +getBook(): K2559603_Book
        +getUser(): K2559603_User
        +getReservedDate(): LocalDate
        +isActive(): boolean
        +setActive(active: boolean): void
    }

    class K2559603_MembershipType {
        <<enum>>
        STUDENT
        FACULTY
        GUEST

        -borrowLimit: int
        -borrowDays: int

        +getBorrowLimit(): int
        +getBorrowDays(): int
    }

    class K2559603_LibraryService {
        -books: Map<String,K2559603_Book>
        -users: Map<String,K2559603_User>
        -transactions: List<K2559603_BorrowTransaction>
        -reservations: List<K2559603_Reservation>
        -notificationService: K2559603_NotificationService

        +K2559603_LibraryService(notificationService: K2559603_NotificationService)
        +seedSampleData(): void

        +createAndAddBook(title: String, author: String, category: String, isbn: String): K2559603_Book
        +addBook(book: K2559603_Book): void
        +getBook(bookId: String): K2559603_Book
        +getAllBooks(): Collection<K2559603_Book>
        +updateBookDetails(bookId: String, title: String, author: String, category: String, isbn: String): void
        +removeBook(bookId: String): void

        +createAndAddUser(name: String, email: String, contactNumber: String, membershipType: K2559603_MembershipType): K2559603_User
        +addUser(user: K2559603_User): void
        +getUser(userId: String): K2559603_User
        +getAllUsers(): Collection<K2559603_User>
        +removeUser(userId: String): void

        +borrowBook(userId: String, bookId: String): void
        +returnBook(userId: String, bookId: String): void
        +forceReturnWithoutFine(userId: String, bookId: String): void

        +reserveBook(userId: String, bookId: String): void
        +cancelReservation(userId: String, bookId: String): void

        +scanDueAndOverdue(): void
        +getMostBorrowedBooks(topN: int): List<K2559603_Book>
        +getActiveBorrowers(): List<K2559603_User>
        +getOverdueTransactions(): List<K2559603_BorrowTransaction>
    }

    %% =========================
    %% OBSERVER PATTERN
    %% =========================
    class K2559603_NotificationObserver {
        <<interface>>
        +update(event: K2559603_NotificationEvent): void
    }

    class K2559603_NotificationSubject {
        <<interface>>
        +registerObserver(observer: K2559603_NotificationObserver): void
        +removeObserver(observer: K2559603_NotificationObserver): void
        +notifyObservers(event: K2559603_NotificationEvent): void
    }

    class K2559603_NotificationEventType {
        <<enum>>
        DUE_SOON
        OVERDUE
        RESERVATION_AVAILABLE
    }

    class K2559603_NotificationEvent {
        -type: K2559603_NotificationEventType
        -message: String
        -user: K2559603_User
        -book: K2559603_Book

        +K2559603_NotificationEvent(type: K2559603_NotificationEventType, message: String, user: K2559603_User, book: K2559603_Book)
        +getType(): K2559603_NotificationEventType
        +getMessage(): String
        +getUser(): K2559603_User
        +getBook(): K2559603_Book
    }

    class K2559603_NotificationService {
        -observers: List<K2559603_NotificationObserver>

        +registerObserver(observer: K2559603_NotificationObserver): void
        +removeObserver(observer: K2559603_NotificationObserver): void
        +notifyObservers(event: K2559603_NotificationEvent): void
    }

    %% =========================
    %% STRATEGY PATTERN
    %% =========================
    class K2559603_FineCalculationStrategy {
        <<interface>>
        +calculateFine(overdueDays: long): double
    }

    class K2559603_StudentFineStrategy {
        +calculateFine(overdueDays: long): double
    }

    class K2559603_FacultyFineStrategy {
        +calculateFine(overdueDays: long): double
    }

    class K2559603_GuestFineStrategy {
        +calculateFine(overdueDays: long): double
    }

    class K2559603_FineCalculationContext {
        -strategy: K2559603_FineCalculationStrategy

        +setStrategy(strategy: K2559603_FineCalculationStrategy): void
        +calculateFine(overdueDays: long): double
        +strategyFor(type: K2559603_MembershipType): K2559603_FineCalculationStrategy
    }

    %% =========================
    %% STATE PATTERN
    %% =========================
    class K2559603_BookState {
        <<interface>>
        +borrow(book: K2559603_Book, user: K2559603_User): void
        +returnBook(book: K2559603_Book, user: K2559603_User): void
        +reserve(book: K2559603_Book, user: K2559603_User): void
        +getName(): String
    }

    class K2559603_AvailableState {
        +borrow(book: K2559603_Book, user: K2559603_User): void
        +returnBook(book: K2559603_Book, user: K2559603_User): void
        +reserve(book: K2559603_Book, user: K2559603_User): void
        +getName(): String
    }

    class K2559603_BorrowedState {
        +borrow(book: K2559603_Book, user: K2559603_User): void
        +returnBook(book: K2559603_Book, user: K2559603_User): void
        +reserve(book: K2559603_Book, user: K2559603_User): void
        +getName(): String
    }

    class K2559603_ReservedState {
        +borrow(book: K2559603_Book, user: K2559603_User): void
        +returnBook(book: K2559603_Book, user: K2559603_User): void
        +reserve(book: K2559603_Book, user: K2559603_User): void
        +getName(): String
    }

    %% =========================
    %% DECORATOR PATTERN
    %% =========================
    class K2559603_BookView {
        <<interface>>
        +getDisplayTitle(): String
    }

    class K2559603_BasicBookView {
        -book: K2559603_Book

        +K2559603_BasicBookView(book: K2559603_Book)
        +getDisplayTitle(): String
    }

    class K2559603_BookViewDecorator {
        -inner: K2559603_BookView

        +K2559603_BookViewDecorator(inner: K2559603_BookView)
        +getDisplayTitle(): String
    }

    class K2559603_FeaturedBookDecorator {
        +K2559603_FeaturedBookDecorator(inner: K2559603_BookView)
        +getDisplayTitle(): String
    }

    class K2559603_RecommendedBookDecorator {
        +K2559603_RecommendedBookDecorator(inner: K2559603_BookView)
        +getDisplayTitle(): String
    }

    class K2559603_SpecialEditionBookDecorator {
        +K2559603_SpecialEditionBookDecorator(inner: K2559603_BookView)
        +getDisplayTitle(): String
    }

    %% =========================
    %% COMMAND PATTERN
    %% =========================
    class K2559603_Command {
        <<interface>>
        +execute(): void
        +undo(): void
        +getName(): String
    }

    class K2559603_BorrowBookCommand {
        -libraryService: K2559603_LibraryService
        -userId: String
        -bookId: String

        +K2559603_BorrowBookCommand(service: K2559603_LibraryService, userId: String, bookId: String)
        +execute(): void
        +undo(): void
        +getName(): String
    }

    class K2559603_ReturnBookCommand {
        -libraryService: K2559603_LibraryService
        -userId: String
        -bookId: String

        +K2559603_ReturnBookCommand(service: K2559603_LibraryService, userId: String, bookId: String)
        +execute(): void
        +undo(): void
        +getName(): String
    }

    class K2559603_ReserveBookCommand {
        -libraryService: K2559603_LibraryService
        -userId: String
        -bookId: String

        +K2559603_ReserveBookCommand(service: K2559603_LibraryService, userId: String, bookId: String)
        +execute(): void
        +undo(): void
        +getName(): String
    }

    class K2559603_CancelReservationCommand {
        -libraryService: K2559603_LibraryService
        -userId: String
        -bookId: String

        +K2559603_CancelReservationCommand(service: K2559603_LibraryService, userId: String, bookId: String)
        +execute(): void
        +undo(): void
        +getName(): String
    }

    class K2559603_CommandHistory {
        -history: Stack<K2559603_Command>

        +push(cmd: K2559603_Command): void
        +undoLast(): void
    }

    %% =========================
    %% RELATIONSHIPS
    %% =========================

    %% Core domain
    K2559603_Book "1" o-- "*" K2559603_BorrowTransaction
    K2559603_Book "1" o-- "*" K2559603_Reservation
    K2559603_User "1" o-- "*" K2559603_BorrowTransaction
    K2559603_User "1" o-- "*" K2559603_Reservation
    K2559603_User --> K2559603_MembershipType
    K2559603_Book_Builder ..> K2559603_Book

    %% Observer
    K2559603_User ..|> K2559603_NotificationObserver
    K2559603_NotificationService ..|> K2559603_NotificationSubject
    K2559603_NotificationService "1" o-- "*" K2559603_NotificationObserver
    K2559603_NotificationEvent --> K2559603_NotificationEventType
    K2559603_NotificationEvent --> K2559603_User
    K2559603_NotificationEvent --> K2559603_Book

    %% State
    K2559603_AvailableState ..|> K2559603_BookState
    K2559603_BorrowedState ..|> K2559603_BookState
    K2559603_ReservedState ..|> K2559603_BookState
    K2559603_Book "1" o-- "1" K2559603_BookState

    %% Strategy
    K2559603_StudentFineStrategy ..|> K2559603_FineCalculationStrategy
    K2559603_FacultyFineStrategy ..|> K2559603_FineCalculationStrategy
    K2559603_GuestFineStrategy ..|> K2559603_FineCalculationStrategy
    K2559603_FineCalculationContext --> K2559603_FineCalculationStrategy
    K2559603_FineCalculationContext --> K2559603_MembershipType

    %% Decorator
    K2559603_BasicBookView ..|> K2559603_BookView
    K2559603_BookViewDecorator ..|> K2559603_BookView
    K2559603_FeaturedBookDecorator --|> K2559603_BookViewDecorator
    K2559603_RecommendedBookDecorator --|> K2559603_BookViewDecorator
    K2559603_SpecialEditionBookDecorator --|> K2559603_BookViewDecorator
    K2559603_BasicBookView --> K2559603_Book

    %% Command
    K2559603_BorrowBookCommand ..|> K2559603_Command
    K2559603_ReturnBookCommand ..|> K2559603_Command
    K2559603_ReserveBookCommand ..|> K2559603_Command
    K2559603_CancelReservationCommand ..|> K2559603_Command
    K2559603_CommandHistory "1" o-- "*" K2559603_Command

    %% LibraryService aggregation
    K2559603_LibraryService "1" o-- "*" K2559603_Book
    K2559603_LibraryService "1" o-- "*" K2559603_User
    K2559603_LibraryService "1" o-- "*" K2559603_BorrowTransaction
    K2559603_LibraryService "1" o-- "*" K2559603_Reservation
    K2559603_LibraryService "1" o-- "1" K2559603_NotificationService
