package edu.esu.kandy.slms.cli;

import edu.esu.kandy.slms.auth.LibrarianAuthService;
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
        LibrarianAuthService authService = new LibrarianAuthService();
        Scanner scanner = new Scanner(System.in);

        boolean running = true;
        while (running) {
            printHeader();
            printMenu();
            System.out.print(ConsoleColors.CYAN + "Enter option: " + ConsoleColors.RESET);
            String option = scanner.nextLine().trim();

            switch (option) {
                case "1" -> listBooks(libraryService);
                case "2" -> listUsers(libraryService);
                case "3" -> borrowFlow(scanner, libraryService, commandHistory);
                case "4" -> returnFlow(scanner, libraryService, commandHistory);
                case "5" -> reserveFlow(scanner, libraryService, commandHistory);
                case "6" -> cancelReservationFlow(scanner, libraryService, commandHistory);
                case "7" -> commandHistory.undoLast();
                case "8" -> {
                    libraryService.scanDueAndOverdue();
                    System.out.println("\u2705 Notification scan completed.");
                }
                case "9" -> reportMenu(scanner, reportService);
                case "10" -> administrationMenu(scanner, libraryService, authService);
                case "11" -> historyMenu(scanner, libraryService);
                case "0" -> {
                    System.out.println(ConsoleColors.GREEN +
                            "\uD83D\uDC4B Exiting Smart Library Management System. Bye!" +
                            ConsoleColors.RESET);
                    running = false;
                }
                default -> System.out.println(ConsoleColors.RED + "Invalid option." + ConsoleColors.RESET);
            }

            if (running) {
                System.out.println(ConsoleColors.PURPLE + "\nPress Enter to continue..." + ConsoleColors.RESET);
                scanner.nextLine();
            }
        }
        scanner.close();
    }

    private static void printHeader() {
        System.out.println(ConsoleColors.BLUE + ConsoleColors.BOLD +
                "==========================================" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.BLUE + ConsoleColors.BOLD +
                "   \uD83D\uDCDA ESU Kandy - Smart Library System   " + ConsoleColors.RESET);
        System.out.println(ConsoleColors.BLUE + ConsoleColors.BOLD +
                "==========================================" + ConsoleColors.RESET);
    }

    private static void printMenu() {
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
    }

    // ---------- Listing with table view ----------

    private static String truncate(String value, int maxLen) {
        if (value == null) return "";
        if (value.length() <= maxLen) return value;
        return value.substring(0, maxLen - 3) + "...";
    }

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

    // ---------- Core flows ----------

    private static void borrowFlow(Scanner scanner, LibraryService libraryService, CommandHistory history) {
        System.out.print("Enter User ID: ");
        String uid = scanner.nextLine().trim();
        System.out.print("Enter Book ID: ");
        String bid = scanner.nextLine().trim();
        Command cmd = new BorrowBookCommand(libraryService, uid, bid);
        cmd.execute();
        history.push(cmd);
    }

    private static void returnFlow(Scanner scanner, LibraryService libraryService, CommandHistory history) {
        System.out.print("Enter User ID: ");
        String uid = scanner.nextLine().trim();
        System.out.print("Enter Book ID: ");
        String bid = scanner.nextLine().trim();
        Command cmd = new ReturnBookCommand(libraryService, uid, bid);
        cmd.execute();
        history.push(cmd);
    }

    private static void reserveFlow(Scanner scanner, LibraryService libraryService, CommandHistory history) {
        System.out.print("Enter User ID: ");
        String uid = scanner.nextLine().trim();
        System.out.print("Enter Book ID: ");
        String bid = scanner.nextLine().trim();
        Command cmd = new ReserveBookCommand(libraryService, uid, bid);
        cmd.execute();
        history.push(cmd);
    }

    private static void cancelReservationFlow(Scanner scanner, LibraryService libraryService, CommandHistory history) {
        System.out.print("Enter User ID: ");
        String uid = scanner.nextLine().trim();
        System.out.print("Enter Book ID: ");
        String bid = scanner.nextLine().trim();
        Command cmd = new CancelReservationCommand(libraryService, uid, bid);
        cmd.execute();
        history.push(cmd);
    }

    // ---------- Reports ----------

    private static void reportMenu(Scanner scanner, ReportService reportService) {
        System.out.println(ConsoleColors.YELLOW +
                "--- Reports ---\n" +
                "1. Most Borrowed Books\n" +
                "2. Active Borrowers\n" +
                "3. Overdue Books\n" +
                "0. Back\n" + ConsoleColors.RESET);
        System.out.print("Choose: ");
        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1" -> reportService.printMostBorrowedBooks(5);
            case "2" -> reportService.printActiveBorrowers();
            case "3" -> reportService.printOverdueBooks();
            default -> System.out.println("Returning to main menu...");
        }
    }

    // ---------- Administration & Authentication ----------

    private static void administrationMenu(Scanner scanner, LibraryService libraryService, LibrarianAuthService authService) {
        System.out.println(ConsoleColors.BLUE + "--- Administration Login ---" + ConsoleColors.RESET);
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        if (!authService.authenticate(username, password)) {
            System.out.println(ConsoleColors.RED + "\u274C Invalid credentials. Access denied." + ConsoleColors.RESET);
            return;
        }

        boolean inAdmin = true;
        while (inAdmin) {
            System.out.println(ConsoleColors.YELLOW +
                    "--- Administration ---\n" +
                    "1. Manage Books\n" +
                    "2. Manage Users\n" +
                    "0. Back\n" + ConsoleColors.RESET);
            System.out.print("Choose: ");
            String choice = scanner.nextLine().trim();
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
        System.out.print("Choose: ");
        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1" -> {
                System.out.print("Title: ");
                String title = scanner.nextLine().trim();
                System.out.print("Author: ");
                String author = scanner.nextLine().trim();
                System.out.print("Category: ");
                String category = scanner.nextLine().trim();
                System.out.print("ISBN: ");
                String isbn = scanner.nextLine().trim();
                Book book = libraryService.createAndAddBook(title, author, category, isbn);
                System.out.println("\u2705 Book added with ID: " + book.getId());
            }
            case "2" -> {
                System.out.print("Book ID to update: ");
                String id = scanner.nextLine().trim();
                Book existing = libraryService.getBook(id);
                if (existing == null) {
                    System.out.println("\u274C Book not found.");
                    return;
                }
                System.out.print("New title (blank to keep '" + existing.getTitle() + "'): ");
                String title = scanner.nextLine();
                System.out.print("New author (blank to keep '" + existing.getAuthor() + "'): ");
                String author = scanner.nextLine();
                System.out.print("New category (blank to keep '" + existing.getCategory() + "'): ");
                String category = scanner.nextLine();
                System.out.print("New ISBN (blank to keep '" + existing.getIsbn() + "'): ");
                String isbn = scanner.nextLine();
                libraryService.updateBookDetails(id, title, author, category, isbn);
                System.out.println("\u2705 Book updated.");
            }
            case "3" -> {
                System.out.print("Book ID to remove: ");
                String id = scanner.nextLine().trim();
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
        System.out.print("Choose: ");
        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1" -> {
                System.out.print("Name: ");
                String name = scanner.nextLine().trim();
                System.out.print("Email: ");
                String email = scanner.nextLine().trim();
                System.out.print("Contact Number: ");
                String contact = scanner.nextLine().trim();
                System.out.print("Membership Type (STUDENT/FACULTY/GUEST): ");
                String memStr = scanner.nextLine().trim().toUpperCase();
                MembershipType type;
                try {
                    type = MembershipType.valueOf(memStr);
                } catch (IllegalArgumentException e) {
                    System.out.println("\u274C Invalid membership type.");
                    return;
                }
                User user = libraryService.createAndAddUser(name, email, contact, type);
                System.out.println("\u2705 User added with ID: " + user.getId());
            }
            case "2" -> {
                System.out.print("User ID to remove: ");
                String id = scanner.nextLine().trim();
                libraryService.removeUser(id);
                System.out.println("\u2705 User removed (if existed).");
            }
            default -> System.out.println("Returning...");
        }
    }

    // ---------- Borrow history ----------

    private static void historyMenu(Scanner scanner, LibraryService libraryService) {
        System.out.println(ConsoleColors.YELLOW +
                "--- Borrow History ---\n" +
                "1. View by User ID\n" +
                "2. View by Book ID\n" +
                "0. Back\n" + ConsoleColors.RESET);
        System.out.print("Choose: ");
        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1" -> {
                System.out.print("Enter User ID: ");
                String uid = scanner.nextLine().trim();
                User user = libraryService.getUser(uid);
                if (user == null) {
                    System.out.println("\u274C User not found.");
                    return;
                }
                printHistoryForUser(user);
            }
            case "2" -> {
                System.out.print("Enter Book ID: ");
                String bid = scanner.nextLine().trim();
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
