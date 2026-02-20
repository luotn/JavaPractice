package BankingSystem;

import java.math.BigDecimal;

public class Money {
    private final BigDecimal amount;

    public Money(BigDecimal amount) {
        this.amount = amount.stripTrailingZeros();
    }

    public BigDecimal getAmount() {
        return this.amount.stripTrailingZeros();
    }

    public Money add(Money other) throws IllegalArgumentException {
        if(other.getAmount().compareTo(BigDecimal.ZERO) > 0) {
            return new Money(this.amount.add(other.getAmount()));

        }
        throw new IllegalArgumentException("Cannot add zero or negative!");
    }

    public Money subtract(Money other) throws IllegalArgumentException {
        if(other.getAmount().compareTo(BigDecimal.ZERO) > 0) {
            return new Money(this.amount.subtract(other.getAmount()));

        }
        throw new IllegalArgumentException("Cannot subtract zero or negative!");
    }

    public boolean equals(Money other) {
        return this.amount.compareTo(other.getAmount()) == 0;
    }

    public String toString() {
        return this.amount.stripTrailingZeros().toPlainString();
    }

    public boolean isNegative() {
        return this.amount.compareTo(BigDecimal.ZERO) < 0;
    }
}
