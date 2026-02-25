package test.InterviewPractice1Tests;

import InterviewPractice1.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

class PaymentServiceTest {
    PaymentService paymentService;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentService();
        paymentService.createAccount(0L, BigDecimal.ZERO);
        paymentService.createAccount(1L, BigDecimal.ONE);
        paymentService.createAccount(2L, BigDecimal.TWO);
        paymentService.createAccount(10L, BigDecimal.TEN);
    }

    @Test
    void shouldCreateAccountSuccessful() {
        assertDoesNotThrow(() ->
            paymentService.createAccount(3L, BigDecimal.ZERO)
        );
    }

    @Test
    void shouldGetBalanceSuccessful() {
        assertEquals(0, paymentService.getBalance(0L).compareTo(BigDecimal.ZERO));
    }

    @Test
    void shouldTransferSuccessful() {
        String transferID  = paymentService.transfer(10L, 1L, BigDecimal.ONE);
        assertEquals(0,
                paymentService.getBalance(10L).compareTo(BigDecimal.valueOf(9))
        );
        assertEquals(0,
                paymentService.getBalance(1L).compareTo(BigDecimal.valueOf(2))
        );
        TransferRecord history = paymentService.getTransferHistory(transferID);
        assertEquals(10L, history.fromUID());
        assertEquals(1L, history.toUID());
        assertEquals(0, BigDecimal.ONE.compareTo(history.amount()));
        assertEquals(TransferStatus.SUCCESS, history.status());
    }

    @Test
    void shouldGetTransferByIDSuccessful() {
        String transferID = paymentService.transfer(10L, 1L, BigDecimal.ONE);
        TransferRecord history = paymentService.getTransferHistory(transferID);
        assertEquals(10L, history.fromUID());
        assertEquals(1L, history.toUID());
        assertEquals(0, BigDecimal.ONE.compareTo(history.amount()));
        assertEquals(TransferStatus.SUCCESS, history.status());
    }

    @Test
    void shouldThrowCreateAccountSameID() {
        assertThrows(IllegalArgumentException.class, () ->
                paymentService.createAccount(0L, BigDecimal.ZERO)
        );
    }

    @Test
    void shouldThrowCreateAccountInvalidBalance() {
        assertThrows(IllegalArgumentException.class, () ->
                paymentService.createAccount(3L, BigDecimal.valueOf(-1))
        );
    }

    @Test
    void shouldThrowGetBalanceIDNotFound() {
        assertThrows(IllegalArgumentException.class, () ->
                paymentService.getBalance(3L)
        );
    }

    @Test
    void shouldThrowAndKeepStateUnchangedOnInsufficientFunds() {
        BigDecimal beforeFromBalance =  paymentService.getBalance(0L);
        BigDecimal BeforeToBalance = paymentService.getBalance(1L);
        assertThrows(IllegalArgumentException.class, () ->
                paymentService.transfer(0L, 1L, BigDecimal.ONE)
        );
        assertEquals(0, beforeFromBalance.compareTo(paymentService.getBalance(0L)));
        assertEquals(0, BeforeToBalance.compareTo(paymentService.getBalance(1L)));

    }

    @Test
    void shouldThrowTransferInvalidAmount() {

    }

    @Test
    void shouldThrowTransferAccountNotFound() {
    }

    @Test
    void shouldThrowSelfTransfer() {

    }

    @Test
    void shouldPreserveTotalBalanceUnderConcurrentTransfers() throws InterruptedException {
        final int THREAD_COUNT = 32;
        final int TRANSFERS_PER_THREAD = 100;
        final int ACCOUNT_COUNT = 100;
        final int INITIAL_BALANCE = 10000;
        final BigDecimal INITIAL_TOTAL_BALANCE =
                BigDecimal.valueOf(INITIAL_BALANCE).multiply(BigDecimal.valueOf(ACCOUNT_COUNT));

        final PaymentService service = new PaymentService();
        for (int i = 0; i < ACCOUNT_COUNT; i++) {
            service.createAccount(i, BigDecimal.valueOf(INITIAL_BALANCE));
        }

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(THREAD_COUNT);

        for (int i = 0; i < THREAD_COUNT; i++) {
            executor.submit(() -> {
                try  {
                    startLatch.await();

                    ThreadLocalRandom random = ThreadLocalRandom.current();

                    for (int j = 0; j < TRANSFERS_PER_THREAD; j++) {
                        int fromID = random.nextInt(ACCOUNT_COUNT);
                        int toID = random.nextInt(ACCOUNT_COUNT);

                        if (fromID == toID) continue;

                        try {
                            service.transfer(fromID, toID, BigDecimal.ONE);
                        } catch (IllegalArgumentException e) {
//                                Ignore insufficient funds, and other valid failures for stress test
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }  finally {
                    doneLatch.countDown();
                }
            });
        }

        assertTimeoutPreemptively(Duration.ofSeconds(5), () -> {
            startLatch.countDown();
            doneLatch.await();

            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);
        });

        BigDecimal finalBalance = BigDecimal.ZERO;
        for (int i = 0; i < ACCOUNT_COUNT; i++) {
            BigDecimal accountBalance = service.getBalance(i);
            assertTrue(accountBalance.compareTo(BigDecimal.ZERO) >= 0);
            finalBalance = finalBalance.add(accountBalance);
        }
        assertEquals(0, finalBalance.compareTo(INITIAL_TOTAL_BALANCE));
    }

    @Test
    void shouldThrowGetTransferIDNotFound() {

    }
}