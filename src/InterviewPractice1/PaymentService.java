package InterviewPractice1;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PaymentService {
    private final ConcurrentHashMap<Long, Account> accounts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, TransferRecord> transfers = new ConcurrentHashMap<>();

    public void createAccount(long id, BigDecimal initialBalance) {
        if(accounts.get(id) != null) throw new IllegalArgumentException("Account already exists");
        if(initialBalance.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Initial balance must NOT be negative");
        accounts.put(id, new Account(initialBalance));
    }

    public BigDecimal getBalance(long id) {
        if(accounts.get(id) == null) throw new IllegalArgumentException("Account does not exist");
        return accounts.get(id).getBalance();
    }

    public String transfer(long fromID, long toID, BigDecimal amount) {
        String transferID =  UUID.randomUUID().toString();
        if(accounts.get(fromID) == null || accounts.get(toID) == null) {
            transfers.put(transferID, new TransferRecord(fromID, toID, amount, TransferStatus.FAILED));
            throw new IllegalArgumentException("Account doesn't exist");
        }
        if(fromID == toID) {
            transfers.put(transferID, new TransferRecord(fromID, toID, amount, TransferStatus.FAILED));
            throw new IllegalArgumentException("Self transferring");
        }

//        Avoid deadlock
        Account firstAccount;
        Account secondAccount;
        if (fromID < toID) {
            firstAccount = accounts.get(fromID);
            secondAccount = accounts.get(toID);
        } else {
            firstAccount = accounts.get(toID);
            secondAccount = accounts.get(fromID);
        }

//        Account locks
        synchronized (firstAccount) {
            synchronized (secondAccount) {
                accounts.get(fromID).withdraw(amount);
                accounts.get(toID).deposit(amount);
            }
        }
        transfers.put(transferID, new TransferRecord(fromID, toID, amount, TransferStatus.SUCCESS));
        return transferID;
    }

    public TransferRecord getTransferHistory(String transferID) {
        if(transfers.get(transferID) == null) throw new IllegalArgumentException("Transfer not found");
        return transfers.get(transferID);
    }
}
