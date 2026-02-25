package InterviewPractice2;

import java.time.Instant;
import java.util.Objects;

public record CurrencyPair (
    Currency fromCurrency,
    Currency toCurrency
    ) {

    public CurrencyPair {
        Objects.requireNonNull(fromCurrency);
        Objects.requireNonNull(toCurrency);
    }

}
