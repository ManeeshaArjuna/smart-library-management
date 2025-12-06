package edu.esu.kandy.slms.decorator;

import edu.esu.kandy.slms.model.Book;

public class BasicBookView implements BookView {

    private final Book book;

    public BasicBookView(Book book) {
        this.book = book;
    }

    @Override
    public String getDisplayTitle() {
        return book.getTitle() + " by " + book.getAuthor();
    }
}
