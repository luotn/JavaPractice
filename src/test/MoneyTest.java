package test;

import BankingSystem.*;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MoneyTest {

    @Test
    void shouldCreateMoneySuccessfully() {
        Money subject = new Money(BigDecimal.valueOf(99, 2));
        assertNotNull(subject);
    }

    @Test
    void shouldCreateZeroMoneySuccessfully() {
        Money subject = new Money(BigDecimal.ZERO);
        assertNotNull(subject);
    }

    @Test
    void shouldHaveToString() {
        Money subject = new Money(BigDecimal.valueOf(99, 2));
        assertEquals("0.99", subject.toString());
    }

    @Test
    void shouldNOTHaveExponentField() {
        Money subject = new Money(BigDecimal.valueOf(9999999999L));
        assertEquals("9999999999", subject.toString());
    }

    @Test
    void sameAmountShouldEqual() {
        Money subject1 = new Money(BigDecimal.valueOf(9999999, 7));
        Money subject2 = new Money(BigDecimal.valueOf(9999999, 7));
        assertTrue(subject1.equals(subject2));
    }

    @Test
    void differentAmountShouldNOTEqual() {
        Money subject1 = new Money(BigDecimal.valueOf(9999999, 7));
        Money subject2 = new Money(BigDecimal.valueOf(9999998, 7));
        assertFalse(subject1.equals(subject2));
    }

    @Test
    void shouldThrowExceptionWhenAddingZero() {
        Money subject1 = new Money(BigDecimal.ONE);
        Money subject2 = new Money(BigDecimal.ZERO);
        assertThrows(IllegalArgumentException.class, () -> {
            subject1.add(subject2);
        });
    }

    @Test
    void shouldThrowExceptionWhenAddingNegative() {
        Money subject1 = new Money(BigDecimal.ONE);
        Money subject2 = new Money(BigDecimal.valueOf(-1));
        assertThrows(IllegalArgumentException.class, () -> {
            subject1.add(subject2);
        });
    }

    @Test
    void shouldAddReturnCorrectValue() {
        Money subject1 = new Money(BigDecimal.ONE);
        Money subject2 = new Money(BigDecimal.ONE);
        Money sum = subject1.add(subject2);
        assertTrue(new Money(BigDecimal.TWO).equals(sum));
    }

    @Test
    void shouldThrowExceptionWhenSubtractingZero() {
        Money subject1 = new Money(BigDecimal.ONE);
        Money subject2 = new Money(BigDecimal.ZERO);
        assertThrows(IllegalArgumentException.class, () -> {
            subject1.subtract(subject2);
        });
    }

    @Test
    void shouldSubtractReturnCorrectValue() {
        Money subject1 = new Money(BigDecimal.valueOf(9999999, 7));
        Money subject2 = new Money(BigDecimal.valueOf(9999998, 7));
        Money result = subject1.subtract(subject2);
        assertTrue(new Money(BigDecimal.valueOf(1, 7)).equals(result));
    }

    @Test
    void shouldReturnZeroWhenSubtractSameValue() {
        Money subject1 = new Money(BigDecimal.valueOf(9999998, 7));
        Money subject2 = new Money(BigDecimal.valueOf(9999998, 7));
        Money result = subject1.subtract(subject2);
        assertTrue(new Money(BigDecimal.ZERO).equals(result));
    }

    @Test
    void shouldAvoidFloatPrecision() {
        Money subject1 = new Money(BigDecimal.valueOf(1, 1));
        Money subject2 = new Money(BigDecimal.valueOf(2, 1));
        Money sum = subject1.add(subject2);
        assertTrue(new Money(BigDecimal.valueOf(3, 1)).equals(sum));
    }

    @Test
    void shouldAvoidFloatPrecision2() {
        Money subject = new Money(BigDecimal.valueOf(1, 1));
        for (int i = 0; i < 10; i++) {
            subject = subject.add(new Money(BigDecimal.valueOf(1, 1)));
        }
        assertTrue(new Money(BigDecimal.valueOf(11, 1)).equals(subject));
    }

    @Test
    void shouldOneNotNegative() {
        Money subject = new Money(BigDecimal.ONE);
        assertFalse(subject.isNegative());
    }

    @Test
    void shouldNegativeBeNegative() {
        Money subject = new Money(BigDecimal.valueOf(-1));
        assertTrue(subject.isNegative());
    }

}