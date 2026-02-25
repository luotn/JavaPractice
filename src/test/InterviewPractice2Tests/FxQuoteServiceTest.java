package test.InterviewPractice2Tests;

import InterviewPractice2.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

class FxQuoteServiceTest {
    FxQuoteService service;
    CurrencyPair GBPtoCNY;
    BigDecimal GBPtoCNYRate;
    Instant timeStamp;
    CurrencyPair USDtoCNY;
    BigDecimal USDtoCNYRate;

    @BeforeEach
    void setUp() {
        service = new FxQuoteService();
        GBPtoCNY = new CurrencyPair(Currency.GBP, Currency.CNY);
        GBPtoCNYRate = BigDecimal.valueOf(928, 2);
        timeStamp = Instant.now();
        service.setExchangeRate(GBPtoCNY, GBPtoCNYRate, timeStamp);

        USDtoCNY = new CurrencyPair(Currency.USD, Currency.CNY);
        USDtoCNYRate = BigDecimal.valueOf(688, 2);
    }

    @Test
    void shouldSetupRateSuccessful() {
        service.setExchangeRate(USDtoCNY, USDtoCNYRate, timeStamp);
        ExchangeRate exchangeRate = service.getExchangeRate(USDtoCNY);
        assertEquals(exchangeRate.rate(), USDtoCNYRate);
        assertEquals(exchangeRate.timestamp(), timeStamp);
    }

    @Test
    void shouldUpdateExchangeRateSuccessful() {
        service.setExchangeRate(USDtoCNY, USDtoCNYRate, timeStamp);

        BigDecimal newRate = BigDecimal.TEN;
        Instant newTimestamp = Instant.now();
        service.setExchangeRate(USDtoCNY, newRate, newTimestamp);
        ExchangeRate newExchangeRate = service.getExchangeRate(USDtoCNY);
        assertEquals(newRate, newExchangeRate.rate());
        assertEquals(newTimestamp, newExchangeRate.timestamp());
    }

    @Test
    void shouldQuoteSuccessful() {
        BigDecimal initialAmount = BigDecimal.valueOf(100);
        Quote quote = service.quote(initialAmount, GBPtoCNY);
        assertEquals(quote.pair(), GBPtoCNY);
        assertEquals(quote.rate(), GBPtoCNYRate);
        assertEquals(quote.timestamp(), timeStamp);
        BigDecimal quotedAmount = initialAmount.multiply(GBPtoCNYRate);
        assertEquals(quote.amount(), quotedAmount);
    }

    @Test
    void shouldThrowSetupRateWithNegativeRate() {
        BigDecimal rate = BigDecimal.valueOf(-688, 2);
        assertThrows(IllegalArgumentException.class, () -> service.setExchangeRate(USDtoCNY, rate, timeStamp));
    }

    @Test
    void shouldThrowSetupRateWithNulls() {
        assertThrows(NullPointerException.class, () -> service.setExchangeRate(null, USDtoCNYRate, timeStamp));
        assertThrows(NullPointerException.class, () -> service.setExchangeRate(USDtoCNY, null, timeStamp));
        assertThrows(NullPointerException.class, () -> service.setExchangeRate(USDtoCNY, USDtoCNYRate, null));
    }

    @Test
    void shouldThrowGetExchangeRateWithUnknowRate() {
        CurrencyPair pair = new CurrencyPair(Currency.EUR, Currency.CNY);
        assertThrows(NullPointerException.class, () -> service.getExchangeRate(pair));
        assertThrows(NullPointerException.class, () -> service.getExchangeRate(null));
    }

    @Test
    void shouldThrowQuoteWithUnknownExchangeRate() {
        CurrencyPair pair = new CurrencyPair(Currency.EUR, Currency.CNY);
        assertThrows(NullPointerException.class, () -> service.getExchangeRate(pair));
        assertThrows(NullPointerException.class, () -> service.getExchangeRate(null));
    }

    @Test
    void shouldThrowQuoteWithNegativeAndNullAmount() {
        assertThrows(IllegalArgumentException.class, () -> service.quote(BigDecimal.valueOf(-1000), GBPtoCNY));
        assertThrows(NullPointerException.class, () -> service.quote(null, GBPtoCNY));
    }

    @Test
    void shouldAvoidDirtyReads() {
        int THREAD_COUNT = 32;
        int OPERATIONS_PER_THREAD = 10000;
        BigDecimal lowestGBPToCNYRate = BigDecimal.valueOf(774, 2);
        Instant lowestGBPToCNYTimeStamp = Instant.ofEpochSecond(1663578000);

        ExecutorService writeExecutor = Executors.newSingleThreadExecutor();

        ExecutorService readExecutor = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(THREAD_COUNT);
        ArrayList<Quote> quotes = new ArrayList<>();

//        Submit threads
        for (int i = 0; i < THREAD_COUNT; i++) {
            int threadNum = i;
            readExecutor.submit(() -> {
                try {
                    startLatch.await();

                    for (int j = 0; j < OPERATIONS_PER_THREAD; j++) {
                        if (threadNum % 2 == 0) {
                            ThreadLocalRandom random = ThreadLocalRandom.current();
                            quotes.add(service.quote(BigDecimal.valueOf(random.nextInt()), GBPtoCNY));
                        } else {
                            service.setExchangeRate(
                                    GBPtoCNY,
                                    lowestGBPToCNYRate,
                                    lowestGBPToCNYTimeStamp);
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

//        Start threads
        assertTimeoutPreemptively(Duration.ofSeconds(5), () -> {
            startLatch.countDown();
            doneLatch.await();

            writeExecutor.shutdown();
            readExecutor.shutdown();
            readExecutor.awaitTermination(5, TimeUnit.SECONDS);
        });

//        Validate Results
        for(Quote quote : quotes) {
            assertEquals(quote.pair(), GBPtoCNY);
            assertEquals(quote.rate(), lowestGBPToCNYRate);
            assertEquals(quote.timestamp(), lowestGBPToCNYTimeStamp);
        }
    }
}