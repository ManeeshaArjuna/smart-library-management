package edu.esu.kandy.slms.strategy;

public class StudentFineStrategy implements FineCalculationStrategy {
    @Override
    public double calculateFine(long overdueDays) {
        return overdueDays * 50.0;
    }
}
