package InterviewPractice2;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

public record ExchangeRate (
        BigDecimal rate,
        Instant timestamp
){
    public ExchangeRate {
        Objects.requireNonNull(rate);
        Objects.requireNonNull(timestamp);
    }
}
