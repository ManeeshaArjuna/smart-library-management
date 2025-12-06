package edu.esu.kandy.slms.service;

import edu.esu.kandy.slms.membership.MembershipType;
import edu.esu.kandy.slms.model.*;
import edu.esu.kandy.slms.observer.NotificationEvent;
import edu.esu.kandy.slms.observer.NotificationEventType;
import edu.esu.kandy.slms.observer.NotificationService;
import edu.esu.kandy.slms.state.AvailableState;
import edu.esu.kandy.slms.state.BookState;
import edu.esu.kandy.slms.strategy.FineCalculationContext;
import edu.esu.kandy.slms.strategy.FineCalculationStrategy;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class LibraryService {

    private final Map<String, Book> books = new HashMap<>();
    private final Map<String, User> users = new HashMap<>();
    private final List<BorrowTransaction> transactions = new ArrayList<>();
    private final List<Reservation> reservations = new ArrayList<>();

    private final NotificationService notificationService;

    public LibraryService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public void seedSampleData() {
        Book b1 = new Book.Builder("B001", "Clean Code", "Robert C. Martin", "Programming", "9780132350884")
                .featured(true)
                .edition("1st")
                .build();

        Book b2 = new Book.Builder("B002", "Design Patterns", "Gang of Four", "Programming", "9780201633610")
                .recommended(true)
                .build();

        Book b3 = new Book.Builder("B003", "Effective Java", "Joshua Bloch", "Programming", "9780134685991")
                .specialEdition(true)
                .build();

        addBook(b1);
        addBook(b2);
        addBook(b3);

        User u1 = new User("U001", "Alice", "alice@esu.lk", "0711111111", MembershipType.STUDENT);
        User u2 = new User("U002", "Bob", "bob@esu.lk", "0722222222", MembershipType.FACULTY);
        User u3 = new User("U003", "Charlie", "charlie@esu.lk", "0733333333", MembershipType.GUEST);

        addUser(u1);
        addUser(u2);
        addUser(u3);
    }

    // Book management
    public void addBook(Book book) {
        books.put(book.getId(), book);
    }

    public Book getBook(String bookId) {
        return books.get(bookId);
    }

    public Collection<Book> getAllBooks() {
        return books.values();
    }

    public void updateBookDetails(String bookId, String title, String author, String category, String isbn) {
        Book existing = books.get(bookId);
        if (existing == null) return;

        Book updated = new Book.Builder(bookId,
                title == null || title.isBlank() ? existing.getTitle() : title,
                author == null || author.isBlank() ? existing.getAuthor() : author,
                category == null || category.isBlank() ? existing.getCategory() : category,
                isbn == null || isbn.isBlank() ? existing.getIsbn() : isbn)
                .featured(existing.isFeatured())
                .recommended(existing.isRecommended())
                .specialEdition(existing.isSpecialEdition())
                .tags(existing.getTags())
                .reviews(existing.getReviews())
                .edition(existing.getEdition())
                .build();

        updated.getBorrowHistory().addAll(existing.getBorrowHistory());
        updated.getReservations().addAll(existing.getReservations());
        updated.setState(existing.getState());

        books.put(bookId, updated);
    }

    public void removeBook(String bookId) {
        books.remove(bookId);
    }

    // User management
    public void addUser(User user) {
        users.put(user.getId(), user);
        notificationService.registerObserver(user);
    }

    public User getUser(String userId) {
        return users.get(userId);
    }

    public Collection<User> getAllUsers() {
        return users.values();
    }

    public void removeUser(String userId) {
        User user = users.remove(userId);
        if (user != null) {
            notificationService.removeObserver(user);
        }
    }

    // Core operations
    public void borrowBook(String userId, String bookId) {
        User user = getUser(userId);
        Book book = getBook(bookId);
        if (user == null || book == null) {
            System.out.println("\u274C Invalid user or book ID.");
            return;
        }

        long currentBorrowed = transactions.stream()
                .filter(tx -> tx.getUser().equals(user) && !tx.isReturned())
                .count();
        if (currentBorrowed >= user.getMembershipType().getBorrowLimit()) {
            System.out.println("\u26A0\uFE0F Borrow limit exceeded for user " + user.getName());
            return;
        }

        BookState state = book.getState();
        try {
            state.borrow(book, user);
        } catch (IllegalStateException e) {
            System.out.println("\u26A0\uFE0F " + e.getMessage());
            return;
        }

        LocalDate borrowDate = LocalDate.now();
        LocalDate dueDate = borrowDate.plusDays(user.getMembershipType().getBorrowDays());

        BorrowTransaction tx = new BorrowTransaction(java.util.UUID.randomUUID().toString(), book, user, borrowDate, dueDate);
        transactions.add(tx);
        book.addBorrowTransaction(tx);
        user.addBorrowTransaction(tx);

        System.out.println("\u2705 Book borrowed. Due date: " + dueDate);
    }

    public void forceReturnWithoutFine(String userId, String bookId) {
        internalReturn(userId, bookId, false);
    }

    public void returnBook(String userId, String bookId) {
        internalReturn(userId, bookId, true);
    }

    private void internalReturn(String userId, String bookId, boolean applyFine) {
        User user = getUser(userId);
        Book book = getBook(bookId);
        if (user == null || book == null) {
            System.out.println("\u274C Invalid user or book ID.");
            return;
        }

        java.util.Optional<BorrowTransaction> optionalTx = transactions.stream()
                .filter(tx -> tx.getUser().equals(user) && tx.getBook().equals(book) && !tx.isReturned())
                .findFirst();

        if (optionalTx.isEmpty()) {
            System.out.println("\u26A0\uFE0F No active borrow record found.");
            return;
        }

        BorrowTransaction tx = optionalTx.get();
        LocalDate returnDate = LocalDate.now();
        tx.setReturnDate(returnDate);

        long overdueDays = Math.max(0, ChronoUnit.DAYS.between(tx.getDueDate(), returnDate));

        double fine = 0.0;
        if (applyFine && overdueDays > 0) {
            FineCalculationStrategy strategy = FineCalculationContext.strategyFor(user.getMembershipType());
            FineCalculationContext ctx = new FineCalculationContext();
            ctx.setStrategy(strategy);
            fine = ctx.calculateFine(overdueDays);
            tx.setFinePaid(fine);

            String msg = "Book '" + book.getTitle() + "' is overdue by " + overdueDays + " days. Fine: LKR " + fine;
            NotificationEvent event = new NotificationEvent(NotificationEventType.OVERDUE, msg, user, book);
            notificationService.notifyObservers(event);
        }

        try {
            book.getState().returnBook(book, user);
        } catch (IllegalStateException e) {
            System.out.println("\u26A0\uFE0F " + e.getMessage());
        }

        java.util.Optional<Reservation> nextRes = reservations.stream()
                .filter(r -> r.getBook().equals(book) && r.isActive())
                .sorted(java.util.Comparator.comparing(Reservation::getReservedDate))
                .findFirst();

        nextRes.ifPresent(reservation -> {
            String msg = "Reserved book '" + book.getTitle() + "' is now available for you.";
            NotificationEvent event = new NotificationEvent(NotificationEventType.RESERVATION_AVAILABLE, msg, reservation.getUser(), book);
            notificationService.notifyObservers(event);
        });

        System.out.println("\u2705 Book returned. Fine paid: LKR " + fine);
    }

    public void reserveBook(String userId, String bookId) {
        User user = getUser(userId);
        Book book = getBook(bookId);
        if (user == null || book == null) {
            System.out.println("\u274C Invalid user or book ID.");
            return;
        }

        if (book.getState() instanceof AvailableState) {
            System.out.println("\u26A0\uFE0F Book is available; you can borrow it instead of reserving.");
            return;
        }

        Reservation reservation = new Reservation(java.util.UUID.randomUUID().toString(), book, user, LocalDate.now());
        reservations.add(reservation);
        book.addReservation(reservation);

        try {
            book.getState().reserve(book, user);
        } catch (IllegalStateException e) {
            System.out.println("\u26A0\uFE0F " + e.getMessage());
        }

        System.out.println("\u2705 Book reserved successfully.");
    }

    public void cancelReservation(String userId, String bookId) {
        User user = getUser(userId);
        Book book = getBook(bookId);
        if (user == null || book == null) {
            System.out.println("\u274C Invalid user or book ID.");
            return;
        }

        java.util.Optional<Reservation> optionalRes = reservations.stream()
                .filter(r -> r.getUser().equals(user) && r.getBook().equals(book) && r.isActive())
                .findFirst();

        if (optionalRes.isEmpty()) {
            System.out.println("\u26A0\uFE0F No active reservation found.");
            return;
        }

        Reservation res = optionalRes.get();
        res.setActive(false);
        book.removeReservation(res);

        if (!book.hasActiveReservations() && book.getState() instanceof edu.esu.kandy.slms.state.ReservedState) {
            book.setState(new AvailableState());
        }

        System.out.println("\u2705 Reservation cancelled.");
    }

    public void scanDueAndOverdue() {
        LocalDate today = LocalDate.now();

        for (BorrowTransaction tx : transactions) {
            if (tx.isReturned()) continue;

            long daysToDue = ChronoUnit.DAYS.between(today, tx.getDueDate());
            long overdueDays = ChronoUnit.DAYS.between(tx.getDueDate(), today);

            if (daysToDue == 1) {
                String msg = "Book '" + tx.getBook().getTitle() + "' is due tomorrow (" + tx.getDueDate() + ").";
                NotificationEvent event = new NotificationEvent(NotificationEventType.DUE_SOON, msg, tx.getUser(), tx.getBook());
                notificationService.notifyObservers(event);
            } else if (overdueDays > 0) {
                String msg = "Book '" + tx.getBook().getTitle() + "' is overdue by " + overdueDays + " days.";
                NotificationEvent event = new NotificationEvent(NotificationEventType.OVERDUE, msg, tx.getUser(), tx.getBook());
                notificationService.notifyObservers(event);
            }
        }
    }

    // Reports
    public java.util.List<Book> getMostBorrowedBooks(int topN) {
        java.util.Map<Book, Long> counts = transactions.stream()
                .collect(Collectors.groupingBy(BorrowTransaction::getBook, Collectors.counting()));

        return counts.entrySet().stream()
                .sorted(java.util.Map.Entry.<Book, Long>comparingByValue().reversed())
                .limit(topN)
                .map(java.util.Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public java.util.List<User> getActiveBorrowers() {
        return transactions.stream()
                .filter(tx -> !tx.isReturned())
                .map(BorrowTransaction::getUser)
                .distinct()
                .collect(Collectors.toList());
    }

    public java.util.List<BorrowTransaction> getOverdueTransactions() {
        LocalDate today = LocalDate.now();
        return transactions.stream()
                .filter(tx -> !tx.isReturned() && tx.getDueDate().isBefore(today))
                .collect(Collectors.toList());
    }
}
