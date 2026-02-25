package test.InterviewPractice3Tests;

import InterviewPractice3.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class GiftCardServiceTest {
    GiftCardService service;
    UUID testCard;
    BigDecimal testCardInitialBalance;
    BigDecimal NineNinetyNine = BigDecimal.valueOf(999, 2);

    @BeforeEach
    void setUp() {
        service = new GiftCardService();
        testCard = UUID.randomUUID();
        testCardInitialBalance = BigDecimal.valueOf(100);
        service.createGiftCard(testCard, testCardInitialBalance);
    }

    @Test
    void shouldCreateAndGetBalanceSuccessful() {
        UUID cardId = UUID.randomUUID();
        service.createGiftCard(cardId, NineNinetyNine);
        assertEquals(NineNinetyNine, service.getBalance(cardId));
    }

    @Test
    void shouldRedeemSuccessful() {
        UUID redeemId = service.redeem(testCard, NineNinetyNine);

        assertNotNull(redeemId);
        BigDecimal expectedBalance = testCardInitialBalance.subtract(NineNinetyNine);
        assertEquals(0, expectedBalance.compareTo(service.getBalance(testCard)));
    }

    @Test
    void shouldRefundSuccessful() {
        UUID redemptionId = service.redeem(testCard, NineNinetyNine);
        service.refund(redemptionId);

        assertEquals(0, testCardInitialBalance.compareTo(service.getBalance(testCard)));
    }

    @Test
    void shouldGetRedemptionSuccessful() {
        UUID redeemId = service.redeem(testCard, NineNinetyNine);

        Redemption redemption = service.getRedemption(redeemId);
        assertEquals(redemption.getCardId(), testCard);
        assertEquals(0, redemption.getAmount().compareTo(NineNinetyNine));
        assertEquals(redemption.getStatus(), GiftCardStatus.SUCCESS);
    }

    @Test
    void shouldThrowCreateGiftCardNullValues() {
        assertThrows(NullPointerException.class, () -> service.createGiftCard(null, NineNinetyNine));
        assertThrows(NullPointerException.class, () -> service.createGiftCard(testCard, null));
        assertThrows(NullPointerException.class, () -> service.createGiftCard(null, null));
    }

    @Test
    void shouldThrowCreateDuplicateGiftCard() {
        assertThrows(IllegalArgumentException.class, () -> service.createGiftCard(testCard, BigDecimal.valueOf(999)));
    }

    @Test
    void shouldThrowCreateZeroOrNegativeGiftCard() {
        assertThrows(IllegalArgumentException.class, () -> service.createGiftCard(testCard, BigDecimal.ZERO));
        assertThrows(IllegalArgumentException.class, () -> service.createGiftCard(testCard, BigDecimal.valueOf(-1)));
    }

    @Test
    void shouldThrowGetBalanceNullValues() {
        assertThrows(NullPointerException.class, () -> service.getBalance(null));
    }

    @Test
    void shouldThrowGetBalanceUnknownCard() {
        assertThrows(IllegalArgumentException.class, () -> service.getBalance(UUID.randomUUID()));
    }

    @Test
    void shouldThrowRedeemNullValues() {
        assertThrows(NullPointerException.class, () -> service.redeem(null, NineNinetyNine));
        assertThrows(NullPointerException.class, () -> service.redeem(testCard, null));
        assertThrows(NullPointerException.class, () -> service.redeem(null, null));
    }

    @Test
    void shouldThrowRedeemUnknownCard() {
        assertThrows(IllegalArgumentException.class, () -> service.redeem(UUID.randomUUID(), NineNinetyNine));
    }

    @Test
    void shouldThrowRedeemInvalidAmount() {
        assertThrows(IllegalArgumentException.class, () -> service.redeem(testCard, BigDecimal.valueOf(0)));
    }

    @Test
    void shouldKeepBalanceInsufficientFunds() {
        BigDecimal bigRedeemAmount = BigDecimal.valueOf(999);
        assertThrows(IllegalArgumentException.class, () -> service.redeem(testCard, bigRedeemAmount));
        assertEquals(0, service.getBalance(testCard).compareTo(testCardInitialBalance));
    }

    @Test
    void shouldThrowRefundInvalidId() {
        assertThrows(NullPointerException.class, () -> service.refund(null));
        assertThrows(IllegalArgumentException.class, () -> service.refund(UUID.randomUUID()));
    }

    @Test
    void shouldThrowRefundInvalidStatus() {
        UUID redeemId = service.redeem(testCard, NineNinetyNine);
        service.refund(redeemId);

        assertThrows(IllegalArgumentException.class, () -> service.refund(redeemId));
    }

    @Test
    void shouldThrowRefundInvvalidRedemptionId() {
        assertThrows(NullPointerException.class, () -> service.refund(null));
        assertThrows(IllegalArgumentException.class, () -> service.refund(UUID.randomUUID()));
    }

    @Test
    void shouldThrowGetRedemptionInvalidId() {
        assertThrows(NullPointerException.class, () -> service.getRedemption(null));
        assertThrows(IllegalArgumentException.class, () -> service.getRedemption(UUID.randomUUID()));
    }

    @Test
    void shouldResultInCorrectBalanceUnderConcurrentRedeems() {
        int threadCount = 32;
        int redeemsPerThread = 100;

        UUID bigTestCard = UUID.randomUUID();
        BigDecimal bigTestCardInitialBalance = BigDecimal.valueOf(threadCount).multiply(BigDecimal.valueOf(redeemsPerThread));
        service.createGiftCard(bigTestCard, bigTestCardInitialBalance);

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();

                    for(int j = 0; j < redeemsPerThread; j++) {
                        assertDoesNotThrow(() -> {
                            service.redeem(bigTestCard, BigDecimal.ONE);
                        });
                    }
                }  catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    endLatch.countDown();
                }
            });
        }

        assertTimeoutPreemptively(Duration.ofSeconds(5), () -> {
            startLatch.countDown();
            endLatch.await();

            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);
        });

        assertEquals(0, service.getBalance(bigTestCard).compareTo(BigDecimal.ZERO));
    }

    @Test
    void shouldAvoidDoubleRefund() {
        int threadCount = 32;

        UUID redemptionId = service.redeem(testCard, NineNinetyNine);

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();

                        try {
                            service.refund(redemptionId);
                        } catch (IllegalArgumentException e) {
//                            Skip invalid refunds.
                        }
                }  catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    endLatch.countDown();
                }
            });
        }

        assertTimeoutPreemptively(Duration.ofSeconds(5), () -> {
            startLatch.countDown();
            endLatch.await();

            executor.shutdown();
        });

        assertEquals(0, testCardInitialBalance.compareTo(service.getBalance(testCard)));
    }
}