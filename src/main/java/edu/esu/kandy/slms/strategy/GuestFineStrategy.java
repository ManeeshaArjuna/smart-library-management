package edu.esu.kandy.slms.strategy;

// Strategy for calculating fines for guest users
public class GuestFineStrategy implements FineCalculationStrategy {
    @Override
    public double calculateFine(long overdueDays) {
        return overdueDays * 100.0;
    }
}
