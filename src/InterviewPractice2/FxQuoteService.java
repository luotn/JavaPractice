package InterviewPractice2;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class FxQuoteService {
    private final ConcurrentHashMap<CurrencyPair, ExchangeRate> exchangeRates = new ConcurrentHashMap<>();

    public void setExchangeRate(CurrencyPair pair, BigDecimal rate, Instant timestamp) {
        if (pair == null) throw new NullPointerException("Currency pair cannot be null");
        if (rate.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Rate cannot be negative");
        synchronized (exchangeRates) {
            this.exchangeRates.put(pair, new ExchangeRate(rate, timestamp));
        }
    }

    public ExchangeRate getExchangeRate(CurrencyPair pair) {
        ExchangeRate exchangeRate = exchangeRates.get(pair);
        if (exchangeRate == null) {throw new NullPointerException("Exchange rate not found");}
        return this.exchangeRates.get(pair);
    }

    public Quote quote(BigDecimal amount, CurrencyPair pair) {
        if (amount == null) throw new NullPointerException("Amount cannot be null");
        if (amount.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Amount cannot be negative");
        if (pair == null) throw new NullPointerException("Currency pair cannot be null");
        ExchangeRate exchangeRate = this.exchangeRates.get(pair);
        BigDecimal quotedAmount = exchangeRate.rate().multiply(amount);
        return new Quote(UUID.randomUUID(), pair, exchangeRate.rate(), quotedAmount, exchangeRate.timestamp());
    }
}
