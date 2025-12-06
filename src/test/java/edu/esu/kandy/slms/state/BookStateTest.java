package edu.esu.kandy.slms.state;

import edu.esu.kandy.slms.model.Book;
import edu.esu.kandy.slms.model.User;
import edu.esu.kandy.slms.membership.MembershipType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BookStateTest {

    @Test
    void testBorrowAndReturnFlow() {
        Book book = new Book.Builder("B999", "Test", "Author", "Cat", "ISBN").build();
        User user = new User("U999", "TestUser", "t@example.com", "000", MembershipType.STUDENT);

        assertEquals("Available", book.getState().getName());
        book.getState().borrow(book, user);
        assertEquals("Borrowed", book.getState().getName());
        book.getState().returnBook(book, user);
        assertEquals("Available", book.getState().getName());
    }
}
