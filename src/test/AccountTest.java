package test;

import BankingSystem.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {
    @Test
    void shouldBalanceIncreaseAfterDeposit() {
        Account subject = new Account(1L, new Money(BigDecimal.TEN));
        BigDecimal before = subject.getBalance();
        subject.deposit(new Money(BigDecimal.ONE));
        BigDecimal after = subject.getBalance();
        assertEquals(-1, before.compareTo(after));
    }

    @Test
    void shouldBalanceIncreaseAvoidFloatPrecision() {
        Account subject = new Account(1L, new Money(BigDecimal.valueOf(1, 1)));
        subject.deposit(new Money(BigDecimal.valueOf(2, 1)));
        assertEquals(BigDecimal.valueOf(3, 1), subject.getBalance());
    }

    @Test
    void shouldThrowExceptionAddZero() {
        Account subject = new Account(1L, new Money(BigDecimal.TEN));
        assertThrows(IllegalArgumentException.class, () -> {
            subject.deposit(new Money(BigDecimal.ZERO));
        });
    }

    @Test
    void shouldThrowExceptionAddNegative() {
        Account subject = new Account(1L, new Money(BigDecimal.TEN));
        assertThrows(IllegalArgumentException.class, () -> {
            subject.deposit(new Money(BigDecimal.valueOf(-1)));
        });
    }

    @Test
    void shouldBalanceDecreaseAfterWithdraw() {
        Account subject = new Account(1L, new Money(BigDecimal.TEN));
        BigDecimal before = subject.getBalance();
        subject.withdraw(new Money(BigDecimal.ONE));
        BigDecimal after = subject.getBalance();
        assertEquals(1, before.compareTo(after));
    }

    @Test
    void shouldThrowExceptionInsufficientFunds() {
        Account subject = new Account(1L, new Money(BigDecimal.ONE));
        assertThrows(IllegalArgumentException.class, () -> {
            subject.withdraw(new Money(BigDecimal.TEN));
        });
    }

    @Test
    void shouldThrowExceptionSubtractZero() {
        Account subject = new Account(1L, new Money(BigDecimal.TEN));
        assertThrows(IllegalArgumentException.class, () -> {
            subject.withdraw(new Money(BigDecimal.ZERO));
        });
    }

    @Test
    void shouldThrowExceptionSubtractNegative() {
        Account subject = new Account(1L, new Money(BigDecimal.TEN));
        assertThrows(IllegalArgumentException.class, () -> {
            subject.withdraw(new Money(BigDecimal.valueOf(-1)));
        });
    }
}