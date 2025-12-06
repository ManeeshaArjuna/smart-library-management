package edu.esu.kandy.slms.membership;

public enum MembershipType {
    STUDENT(2, 14),
    FACULTY(5, 30),
    GUEST(1, 7);

    private final int borrowLimit;
    private final int borrowDays;

    MembershipType(int borrowLimit, int borrowDays) {
        this.borrowLimit = borrowLimit;
        this.borrowDays = borrowDays;
    }

    public int getBorrowLimit() {
        return borrowLimit;
    }

    public int getBorrowDays() {
        return borrowDays;
    }
}
