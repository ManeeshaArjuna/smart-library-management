package edu.esu.kandy.slms.strategy;

public class FacultyFineStrategy implements FineCalculationStrategy {
    @Override
    public double calculateFine(long overdueDays) {
        return overdueDays * 20.0;
    }
}
