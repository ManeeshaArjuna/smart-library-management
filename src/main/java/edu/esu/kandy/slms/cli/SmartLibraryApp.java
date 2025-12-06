package edu.esu.kandy.slms.cli;

import edu.esu.kandy.slms.command.*;
import edu.esu.kandy.slms.decorator.*;
import edu.esu.kandy.slms.membership.MembershipType;
import edu.esu.kandy.slms.model.Book;
import edu.esu.kandy.slms.model.User;
import edu.esu.kandy.slms.observer.NotificationService;
import edu.esu.kandy.slms.report.ReportService;
import edu.esu.kandy.slms.service.LibraryService;
import edu.esu.kandy.slms.util.ConsoleColors;

import java.util.Scanner;

public class SmartLibraryApp {

    public static void main(String[] args) {
        NotificationService notificationService = new NotificationService();
        LibraryService libraryService = new LibraryService(notificationService);
        libraryService.seedSampleData();

        ReportService reportService = new ReportService(libraryService);
        CommandHistory commandHistory = new CommandHistory();
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
                case "10" -> librarianBookMenu(scanner, libraryService);
                case "11" -> librarianUserMenu(scanner, libraryService);
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
                "10. \uD83D\uDEE0 Librarian - Manage Books\n" +
                "11. \uD83E\uDDD1\u200D\uD83D\uDCBC Librarian - Manage Users\n" +
                "0. \uD83D\uDEAA Exit\n"
                + ConsoleColors.RESET);
    }

    private static void listBooks(LibraryService libraryService) {
        System.out.println("\uD83D\uDCDA Books:");
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
            System.out.println(" - [" + book.getId() + "] " + view.getDisplayTitle()
                    + " | State: " + book.getState().getName());
        }
    }

    private static void listUsers(LibraryService libraryService) {
        System.out.println("\uD83D\uDC64 Users:");
        for (User user : libraryService.getAllUsers()) {
            System.out.println(" - [" + user.getId() + "] " + user.getName()
                    + " (" + user.getMembershipType() + ")");
        }
    }

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
                System.out.print("Book ID: ");
                String id = scanner.nextLine().trim();
                System.out.print("Title: ");
                String title = scanner.nextLine().trim();
                System.out.print("Author: ");
                String author = scanner.nextLine().trim();
                System.out.print("Category: ");
                String category = scanner.nextLine().trim();
                System.out.print("ISBN: ");
                String isbn = scanner.nextLine().trim();
                Book book = new Book.Builder(id, title, author, category, isbn).build();
                libraryService.addBook(book);
                System.out.println("\u2705 Book added.");
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
                System.out.print("User ID: ");
                String id = scanner.nextLine().trim();
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
                User user = new User(id, name, email, contact, type);
                libraryService.addUser(user);
                System.out.println("\u2705 User added.");
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
}
