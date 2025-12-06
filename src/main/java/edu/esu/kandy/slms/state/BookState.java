package edu.esu.kandy.slms.state;

import edu.esu.kandy.slms.model.Book;
import edu.esu.kandy.slms.model.User;

public interface BookState {
    void borrow(Book book, User user);
    void returnBook(Book book, User user);
    void reserve(Book book, User user);
    String getName();
}
