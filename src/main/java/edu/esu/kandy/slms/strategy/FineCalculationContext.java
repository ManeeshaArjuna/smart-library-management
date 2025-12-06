package edu.esu.kandy.slms.strategy;

import edu.esu.kandy.slms.membership.MembershipType;

public class FineCalculationContext {

    private FineCalculationStrategy strategy;

    public void setStrategy(FineCalculationStrategy strategy) {
        this.strategy = strategy;
    }

    public double calculateFine(long overdueDays) {
        if (strategy == null) return 0.0;
        return strategy.calculateFine(overdueDays);
    }

    public static FineCalculationStrategy strategyFor(MembershipType type) {
        return switch (type) {
            case STUDENT -> new StudentFineStrategy();
            case FACULTY -> new FacultyFineStrategy();
            case GUEST -> new GuestFineStrategy();
        };
    }
}
