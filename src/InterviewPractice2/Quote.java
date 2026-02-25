package InterviewPractice2;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record Quote (
        UUID id,
        CurrencyPair pair,
        BigDecimal rate,
        BigDecimal amount,
        Instant timestamp) {
    public Quote {
        Objects.requireNonNull(id);
        Objects.requireNonNull(pair);
        Objects.requireNonNull(rate);
        Objects.requireNonNull(amount);
        Objects.requireNonNull(timestamp);
    }
}
