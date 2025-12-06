package edu.esu.kandy.slms.command;

import edu.esu.kandy.slms.service.LibraryService;

public class ReserveBookCommand implements Command {

    private final LibraryService libraryService;
    private final String userId;
    private final String bookId;

    public ReserveBookCommand(LibraryService libraryService, String userId, String bookId) {
        this.libraryService = libraryService;
        this.userId = userId;
        this.bookId = bookId;
    }

    @Override
    public void execute() {
        libraryService.reserveBook(userId, bookId);
    }

    @Override
    public void undo() {
        libraryService.cancelReservation(userId, bookId);
    }

    @Override
    public String getName() {
        return "Reserve Book (" + bookId + ") for user " + userId;
    }
}
