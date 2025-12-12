package edu.esu.kandy.slms.state;

import edu.esu.kandy.slms.model.Book;
import edu.esu.kandy.slms.model.User;

// State when the book is reserved by a user
public class ReservedState implements BookState {

    @Override
    public void borrow(Book book, User user) {
        book.setState(new BorrowedState());
    }

    @Override
    public void returnBook(Book book, User user) {
        if (book.hasActiveReservations()) {
            book.setState(this);
        } else {
            book.setState(new AvailableState());
        }
    }

    @Override
    public void reserve(Book book, User user) {
        // multiple reservations allowed
    }

    @Override
    public String getName() {
        return "Reserved";
    }
}
