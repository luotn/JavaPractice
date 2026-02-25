package test.BankingSystemTests;

import BankingSystem.*;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

public class TransferRecordConcurrencyTest {
    private static final int THREAD_COUNT = 32;
    private static final int TRANSFERS_PER_THREAD = 100000;
    private static final int ACCOUNT_COUNT = 100;
    private static final BigDecimal INITIAL_BALANCE_PER_ACCOUNT = BigDecimal.valueOf(1000 ,0);

    @Test
    void shouldPreserveTotalBalanceUnderConcurrentTransfers() throws InterruptedException {

        List<Account> accounts = new ArrayList<>();
        for (int i = 0; i < ACCOUNT_COUNT; i++) {
            accounts.add(new Account(i, new Money(INITIAL_BALANCE_PER_ACCOUNT)));
        }

        BigDecimal initialTotal = calculateTotal(accounts);

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(THREAD_COUNT);

        Random random = new Random();

        for (int i = 0; i < THREAD_COUNT; i++) {
            executor.execute(() -> {
                try {
                    startLatch.await();

                    for (int j = 0; j < TRANSFERS_PER_THREAD; j++) {

                        int fromIndex = random.nextInt(ACCOUNT_COUNT);
                        int toIndex = random.nextInt(ACCOUNT_COUNT);

                        if (fromIndex == toIndex) continue;

                        Account from = accounts.get(fromIndex);
                        Account to = accounts.get(toIndex);

                        Money amount = new Money(BigDecimal.ONE);

                        try {
                            Transfer transfer = new Transfer(from, to, amount);
                            transfer.execute();
                        } catch (IllegalArgumentException ignored) {
                            // Ignore insufficient funds
                        }
                    }

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown(); // Wait for all threads to start
        doneLatch.await();      // Wait for all threads to terminate

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        BigDecimal finalTotal = calculateTotal(accounts);

        assertEquals(0, initialTotal.compareTo(finalTotal),
                "Initial total: " + initialTotal + ", final total: " + finalTotal);
    }

    private BigDecimal calculateTotal(List<Account> accounts) {
        BigDecimal total = BigDecimal.ZERO;
        for (Account account : accounts) {
            total = total.add(account.getBalance());
        }
        return total;
    }
}
