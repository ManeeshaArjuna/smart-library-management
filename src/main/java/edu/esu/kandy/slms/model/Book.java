package edu.esu.kandy.slms.model;

import edu.esu.kandy.slms.state.AvailableState;
import edu.esu.kandy.slms.state.BookState;

import java.util.ArrayList;
import java.util.List;

public class Book {

    private final String id;
    private final String title;
    private final String author;
    private final String category;
    private final String isbn;

    private final List<String> tags;
    private final List<String> reviews;
    private final String edition;

    private final boolean featured;
    private final boolean recommended;
    private final boolean specialEdition;

    private final List<BorrowTransaction> borrowHistory = new ArrayList<>();
    private final List<Reservation> reservations = new ArrayList<>();

    private BookState state;

    public Book(Builder builder) {
        this.id = builder.id;
        this.title = builder.title;
        this.author = builder.author;
        this.category = builder.category;
        this.isbn = builder.isbn;
        this.tags = builder.tags;
        this.reviews = builder.reviews;
        this.edition = builder.edition;
        this.featured = builder.featured;
        this.recommended = builder.recommended;
        this.specialEdition = builder.specialEdition;
        this.state = new AvailableState();
    }

    public String getId() { return id; }

    public String getTitle() { return title; }

    public String getAuthor() { return author; }

    public String getCategory() { return category; }

    public String getIsbn() { return isbn; }

    public List<String> getTags() { return tags; }

    public List<String> getReviews() { return reviews; }

    public String getEdition() { return edition; }

    public boolean isFeatured() { return featured; }

    public boolean isRecommended() { return recommended; }

    public boolean isSpecialEdition() { return specialEdition; }

    public void addBorrowTransaction(BorrowTransaction tx) {
        borrowHistory.add(tx);
    }

    public List<BorrowTransaction> getBorrowHistory() {
        return borrowHistory;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void addReservation(Reservation reservation) {
        reservations.add(reservation);
    }

    public void removeReservation(Reservation reservation) {
        reservations.remove(reservation);
    }

    public boolean hasActiveReservations() {
        return reservations.stream().anyMatch(Reservation::isActive);
    }

    public BookState getState() { return state; }

    public void setState(BookState state) { this.state = state; }

    public static class Builder {
        private final String id;
        private final String title;
        private final String author;
        private final String category;
        private final String isbn;

        private List<String> tags = new ArrayList<>();
        private List<String> reviews = new ArrayList<>();
        private String edition;

        private boolean featured;
        private boolean recommended;
        private boolean specialEdition;

        public Builder(String id, String title, String author, String category, String isbn) {
            this.id = id;
            this.title = title;
            this.author = author;
            this.category = category;
            this.isbn = isbn;
        }

        public Builder tags(List<String> tags) {
            this.tags = tags;
            return this;
        }

        public Builder reviews(List<String> reviews) {
            this.reviews = reviews;
            return this;
        }

        public Builder edition(String edition) {
            this.edition = edition;
            return this;
        }

        public Builder featured(boolean featured) {
            this.featured = featured;
            return this;
        }

        public Builder recommended(boolean recommended) {
            this.recommended = recommended;
            return this;
        }

        public Builder specialEdition(boolean specialEdition) {
            this.specialEdition = specialEdition;
            return this;
        }

        public Book build() {
            return new Book(this);
        }
    }
}
