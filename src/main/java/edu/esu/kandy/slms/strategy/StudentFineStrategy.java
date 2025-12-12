package edu.esu.kandy.slms.strategy;

// Strategy interface for fine calculation
public class StudentFineStrategy implements FineCalculationStrategy {
    @Override
    public double calculateFine(long overdueDays) {
        return overdueDays * 50.0;
    }
}
