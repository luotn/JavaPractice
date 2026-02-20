package test;

import BankingSystem.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class TransferTest {

    Account emptyAccount;
    Account oneAccount;
    Account nullAccount;
    Money oneUnit;
    Money zeroUnit;
    Money negativeUnit;

    @BeforeEach
    void setUp() {
        emptyAccount = new Account(1L, new Money(BigDecimal.ZERO));
        oneAccount = new Account(2L, new Money(BigDecimal.ONE));
        oneUnit = new Money(BigDecimal.ONE);
        zeroUnit = new Money(BigDecimal.ZERO);
        negativeUnit = new Money(BigDecimal.valueOf(-1, 0));
    }

    @Test
    void shouldNormalTransferSuccessful() {
        Transfer subject = new Transfer(oneAccount, emptyAccount, oneUnit);
        subject.execute();
    }

    @Test
    void shouldNormalTransferBalanceCorrect() {
        Transfer subject = new Transfer(oneAccount, emptyAccount, oneUnit);
        subject.execute();
        assertTrue(oneAccount.getBalance().compareTo(BigDecimal.ZERO) == 0);
        assertTrue(emptyAccount.getBalance().compareTo(BigDecimal.ONE) == 0);
    }

    @Test
    void shouldInsufficientFundsThrow() {
        Transfer subject = new Transfer(emptyAccount, oneAccount, oneUnit);
        assertThrows(IllegalArgumentException.class, () -> {
            subject.execute();
        });
    }

    @Test
    void shouldNullOriginThrow() {
        Transfer subject = new Transfer(nullAccount, oneAccount, oneUnit);
        assertThrows(IllegalArgumentException.class, () -> {
            subject.execute();
        });
    }

    @Test
    void shouldNullTargetThrow() {
        Transfer subject = new Transfer(emptyAccount, nullAccount, oneUnit);
        assertThrows(IllegalArgumentException.class, () -> {
            subject.execute();
        });
    }

    @Test
    void shouldSelfTransferThrow() {
        Transfer subject = new Transfer(oneAccount, oneAccount, oneUnit);
        assertThrows(IllegalArgumentException.class, () -> {
            subject.execute();
        });
    }

    @Test
    void shouldZeroUnitTransferThrow() {
        Transfer subject = new Transfer(emptyAccount, oneAccount, zeroUnit);
        assertThrows(IllegalArgumentException.class, () -> {
            subject.execute();
        });
    }

    @Test
    void shouldNegativeUnitTransferThrow() {
        Transfer subject = new Transfer(emptyAccount, oneAccount, negativeUnit);
        assertThrows(IllegalArgumentException.class, () -> {
            subject.execute();
        });
    }


}