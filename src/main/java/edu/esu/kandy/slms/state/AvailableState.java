package edu.esu.kandy.slms.state;

import edu.esu.kandy.slms.model.Book;
import edu.esu.kandy.slms.model.User;

// State when the book is available for borrowing
public class AvailableState implements BookState {

    @Override
    public void borrow(Book book, User user) {
        book.setState(new BorrowedState());
    }

    @Override
    public void returnBook(Book book, User user) {
        throw new IllegalStateException("Book is already available.");
    }

    @Override
    public void reserve(Book book, User user) {
        book.setState(new ReservedState());
    }

    @Override
    public String getName() {
        return "Available";
    }
}
