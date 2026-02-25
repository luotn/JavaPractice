package test.InterviewPractice4Tests;

import InterviewPractice4.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class ScheduledPaymentServiceTest {
    ScheduledPaymentService service;
    UUID user1;
    BigDecimal user1InitialBalance;
    UUID user10;
    BigDecimal user10InitialBalance;
    UUID testPaymentId;
    BigDecimal testPaymentAmount;
    Instant testPaymentTime;

    @BeforeEach
    void setUp() {
        service = new ScheduledPaymentService();
        user1 = UUID.randomUUID();
        user10 = UUID.randomUUID();
        user1InitialBalance = BigDecimal.ONE;
        user10InitialBalance = BigDecimal.TEN;
        service.createUser(user1, user1InitialBalance);
        service.createUser(user10, user10InitialBalance);
        testPaymentAmount = BigDecimal.ONE;
        testPaymentTime = Instant.now().plusSeconds(1);

        testPaymentId = service.schedulePayment(user10, user1, testPaymentAmount, testPaymentTime);
    }

    @Test
    void shouldCreateUserSuccessful() {
        UUID user2 = UUID.randomUUID();
        BigDecimal user2InitialBalance = BigDecimal.TWO;
        service.createUser(user2, BigDecimal.TWO);
        User resultUser = service.getUser(user2);
        assertEquals(0, user2InitialBalance.compareTo(resultUser.getBalance()));
    }

    @Test
    void shouldSchedulePaymentSuccessful() {
        BigDecimal paymentAmount = BigDecimal.ONE;
        Instant paymentTime = Instant.now().plusSeconds(5);
        UUID paymentId = service.schedulePayment(user1, user10, paymentAmount, paymentTime);
        Payment payment = service.getPayment(paymentId);
        assertEquals(user1, payment.getUserId());
        assertEquals(user10, payment.getPayeeId());
        assertEquals(paymentAmount, payment.getAmount());
        assertEquals(paymentTime, payment.getExecuteAt());

    }

    @Test
    void shouldRunDuePaymentsSuccessful() {
        assertDoesNotThrow(() -> service.runDuePayments(Instant.now().plusSeconds(10)));
        BigDecimal user1BalanceAfter = service.getUser(user1).getBalance();
        BigDecimal user10BalanceAfter = service.getUser(user10).getBalance();
        assertEquals(0, user1BalanceAfter.compareTo(user1InitialBalance.add(testPaymentAmount)));
        assertEquals(0, user10BalanceAfter.compareTo(user10InitialBalance.subtract(testPaymentAmount)));
    }

    @Test
    void shouldGetPaymentSuccessful() {
        Payment payment = service.getPayment(testPaymentId);
        assertEquals(user10, payment.getUserId());
        assertEquals(user1, payment.getPayeeId());
        assertEquals(0, testPaymentAmount.compareTo(payment.getAmount()));
        assertEquals(0, testPaymentTime.compareTo(payment.getExecuteAt()));
        assertEquals(0, payment.getAttempts());
    }

    @Test
    void shouldThrowCreateUserInvalidValues() {
        assertThrows(NullPointerException.class, () -> service.createUser(null, BigDecimal.ONE));
        assertThrows(NullPointerException.class, () -> service.createUser(UUID.randomUUID(), null));
        assertThrows(IllegalArgumentException.class, () -> service.createUser(UUID.randomUUID(), BigDecimal.valueOf(0)));
    }

    @Test
    void shouldThrowCreateExistingUser() {
        assertThrows(IllegalArgumentException.class, () -> service.createUser(user1, BigDecimal.TEN));
    }

    @Test
    void shouldThrowSchedulePaymentInvalidValues() {
        assertThrows(NullPointerException.class, () -> service.schedulePayment(null, user1, testPaymentAmount, testPaymentTime));
        assertThrows(NullPointerException.class, () -> service.schedulePayment(user10, null, testPaymentAmount, testPaymentTime));
        assertThrows(NullPointerException.class, () -> service.schedulePayment(user10, user1, null, testPaymentTime));
        assertThrows(NullPointerException.class, () -> service.schedulePayment(user10, user1, testPaymentAmount, null));
        assertThrows(IllegalArgumentException.class, () -> service.schedulePayment(user10, user1, BigDecimal.ZERO, testPaymentTime));
        assertThrows(IllegalArgumentException.class, () -> service.schedulePayment(user10, user1, testPaymentAmount, Instant.now().minusSeconds(1)));

    }

    @Test
    void shouldThrowScheduleSelfPayment() {
        assertThrows(IllegalArgumentException.class, () -> service.schedulePayment(user1, user1, testPaymentAmount, testPaymentTime));
    }

    @Test
    void shouldThrowSchedulePaymentUserDoesNotExist() {
        assertThrows(IllegalArgumentException.class, () -> service.schedulePayment(UUID.randomUUID(), user1, testPaymentAmount, testPaymentTime));
        assertThrows(IllegalArgumentException.class, () -> service.schedulePayment(user1, UUID.randomUUID(), testPaymentAmount, testPaymentTime));

    }

    @Test
    void shouldThrowRunPaymentsNullInstant() {
        assertThrows(NullPointerException.class, () -> service.runDuePayments(null));
    }

    @Test
    void shouldKeepBalanceRunDuePaymentsInsufficientFunds() {
        UUID paymentId = service.schedulePayment(user1, user10, BigDecimal.TWO, testPaymentTime);
        assertThrows(IllegalArgumentException.class, () -> service.runDuePayments(Instant.now().plusSeconds(10)));
        assertEquals(0, service.getUser(user1).getBalance().compareTo(user1InitialBalance));
        assertEquals(0, service.getUser(user10).getBalance().compareTo(user10InitialBalance));
    }

    @Test
    void shouldThrowGetPaymentNullAndUnknownId() {
        assertThrows(NullPointerException.class, () -> service.getPayment(null));
        assertThrows(IllegalArgumentException.class, () -> service.getPayment(UUID.randomUUID()));
    }

    @Test
    void shouldAvoidDoubleSpendRunDuePayments() {
        int threadCount = 32;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();

                    try {
                        service.runDuePayments(Instant.now().plusSeconds(20));
                    } catch (IllegalArgumentException e) {
//                      Ignore exception thrown by Insufficient funds
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        assertTimeoutPreemptively(Duration.ofSeconds(5), () -> {
            startLatch.countDown();
            doneLatch.await();

            executor.shutdown();
        });

        Payment resultPayment = service.getPayment(testPaymentId);
        assertEquals(ScheduledPaymentStatus.SUCCESS, resultPayment.getStatus());
        assertEquals(1, resultPayment.getAttempts());
        assertEquals(0, user1InitialBalance.add(testPaymentAmount).compareTo(service.getUser(user1).getBalance()));
        assertEquals(0, user10InitialBalance.subtract(testPaymentAmount).compareTo(service.getUser(user10).getBalance()));
    }
}