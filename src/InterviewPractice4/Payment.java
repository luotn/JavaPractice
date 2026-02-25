package InterviewPractice4;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Payment {
    private final UUID userId;
    private final UUID payeeId;
    private final BigDecimal amount;
    private final Instant executeAt;
    private ScheduledPaymentStatus status;
    private int attempts = 0;

    public Payment(UUID userId, UUID payeeId, BigDecimal amount, Instant executeAt) {
        Objects.requireNonNull(userId);
        Objects.requireNonNull(payeeId);
        Objects.requireNonNull(amount);
        Objects.requireNonNull(executeAt);

        this.userId = userId;
        this.payeeId = payeeId;
        this.amount = amount;
        this.executeAt = executeAt;
        status = ScheduledPaymentStatus.SCHEDULED;
    }

    void updateStatus(ScheduledPaymentStatus status) {
        Objects.requireNonNull(status);

        this.status = status;
    }

    void attempt() {
        this.attempts++;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getPayeeId() {
        return payeeId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Instant getExecuteAt() {
        return executeAt;
    }

    public ScheduledPaymentStatus getStatus() {
        return status;
    }

    public int getAttempts() {
        return attempts;
    }
}
