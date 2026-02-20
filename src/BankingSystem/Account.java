package BankingSystem;

import java.math.BigDecimal;

public class Account {
    private final long uuid;
    private Money balance;

    public Account(long uuid, Money initialBalance) {
        this.uuid = uuid;
        this.balance = initialBalance;
    }

    public void deposit(Money amount) throws IllegalArgumentException {
        this.balance = this.balance.add(amount);
    }

    public void withdraw(Money amount) throws  IllegalArgumentException {
        Money resultBalance = this.balance.subtract(amount);
        if (resultBalance.isNegative()) throw new IllegalArgumentException("Insufficient funds.");
        this.balance = resultBalance;
    }

    public BigDecimal getBalance() {
        return this.balance.getAmount();
    }

    public long getUuid() {
        return this.uuid;
    }
}
