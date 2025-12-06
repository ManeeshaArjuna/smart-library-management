package edu.esu.kandy.slms.observer;

import edu.esu.kandy.slms.model.Book;
import edu.esu.kandy.slms.model.User;

public class NotificationEvent {

    private final NotificationEventType type;
    private final String message;
    private final User user;
    private final Book book;

    public NotificationEvent(NotificationEventType type, String message, User user, Book book) {
        this.type = type;
        this.message = message;
        this.user = user;
        this.book = book;
    }

    public NotificationEventType getType() { return type; }

    public String getMessage() { return message; }

    public User getUser() { return user; }

    public Book getBook() { return book; }
}
