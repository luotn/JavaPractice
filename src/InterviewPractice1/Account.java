package InterviewPractice1;

import java.math.BigDecimal;

public class Account {
    private BigDecimal balance;

    public Account(BigDecimal initialBalance) {
        this.balance = initialBalance;
    }


    public BigDecimal getBalance() { return this.balance; }

    public void deposit(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }

    public void withdraw(BigDecimal amount) {
        BigDecimal result = this.balance.subtract(amount);
        if (result.compareTo(BigDecimal.ZERO) >= 0) {
            this.balance = result;
        } else {
            throw new IllegalArgumentException("Insufficient funds");
        }
    }
}
