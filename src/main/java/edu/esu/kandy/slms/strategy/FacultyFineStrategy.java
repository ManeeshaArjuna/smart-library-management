package edu.esu.kandy.slms.strategy;

// Fine calculation strategy for faculty members
public class FacultyFineStrategy implements FineCalculationStrategy {
    @Override
    public double calculateFine(long overdueDays) {
        return overdueDays * 20.0;
    }
}
