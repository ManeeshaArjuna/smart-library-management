package edu.esu.kandy.slms.strategy;

import edu.esu.kandy.slms.membership.MembershipType;

public class FineCalculationContext {

    private FineCalculationStrategy strategy;

    public void setStrategy(FineCalculationStrategy strategy) {
        this.strategy = strategy;
    }

    // Calculate fine using the current strategy
    public double calculateFine(long overdueDays) {
        if (strategy == null) return 0.0;
        return strategy.calculateFine(overdueDays);
    }

    // Factory method to get strategy based on membership type
    public static FineCalculationStrategy strategyFor(MembershipType type) {
        return switch (type) {
            case STUDENT -> new StudentFineStrategy();
            case FACULTY -> new FacultyFineStrategy();
            case GUEST -> new GuestFineStrategy();
        };
    }
}
