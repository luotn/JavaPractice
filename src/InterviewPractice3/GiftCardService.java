package InterviewPractice3;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GiftCardService {
    private final ConcurrentHashMap<UUID, BigDecimal> giftCards = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, Redemption> redemptions = new ConcurrentHashMap<>();

    public void createGiftCard(UUID cardId, BigDecimal initialBalance) {
        Objects.requireNonNull(cardId);
        Objects.requireNonNull(initialBalance);

        if (giftCards.containsKey(cardId)) {
            throw new IllegalArgumentException("Gift card already exists");
        }
        if (initialBalance.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Initial balance must be greater than zero");
        }

        giftCards.put(cardId, initialBalance);
    }

    public BigDecimal getBalance(UUID cardId) {
        Objects.requireNonNull(cardId);
        if (!giftCards.containsKey(cardId)) {
            throw new IllegalArgumentException("Gift card does not exist");
        }
        return giftCards.get(cardId);
    }

    public UUID redeem(UUID cardId, BigDecimal amount) {
        Objects.requireNonNull(cardId);
        Objects.requireNonNull(amount);
        if (!giftCards.containsKey(cardId)) {throw new IllegalArgumentException("Gift card does not exist");}
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {throw new IllegalArgumentException("Redeem amount must be greater than zero");}

        UUID redemptionId = UUID.randomUUID();
        redemptions.put(redemptionId, new Redemption(cardId, amount, GiftCardStatus.PENDING));
        Redemption redemption = redemptions.get(redemptionId);

        synchronized (giftCards) {
            BigDecimal balanceAfterRedemption = getBalance(cardId).subtract(amount);
            if(balanceAfterRedemption.compareTo(BigDecimal.ZERO) < 0) {
                redemption.changeStatus(GiftCardStatus.FAILED);
                throw new IllegalArgumentException("Insufficient balance");
            }
            giftCards.put(cardId, balanceAfterRedemption);
        }
            redemption.changeStatus(GiftCardStatus.SUCCESS);

        return redemptionId;
    }

    public void refund(UUID redemptionId) {
        Redemption redemption = this.getRedemption(redemptionId);
        synchronized (redemption) {
            UUID cardId = redemption.getCardId();
            BigDecimal amount = redemption.getAmount();
            GiftCardStatus giftCardStatus = redemption.getStatus();

            if (!giftCardStatus.equals(GiftCardStatus.SUCCESS)) { throw new IllegalArgumentException("Gift card status must be SUCCESS");}

            giftCards.put(cardId, giftCards.get(cardId).add(amount));

            redemption.changeStatus(GiftCardStatus.REFUNDED);
        }
    }

    public Redemption getRedemption(UUID redemptionId) {
        Objects.requireNonNull(redemptionId);

        if (!redemptions.containsKey(redemptionId)) { throw new IllegalArgumentException("Redemption does not exist");}
        return redemptions.get(redemptionId);
    }
}
