package edu.esu.kandy.slms.strategy;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FineStrategyTest {

    @Test
    void testStudentFine() {
        FineCalculationStrategy s = new StudentFineStrategy();
        assertEquals(150.0, s.calculateFine(3));
    }

    @Test
    void testFacultyFine() {
        FineCalculationStrategy s = new FacultyFineStrategy();
        assertEquals(60.0, s.calculateFine(3));
    }

    @Test
    void testGuestFine() {
        FineCalculationStrategy s = new GuestFineStrategy();
        assertEquals(300.0, s.calculateFine(3));
    }
}
