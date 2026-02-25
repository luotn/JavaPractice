package InterviewPractice4;

import java.math.BigDecimal;

public class User {
    private BigDecimal balance;

    public User(BigDecimal initialBalance) {
        if (initialBalance.compareTo(BigDecimal.ZERO) <= 0) { throw new IllegalArgumentException("initial balance must be greater than zero"); }
        balance = initialBalance;
    }

    public void deposit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) { throw new IllegalArgumentException("amount must be greater than zero"); }
        balance = balance.add(amount);
    }

    public void withdraw(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) { throw new IllegalArgumentException("amount must be greater than zero"); }
        BigDecimal remainingBalance = balance.subtract(amount);
        if (remainingBalance.compareTo(BigDecimal.ZERO) < 0) { throw new IllegalArgumentException("Insufficient funds"); }
        balance = remainingBalance;
    }

    public BigDecimal getBalance() {
        return this.balance;
    }
}
