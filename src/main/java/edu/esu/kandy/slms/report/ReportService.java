package edu.esu.kandy.slms.report;

import edu.esu.kandy.slms.model.Book;
import edu.esu.kandy.slms.model.BorrowTransaction;
import edu.esu.kandy.slms.model.User;
import edu.esu.kandy.slms.service.LibraryService;

import java.util.List;

public class ReportService {

    private final LibraryService libraryService;

    public ReportService(LibraryService libraryService) {
        this.libraryService = libraryService;
    }
    // Print top N most borrowed books
    public void printMostBorrowedBooks(int topN) {
        List<Book> books = libraryService.getMostBorrowedBooks(topN);
        System.out.println("\uD83D\uDCCA Most Borrowed Books:");
        if (books.isEmpty()) {
            System.out.println(" (none yet)");
        }
        for (Book book : books) {
            System.out.println(" - " + book.getTitle() + " by " + book.getAuthor());
        }
    }
    // Print list of active borrowers
    public void printActiveBorrowers() {
        List<User> users = libraryService.getActiveBorrowers();
        System.out.println("\uD83D\uDC65 Active Borrowers:");
        if (users.isEmpty()) {
            System.out.println(" (none yet)");
        }
        for (User user : users) {
            System.out.println(" - " + user.getName() + " (" + user.getMembershipType() + ")");
        }
    }
    // Print list of overdue books
    public void printOverdueBooks() {
        List<BorrowTransaction> overdue = libraryService.getOverdueTransactions();
        System.out.println("\u23F0 Overdue Books:");
        if (overdue.isEmpty()) {
            System.out.println(" (none)");
        }
        for (BorrowTransaction tx : overdue) {
            System.out.println(" - " + tx.getBook().getTitle()
                    + " (User: " + tx.getUser().getName()
                    + ", Due: " + tx.getDueDate() + ")");
        }
    }
}
