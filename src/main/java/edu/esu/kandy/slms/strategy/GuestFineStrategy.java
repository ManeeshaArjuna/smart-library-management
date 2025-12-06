package edu.esu.kandy.slms.strategy;

public class GuestFineStrategy implements FineCalculationStrategy {
    @Override
    public double calculateFine(long overdueDays) {
        return overdueDays * 100.0;
    }
}
