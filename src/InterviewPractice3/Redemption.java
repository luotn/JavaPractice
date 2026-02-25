package InterviewPractice3;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class Redemption {

    private final UUID cardId;
    private final BigDecimal amount;
    private GiftCardStatus status;

    Redemption(UUID cardId, BigDecimal amount, GiftCardStatus status) {
        Objects.requireNonNull(cardId);
        Objects.requireNonNull(amount);
        Objects.requireNonNull(status);

        this.cardId = cardId;
        this.amount = amount;
        this.status = status;
    }

    void changeStatus(GiftCardStatus newStatus) {
        status = newStatus;
    }

    public UUID getCardId() {
        return cardId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public GiftCardStatus getStatus() {
        return status;
    }
}
