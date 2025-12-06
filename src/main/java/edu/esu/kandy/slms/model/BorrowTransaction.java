package edu.esu.kandy.slms.model;

import java.time.LocalDate;

public class BorrowTransaction {

    private final String id;
    private final Book book;
    private final User user;
    private final LocalDate borrowDate;
    private final LocalDate dueDate;

    private LocalDate returnDate;
    private double finePaid;

    public BorrowTransaction(String id, Book book, User user, LocalDate borrowDate, LocalDate dueDate) {
        this.id = id;
        this.book = book;
        this.user = user;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
    }

    public String getId() { return id; }

    public Book getBook() { return book; }

    public User getUser() { return user; }

    public LocalDate getBorrowDate() { return borrowDate; }

    public LocalDate getDueDate() { return dueDate; }

    public LocalDate getReturnDate() { return returnDate; }

    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }

    public double getFinePaid() { return finePaid; }

    public void setFinePaid(double finePaid) { this.finePaid = finePaid; }

    public boolean isReturned() { return returnDate != null; }
}
