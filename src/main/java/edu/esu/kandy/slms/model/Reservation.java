package edu.esu.kandy.slms.model;

import java.time.LocalDate;

public class Reservation {

    private final String id;
    private final Book book;
    private final User user;
    private final LocalDate reservedDate;
    private boolean active;

    public Reservation(String id, Book book, User user, LocalDate reservedDate) {
        this.id = id;
        this.book = book;
        this.user = user;
        this.reservedDate = reservedDate;
        this.active = true;
    }

    public String getId() { return id; }

    public Book getBook() { return book; }

    public User getUser() { return user; }

    public LocalDate getReservedDate() { return reservedDate; }

    public boolean isActive() { return active; }

    public void setActive(boolean active) { this.active = active; }
}
