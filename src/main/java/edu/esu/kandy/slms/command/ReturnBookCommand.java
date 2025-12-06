package edu.esu.kandy.slms.command;

import edu.esu.kandy.slms.service.LibraryService;

public class ReturnBookCommand implements Command {

    private final LibraryService libraryService;
    private final String userId;
    private final String bookId;

    public ReturnBookCommand(LibraryService libraryService, String userId, String bookId) {
        this.libraryService = libraryService;
        this.userId = userId;
        this.bookId = bookId;
    }

    @Override
    public void execute() {
        libraryService.returnBook(userId, bookId);
    }

    @Override
    public void undo() {
        libraryService.borrowBook(userId, bookId);
    }

    @Override
    public String getName() {
        return "Return Book (" + bookId + ") for user " + userId;
    }
}
