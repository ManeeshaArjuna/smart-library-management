package edu.esu.kandy.slms.state;

import edu.esu.kandy.slms.model.Book;
import edu.esu.kandy.slms.model.User;


// State when the book is borrowed
public class BorrowedState implements BookState {

    @Override
    public void borrow(Book book, User user) {
        throw new IllegalStateException("Book is already borrowed.");
    }

    @Override
    public void returnBook(Book book, User user) {
        if (book.hasActiveReservations()) {
            book.setState(new ReservedState());
        } else {
            book.setState(new AvailableState());
        }
    }

    @Override
    public void reserve(Book book, User user) {
        // reservations handled elsewhere
    }

    @Override
    public String getName() {
        return "Borrowed";
    }
}
