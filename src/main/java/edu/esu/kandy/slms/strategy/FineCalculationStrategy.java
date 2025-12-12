package edu.esu.kandy.slms.strategy;

// Strategy interface for fine calculation
public interface FineCalculationStrategy {
    double calculateFine(long overdueDays);
}
