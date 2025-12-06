package edu.esu.kandy.slms.model;

import edu.esu.kandy.slms.membership.MembershipType;
import edu.esu.kandy.slms.observer.NotificationEvent;
import edu.esu.kandy.slms.observer.NotificationObserver;

import java.util.ArrayList;
import java.util.List;

public class User implements NotificationObserver {

    private final String id;
    private final String name;
    private final String email;
    private final String contactNumber;
    private final MembershipType membershipType;

    private final List<BorrowTransaction> borrowHistory = new ArrayList<>();

    public User(String id, String name, String email, String contactNumber, MembershipType membershipType) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.contactNumber = contactNumber;
        this.membershipType = membershipType;
    }

    public String getId() { return id; }

    public String getName() { return name; }

    public String getEmail() { return email; }

    public String getContactNumber() { return contactNumber; }

    public MembershipType getMembershipType() { return membershipType; }

    public List<BorrowTransaction> getBorrowHistory() { return borrowHistory; }

    public void addBorrowTransaction(BorrowTransaction tx) {
        borrowHistory.add(tx);
    }

    @Override
    public void update(NotificationEvent event) {
        System.out.println("\uD83D\uDD14 Notification for " + name + " (" + email + "): " + event.getMessage());
    }
}
