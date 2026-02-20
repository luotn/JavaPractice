package BankingSystem;

public class Transfer {

    private final Account originAccount;
    private final Account targetAccount;
    private final Money amount;

    public Transfer(Account origin, Account target, Money amount) {
        this.originAccount = origin;
        this.targetAccount = target;
        this.amount = amount;
    }

    public void execute() throws IllegalArgumentException {
        if (this.originAccount == null || this.targetAccount == null)
            throw new IllegalArgumentException("Transfer account(s) is null");
        if (this.originAccount.getUuid() == this.targetAccount.getUuid())
            throw new IllegalArgumentException("Transfer origin same as target");
        Account first;
        Account second;
        if (this.originAccount.getUuid() < this.targetAccount.getUuid()) {
            first = this.originAccount;
            second = this.targetAccount;
        } else {
            first = this.targetAccount;
            second = this.originAccount;
        }
        synchronized (first) {
            synchronized (second) {
                this.originAccount.withdraw(this.amount);
                this.targetAccount.deposit(this.amount);
            }
        }
    }

}
