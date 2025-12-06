package edu.esu.kandy.slms.command;

import edu.esu.kandy.slms.service.LibraryService;

public class BorrowBookCommand implements Command {

    private final LibraryService libraryService;
    private final String userId;
    private final String bookId;

    public BorrowBookCommand(LibraryService libraryService, String userId, String bookId) {
        this.libraryService = libraryService;
        this.userId = userId;
        this.bookId = bookId;
    }

    @Override
    public void execute() {
        libraryService.borrowBook(userId, bookId);
    }

    @Override
    public void undo() {
        libraryService.forceReturnWithoutFine(userId, bookId);
    }

    @Override
    public String getName() {
        return "Borrow Book (" + bookId + ") for user " + userId;
    }
}
