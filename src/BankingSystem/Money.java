package BankingSystem;

import java.math.BigDecimal;

public class Money {
    private final BigDecimal amount;

    public Money(BigDecimal amount) throws IllegalArgumentException{
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Money CANNOT be negative.");
        }
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

    @Override
    public boolean equals(Object otherObj) {
        if (otherObj.getClass() == Money.class) {
            Money other = (Money) otherObj;
            return this.amount.compareTo(other.getAmount()) == 0;
        }
        return false;
    }

    public String toString() {
        return this.amount.stripTrailingZeros().toPlainString();
    }
}
