package edu.esu.kandy.slms.cli;

import edu.esu.kandy.slms.auth.LibrarianAuthService;
import edu.esu.kandy.slms.auth.UserAuthService;
import edu.esu.kandy.slms.command.*;
import edu.esu.kandy.slms.decorator.*;
import edu.esu.kandy.slms.membership.MembershipType;
import edu.esu.kandy.slms.model.Book;
import edu.esu.kandy.slms.model.BorrowTransaction;
import edu.esu.kandy.slms.model.User;
import edu.esu.kandy.slms.observer.NotificationService;
import edu.esu.kandy.slms.report.ReportService;
import edu.esu.kandy.slms.service.LibraryService;
import edu.esu.kandy.slms.util.ConsoleColors;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class SmartLibraryApp {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void main(String[] args) {
        NotificationService notificationService = new NotificationService();
        LibraryService libraryService = new LibraryService(notificationService);
        libraryService.seedSampleData();

        ReportService reportService = new ReportService(libraryService);
        CommandHistory commandHistory = new CommandHistory();
        LibrarianAuthService librarianAuthService = new LibrarianAuthService();
        UserAuthService userAuthService = new UserAuthService(libraryService);

        Scanner scanner = new Scanner(System.in);

        boolean exit = false;
        while (!exit) {
            printWelcomeScreen();
            String option = readInput(scanner, ConsoleColors.CYAN + "Enter option: " + ConsoleColors.RESET);
            if (option == null) {
                // '#' pressed on welcome screen: just re-draw the screen
                continue;
            }

            switch (option) {
                case "1" -> handleUserLogin(scanner, libraryService, reportService, commandHistory, userAuthService);
                case "2" -> handleUserSignup(scanner, libraryService, reportService, commandHistory, userAuthService);
                case "3" -> handleAdminLogin(scanner, libraryService, reportService, commandHistory, librarianAuthService);
                case "0" -> {
                    System.out.println(ConsoleColors.GREEN +
                            "\uD83D\uDC4B Exiting Smart Library Management System. Bye!" +
                            ConsoleColors.RESET);
                    exit = true;
                }
                default -> System.out.println(ConsoleColors.RED + "Invalid option." + ConsoleColors.RESET);
            }
        }

        scanner.close();
    }

    // ========= High-level screens =========

    private static void printWelcomeScreen() {
        System.out.println(ConsoleColors.BLUE + ConsoleColors.BOLD +
                "==========================================" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.BLUE + ConsoleColors.BOLD +
                "   \uD83D\uDCDA ESU Kandy - Smart Library System   " + ConsoleColors.RESET);
        System.out.println(ConsoleColors.BLUE + ConsoleColors.BOLD +
                "==========================================" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.YELLOW +
                "1. \uD83D\uDD10 Login\n" +
                "2. \u2795 Sign Up\n" +
                "3. \uD83D\uDD11 Admin Login\n" +
                "0. \uD83D\uDEAA Exit\n" +
                ConsoleColors.RESET);
        System.out.println(ConsoleColors.PURPLE +
                "(Hint: type '#' at any prompt to go back to the main menu.)" +
                ConsoleColors.RESET);
    }

    private static void runMainMenu(Scanner scanner,
                                    LibraryService libraryService,
                                    ReportService reportService,
                                    CommandHistory commandHistory,
                                    User currentUser,
                                    boolean isAdmin) {

        boolean running = true;

        while (running) {
            printHeader();
            printMainMenu(isAdmin);

            String option = readInput(scanner, ConsoleColors.CYAN + "Enter option: " + ConsoleColors.RESET);
            if (option == null) {
                // '#' at this level just re-draws the main menu
                continue;
            }

            switch (option) {
                case "1" -> listBooks(libraryService);
                case "2" -> listUsers(libraryService);
                case "3" -> borrowFlow(scanner, libraryService, commandHistory, currentUser, isAdmin);
                case "4" -> returnFlow(scanner, libraryService, commandHistory, currentUser, isAdmin);
                case "5" -> reserveFlow(scanner, libraryService, commandHistory, currentUser, isAdmin);
                case "6" -> cancelReservationFlow(scanner, libraryService, commandHistory, currentUser, isAdmin);
                case "7" -> commandHistory.undoLast();
                case "8" -> {
                    libraryService.scanDueAndOverdue();
                    System.out.println("\u2705 Notification scan completed.");
                }
                case "9" -> {
                    if (isAdmin) {
                        reportMenu(scanner, reportService);
                    } else {
                        System.out.println(ConsoleColors.RED + "Reports are available to administrators only." + ConsoleColors.RESET);
                    }
                }
                case "10" -> {
                    if (isAdmin) {
                        administrationMenu(scanner, libraryService);
                    } else {
                        System.out.println(ConsoleColors.RED + "Administration is available to administrators only." + ConsoleColors.RESET);
                    }
                }
                case "11" -> historyMenu(scanner, libraryService);
                case "0" -> running = false;
                default -> System.out.println(ConsoleColors.RED + "Invalid option." + ConsoleColors.RESET);
            }

            if (running) {
                System.out.println(ConsoleColors.PURPLE + "\nPress Enter to continue..." + ConsoleColors.RESET);
                scanner.nextLine();
            }
        }
    }

    private static void printHeader() {
        System.out.println(ConsoleColors.BLUE + ConsoleColors.BOLD +
                "==========================================" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.BLUE + ConsoleColors.BOLD +
                "   \uD83D\uDCDA ESU Kandy - Smart Library System   " + ConsoleColors.RESET);
        System.out.println(ConsoleColors.BLUE + ConsoleColors.BOLD +
                "==========================================" + ConsoleColors.RESET);
    }

    private static void printMainMenu(boolean isAdmin) {
        if (isAdmin) {
            System.out.println(ConsoleColors.YELLOW +
                    "1. \uD83D\uDCD6 List Books\n" +
                    "2. \uD83D\uDC64 List Users\n" +
                    "3. \u2795 Borrow Book\n" +
                    "4. \uD83D\uDD01 Return Book\n" +
                    "5. \uD83D\uDCCC Reserve Book\n" +
                    "6. \u274C Cancel Reservation\n" +
                    "7. \u21A9\uFE0F Undo Last Action\n" +
                    "8. \uD83D\uDD14 Run Due/Overdue Notifications\n" +
                    "9. \uD83D\uDCCA View Reports\n" +
                    "10. \uD83D\uDD10 Administration\n" +
                    "11. \uD83D\uDCDC View Borrow History\n" +
                    "0. \uD83D\uDEAA Exit\n"
                    + ConsoleColors.RESET);
        } else {
            // normal user view as requested
            System.out.println(ConsoleColors.YELLOW +
                    "1. \uD83D\uDCD6 List Books\n" +
                    "2. \uD83D\uDC64 List Users\n" +
                    "3. \u2795 Borrow Book\n" +
                    "4. \uD83D\uDD01 Return Book\n" +
                    "5. \uD83D\uDCCC Reserve Book\n" +
                    "6. \u274C Cancel Reservation\n" +
                    "7. \u21A9\uFE0F Undo Last Action\n" +
                    "8. \uD83D\uDD14 Run Due/Overdue Notifications\n" +
                    "11. \uD83D\uDCDC View Borrow History\n" +
                    "0. \uD83D\uDEAA Exit\n"
                    + ConsoleColors.RESET);
        }
    }

    // ========= Helper for reading input with '#' support =========

    /**
     * Reads a line from the scanner. If the user types exactly "#" it returns null,
     * indicating that the caller should go back to the previous/main menu.
     */
    private static String readInput(Scanner scanner, String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        if ("#".equals(input)) {
            return null;
        }
        return input;
    }

    private static String normalizeId(String id) {
        return id == null ? null : id.trim().toUpperCase();
    }

    private static MembershipType selectMembershipType(Scanner scanner) {
        while (true) {
            String choice = readInput(scanner,
                    "Membership Type (1. STUDENT | 2. FACULTY | 3. GUEST, '#' to main menu): ");
            if (choice == null) {
                return null; // caller will interpret as cancel
            }
            switch (choice) {
                case "1":
                    return MembershipType.STUDENT;
                case "2":
                    return MembershipType.FACULTY;
                case "3":
                    return MembershipType.GUEST;
                default:
                    System.out.println(ConsoleColors.RED + "Invalid membership selection, please try again." + ConsoleColors.RESET);
            }
        }
    }

    private static String readValidatedName(Scanner scanner, String prompt) {
        while (true) {
            String value = readInput(scanner, prompt); // supports '#'
            if (value == null) {
                return null; // '#' pressed
            }
            if (value.isBlank()) {
                System.out.println(ConsoleColors.RED + "Name cannot be empty." + ConsoleColors.RESET);
                continue;
            }
            // letters and spaces only
            if (!value.matches("[A-Za-z ]+")) {
                System.out.println(ConsoleColors.RED + "Name should contain only letters and spaces." + ConsoleColors.RESET);
                continue;
            }
            return value;
        }
    }

    private static String readValidatedEmail(Scanner scanner, String prompt) {
        while (true) {
            String value = readInput(scanner, prompt); // supports '#'
            if (value == null) {
                return null;
            }
            // very simple email pattern: something@something.domain
            if (!value.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                System.out.println(ConsoleColors.RED + "Please enter a valid email address (e.g. user@example.com)." + ConsoleColors.RESET);
                continue;
            }
            return value;
        }
    }

    private static String readValidatedContact(Scanner scanner, String prompt) {
        while (true) {
            String value = readInput(scanner, prompt); // supports '#'
            if (value == null) {
                return null;
            }
            // exactly 10 digits
            if (!value.matches("\\d{10}")) {
                System.out.println(ConsoleColors.RED + "Contact number must be exactly 10 digits (numbers only)." + ConsoleColors.RESET);
                continue;
            }
            return value;
        }
    }

    private static String truncate(String value, int maxLen) {
        if (value == null) return "";
        if (value.length() <= maxLen) return value;
        return value.substring(0, maxLen - 3) + "...";
    }

    // ========= Login / Signup handlers =========

    private static void handleUserLogin(Scanner scanner,
                                        LibraryService libraryService,
                                        ReportService reportService,
                                        CommandHistory commandHistory,
                                        UserAuthService userAuthService) {

        System.out.println(ConsoleColors.YELLOW + "--- User Login ---" + ConsoleColors.RESET);
        String userId = readInput(scanner, "User ID: ");
        if (userId == null) {
            return;
        }
        String password = readInput(scanner, "Password: ");
        if (password == null) {
            return;
        }

        User user = userAuthService.login(userId, password);
        if (user == null) {
            System.out.println(ConsoleColors.RED + "\u274C Invalid user ID or password." + ConsoleColors.RESET);
            return;
        }

        System.out.println(ConsoleColors.GREEN + "\u2705 Login successful. Welcome, " + user.getName() + "!" + ConsoleColors.RESET);
        runMainMenu(scanner, libraryService, reportService, commandHistory, user, false);
    }

    private static void handleUserSignup(Scanner scanner,
                                         LibraryService libraryService,
                                         ReportService reportService,
                                         CommandHistory commandHistory,
                                         UserAuthService userAuthService) {

        System.out.println(ConsoleColors.YELLOW + "--- User Sign Up ---" + ConsoleColors.RESET);

        String name = readValidatedName(scanner, "Name: ");
        if (name == null) return;

        String email = readValidatedEmail(scanner, "Email: ");
        if (email == null) return;

        String contact = readValidatedContact(scanner, "Contact Number (10 digits): ");
        if (contact == null) return;

        MembershipType membershipType = selectMembershipType(scanner);
        if (membershipType == null) {
            // '#' pressed; go back to welcome screen
            return;
        }

        String password = readInput(scanner, "Create Password: ");
        if (password == null) return;

        // rest of your existing code stays the same
        User created;
        try {
            created = userAuthService.signUp(name, email, contact, membershipType, password);
        } catch (IllegalArgumentException e) {
            System.out.println(ConsoleColors.RED + "\u274C " + e.getMessage() + ConsoleColors.RESET);
            return;
        }

        System.out.println(ConsoleColors.GREEN +
                "\u2705 Sign up successful. Your User ID is: " + created.getId() +
                "  (keep this for login)" +
                ConsoleColors.RESET);

        runMainMenu(scanner, libraryService, reportService, commandHistory, created, false);
    }

    private static void handleAdminLogin(Scanner scanner,
                                         LibraryService libraryService,
                                         ReportService reportService,
                                         CommandHistory commandHistory,
                                         LibrarianAuthService librarianAuthService) {

        System.out.println(ConsoleColors.YELLOW + "--- Admin Login ---" + ConsoleColors.RESET);
        String username = readInput(scanner, "Username: ");
        if (username == null) return;
        String password = readInput(scanner, "Password: ");
        if (password == null) return;

        if (!librarianAuthService.authenticate(username, password)) {
            System.out.println(ConsoleColors.RED + "\u274C Invalid admin credentials." + ConsoleColors.RESET);
            return;
        }

        System.out.println(ConsoleColors.GREEN + "\u2705 Admin login successful." + ConsoleColors.RESET);
        // Admin doesn't map to a specific User object; pass null and isAdmin = true
        runMainMenu(scanner, libraryService, reportService, commandHistory, null, true);
    }

    // ========= Listing with table view =========

    private static void listBooks(LibraryService libraryService) {
        System.out.println("\uD83D\uDCDA Books:");
        System.out.println("----------------------------------------------------------------------------------------------");
        System.out.printf("%-8s %-30s %-20s %-15s %-15s %-10s%n",
                "ID", "Title", "Author", "Category", "ISBN", "State");
        System.out.println("----------------------------------------------------------------------------------------------");
        for (Book book : libraryService.getAllBooks()) {
            BookView view = new BasicBookView(book);
            if (book.isFeatured()) {
                view = new FeaturedBookDecorator(view);
            }
            if (book.isRecommended()) {
                view = new RecommendedBookDecorator(view);
            }
            if (book.isSpecialEdition()) {
                view = new SpecialEditionBookDecorator(view);
            }
            System.out.printf("%-8s %-30s %-20s %-15s %-15s %-10s%n",
                    book.getId(),
                    truncate(book.getTitle(), 30),
                    truncate(book.getAuthor(), 20),
                    truncate(book.getCategory(), 15),
                    truncate(book.getIsbn(), 15),
                    book.getState().getName());
        }
        System.out.println("----------------------------------------------------------------------------------------------");
    }

    private static void listUsers(LibraryService libraryService) {
        System.out.println("\uD83D\uDC64 Users:");
        System.out.println("----------------------------------------------------------------------------------------------");
        System.out.printf("%-8s %-20s %-25s %-15s %-12s%n",
                "ID", "Name", "Email", "Contact", "Membership");
        System.out.println("----------------------------------------------------------------------------------------------");
        for (User user : libraryService.getAllUsers()) {
            System.out.printf("%-8s %-20s %-25s %-15s %-12s%n",
                    user.getId(),
                    truncate(user.getName(), 20),
                    truncate(user.getEmail(), 25),
                    truncate(user.getContactNumber(), 15),
                    user.getMembershipType());
        }
        System.out.println("----------------------------------------------------------------------------------------------");
    }

    // ========= Core flows (case-insensitive IDs + current user support) =========

    private static void borrowFlow(Scanner scanner,
                                   LibraryService libraryService,
                                   CommandHistory history,
                                   User currentUser,
                                   boolean isAdmin) {

        String uid;
        if (!isAdmin && currentUser != null) {
            uid = currentUser.getId();
            System.out.println("Using your User ID: " + uid);
        } else {
            String input = readInput(scanner, "Enter User ID: ");
            if (input == null) return;
            uid = normalizeId(input);
        }

        String bidInput = readInput(scanner, "Enter Book ID: ");
        if (bidInput == null) return;
        String bid = normalizeId(bidInput);

        Command cmd = new BorrowBookCommand(libraryService, uid, bid);
        cmd.execute();
        history.push(cmd);
    }

    private static void returnFlow(Scanner scanner,
                                   LibraryService libraryService,
                                   CommandHistory history,
                                   User currentUser,
                                   boolean isAdmin) {

        String uid;
        if (!isAdmin && currentUser != null) {
            uid = currentUser.getId();
            System.out.println("Using your User ID: " + uid);
        } else {
            String input = readInput(scanner, "Enter User ID: ");
            if (input == null) return;
            uid = normalizeId(input);
        }

        String bidInput = readInput(scanner, "Enter Book ID: ");
        if (bidInput == null) return;
        String bid = normalizeId(bidInput);

        Command cmd = new ReturnBookCommand(libraryService, uid, bid);
        cmd.execute();
        history.push(cmd);
    }

    private static void reserveFlow(Scanner scanner,
                                    LibraryService libraryService,
                                    CommandHistory history,
                                    User currentUser,
                                    boolean isAdmin) {

        String uid;
        if (!isAdmin && currentUser != null) {
            uid = currentUser.getId();
            System.out.println("Using your User ID: " + uid);
        } else {
            String input = readInput(scanner, "Enter User ID: ");
            if (input == null) return;
            uid = normalizeId(input);
        }

        String bidInput = readInput(scanner, "Enter Book ID: ");
        if (bidInput == null) return;
        String bid = normalizeId(bidInput);

        Command cmd = new ReserveBookCommand(libraryService, uid, bid);
        cmd.execute();
        history.push(cmd);
    }

    private static void cancelReservationFlow(Scanner scanner,
                                              LibraryService libraryService,
                                              CommandHistory history,
                                              User currentUser,
                                              boolean isAdmin) {

        String uid;
        if (!isAdmin && currentUser != null) {
            uid = currentUser.getId();
            System.out.println("Using your User ID: " + uid);
        } else {
            String input = readInput(scanner, "Enter User ID: ");
            if (input == null) return;
            uid = normalizeId(input);
        }

        String bidInput = readInput(scanner, "Enter Book ID: ");
        if (bidInput == null) return;
        String bid = normalizeId(bidInput);

        Command cmd = new CancelReservationCommand(libraryService, uid, bid);
        cmd.execute();
        history.push(cmd);
    }

    // ========= Reports =========

    private static void reportMenu(Scanner scanner, ReportService reportService) {
        System.out.println(ConsoleColors.YELLOW +
                "--- Reports ---\n" +
                "1. Most Borrowed Books\n" +
                "2. Active Borrowers\n" +
                "3. Overdue Books\n" +
                "0. Back\n" + ConsoleColors.RESET);
        String choice = readInput(scanner, "Choose: ");
        if (choice == null) {
            return;
        }
        switch (choice) {
            case "1" -> reportService.printMostBorrowedBooks(5);
            case "2" -> reportService.printActiveBorrowers();
            case "3" -> reportService.printOverdueBooks();
            default -> System.out.println("Returning to main menu...");
        }
    }

    // ========= Administration & Librarian menus =========

    private static void administrationMenu(Scanner scanner, LibraryService libraryService) {
        boolean inAdmin = true;
        while (inAdmin) {
            System.out.println(ConsoleColors.YELLOW +
                    "--- Administration ---\n" +
                    "1. Manage Books\n" +
                    "2. Manage Users\n" +
                    "0. Back\n" + ConsoleColors.RESET);
            String choice = readInput(scanner, "Choose: ");
            if (choice == null) {
                // '#' => back to main menu
                return;
            }
            switch (choice) {
                case "1" -> librarianBookMenu(scanner, libraryService);
                case "2" -> librarianUserMenu(scanner, libraryService);
                case "0" -> inAdmin = false;
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private static void librarianBookMenu(Scanner scanner, LibraryService libraryService) {
        System.out.println(ConsoleColors.YELLOW +
                "--- Librarian - Book Management ---\n" +
                "1. Add Book\n" +
                "2. Update Book\n" +
                "3. Remove Book\n" +
                "0. Back\n" + ConsoleColors.RESET);
        String choice = readInput(scanner, "Choose: ");
        if (choice == null) {
            return;
        }
        switch (choice) {
            case "1" -> {
                String title = readInput(scanner, "Title: ");
                if (title == null) return;
                String author = readInput(scanner, "Author: ");
                if (author == null) return;
                String category = readInput(scanner, "Category: ");
                if (category == null) return;
                String isbn = readInput(scanner, "ISBN: ");
                if (isbn == null) return;
                Book book = libraryService.createAndAddBook(title, author, category, isbn);
                System.out.println("\u2705 Book added with ID: " + book.getId());
            }
            case "2" -> {
                String id = readInput(scanner, "Book ID to update: ");
                if (id == null) return;
                id = normalizeId(id);
                Book existing = libraryService.getBook(id);
                if (existing == null) {
                    System.out.println("\u274C Book not found.");
                    return;
                }
                String title = readInput(scanner, "New title (blank to keep '" + existing.getTitle() + "'): ");
                if (title == null) return;
                String author = readInput(scanner, "New author (blank to keep '" + existing.getAuthor() + "'): ");
                if (author == null) return;
                String category = readInput(scanner, "New category (blank to keep '" + existing.getCategory() + "'): ");
                if (category == null) return;
                String isbn = readInput(scanner, "New ISBN (blank to keep '" + existing.getIsbn() + "'): ");
                if (isbn == null) return;
                libraryService.updateBookDetails(id, title, author, category, isbn);
                System.out.println("\u2705 Book updated.");
            }
            case "3" -> {
                String id = readInput(scanner, "Book ID to remove: ");
                if (id == null) return;
                id = normalizeId(id);
                libraryService.removeBook(id);
                System.out.println("\u2705 Book removed (if it existed).");
            }
            default -> System.out.println("Returning...");
        }
    }

    private static void librarianUserMenu(Scanner scanner, LibraryService libraryService) {
        System.out.println(ConsoleColors.YELLOW +
                "--- Librarian - User Management ---\n" +
                "1. Add User\n" +
                "2. Remove User\n" +
                "0. Back\n" + ConsoleColors.RESET);
        String choice = readInput(scanner, "Choose: ");
        if (choice == null) {
            return;
        }
        switch (choice) {
            case "1" -> {
                String name = readValidatedName(scanner, "Name: ");
                if (name == null) return;

                String email = readValidatedEmail(scanner, "Email: ");
                if (email == null) return;

                String contact = readValidatedContact(scanner, "Contact Number (10 digits): ");
                if (contact == null) return;

                MembershipType type = selectMembershipType(scanner);
                if (type == null) return;

                User user = libraryService.createAndAddUser(name, email, contact, type);
                System.out.println("\u2705 User added with ID: " + user.getId());
            }
            case "2" -> {
                String id = readInput(scanner, "User ID to remove: ");
                if (id == null) return;
                id = normalizeId(id);
                libraryService.removeUser(id);
                System.out.println("\u2705 User removed (if existed).");
            }
            default -> System.out.println("Returning...");
        }
    }

    // ========= Borrow history =========

    private static void historyMenu(Scanner scanner, LibraryService libraryService) {
        System.out.println(ConsoleColors.YELLOW +
                "--- Borrow History ---\n" +
                "1. View by User ID\n" +
                "2. View by Book ID\n" +
                "0. Back\n" + ConsoleColors.RESET);
        String choice = readInput(scanner, "Choose: ");
        if (choice == null) {
            return;
        }
        switch (choice) {
            case "1" -> {
                String uidInput = readInput(scanner, "Enter User ID: ");
                if (uidInput == null) return;
                String uid = normalizeId(uidInput);
                User user = libraryService.getUser(uid);
                if (user == null) {
                    System.out.println("\u274C User not found.");
                    return;
                }
                printHistoryForUser(user);
            }
            case "2" -> {
                String bidInput = readInput(scanner, "Enter Book ID: ");
                if (bidInput == null) return;
                String bid = normalizeId(bidInput);
                Book book = libraryService.getBook(bid);
                if (book == null) {
                    System.out.println("\u274C Book not found.");
                    return;
                }
                printHistoryForBook(book);
            }
            default -> System.out.println("Returning...");
        }
    }

    private static void printHistoryForUser(User user) {
        List<BorrowTransaction> history = user.getBorrowHistory();
        System.out.println("Borrow history for user: " + user.getName() + " [" + user.getId() + "]");
        if (history.isEmpty()) {
            System.out.println(" (no borrow records)");
            return;
        }
        System.out.println("-----------------------------------------------------------------------------------------------------------");
        System.out.printf("%-10s %-8s %-30s %-12s %-12s %-12s %-8s%n",
                "TxID", "BookID", "Title", "Borrow", "Due", "Return", "Fine");
        System.out.println("-----------------------------------------------------------------------------------------------------------");
        for (BorrowTransaction tx : history) {
            String txIdShort = tx.getId().length() > 8 ? tx.getId().substring(0, 8) : tx.getId();
            String borrow = tx.getBorrowDate() != null ? DATE_FMT.format(tx.getBorrowDate()) : "";
            String due = tx.getDueDate() != null ? DATE_FMT.format(tx.getDueDate()) : "";
            String ret = tx.getReturnDate() != null ? DATE_FMT.format(tx.getReturnDate()) : "-";
            System.out.printf("%-10s %-8s %-30s %-12s %-12s %-12s %-8.2f%n",
                    txIdShort,
                    tx.getBook().getId(),
                    truncate(tx.getBook().getTitle(), 30),
                    borrow,
                    due,
                    ret,
                    tx.getFinePaid());
        }
        System.out.println("-----------------------------------------------------------------------------------------------------------");
    }

    private static void printHistoryForBook(Book book) {
        List<BorrowTransaction> history = book.getBorrowHistory();
        System.out.println("Borrow history for book: " + book.getTitle() + " [" + book.getId() + "]");
        if (history.isEmpty()) {
            System.out.println(" (no borrow records)");
            return;
        }
        System.out.println("-----------------------------------------------------------------------------------------------------------");
        System.out.printf("%-10s %-8s %-20s %-12s %-12s %-12s %-8s%n",
                "TxID", "UserID", "UserName", "Borrow", "Due", "Return", "Fine");
        System.out.println("-----------------------------------------------------------------------------------------------------------");
        for (BorrowTransaction tx : history) {
            String txIdShort = tx.getId().length() > 8 ? tx.getId().substring(0, 8) : tx.getId();
            String borrow = tx.getBorrowDate() != null ? DATE_FMT.format(tx.getBorrowDate()) : "";
            String due = tx.getDueDate() != null ? DATE_FMT.format(tx.getDueDate()) : "";
            String ret = tx.getReturnDate() != null ? DATE_FMT.format(tx.getReturnDate()) : "-";
            System.out.printf("%-10s %-8s %-20s %-12s %-12s %-12s %-8.2f%n",
                    txIdShort,
                    tx.getUser().getId(),
                    truncate(tx.getUser().getName(), 20),
                    borrow,
                    due,
                    ret,
                    tx.getFinePaid());
        }
        System.out.println("-----------------------------------------------------------------------------------------------------------");
    }
}
