package InterviewPractice4;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ScheduledPaymentService {
    private final ConcurrentHashMap<UUID, User> users = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, Payment> payments = new ConcurrentHashMap<>();

    public void createUser(UUID userId, BigDecimal initialBalance) {
        Objects.requireNonNull(userId, "userId is null");
        Objects.requireNonNull(initialBalance, "initialBalance is null");
        if (users.containsKey(userId)) { throw new IllegalArgumentException("user already exists"); }
        if (initialBalance.compareTo(BigDecimal.ZERO) <= 0) { throw new IllegalArgumentException("Initial balance must be greater than zero"); }

        users.put(userId, new User(initialBalance));
    }

    public UUID schedulePayment(UUID userId, UUID payeeId, BigDecimal amount, Instant executeAt) {
        Objects.requireNonNull(userId, "userId is null");
        Objects.requireNonNull(payeeId, "payeeId is null");
        Objects.requireNonNull(amount, "amount is null");
        Objects.requireNonNull(executeAt, "executeAt is null");
        if (!users.containsKey(userId)) { throw new IllegalArgumentException("user does not exist"); }
        if (!users.containsKey(payeeId)) { throw new IllegalArgumentException("payee does not exist"); }
        if (userId.compareTo(payeeId) == 0) {throw new IllegalArgumentException("Self payment"); }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) { throw new IllegalArgumentException("Invalid amount"); }
        if (executeAt.isBefore(Instant.now())) { throw new IllegalArgumentException("executeAt must be in future"); }

        UUID paymentId = UUID.randomUUID();
        payments.put(paymentId, new Payment(userId, payeeId, amount, executeAt));

        return paymentId;
    }

    public void runDuePayments(Instant time) {
        ArrayList<Payment> duePayments = new ArrayList<>();
//        payments uses ConcurrentHashMap that is multi-thread safe
        for (UUID uuid : payments.keySet()) {
            Payment payment = payments.get(uuid);
            synchronized (payment) {
                if (time.isAfter(payment.getExecuteAt()) &&
                        (payment.getStatus() == ScheduledPaymentStatus.SCHEDULED ||
                                payment.getStatus() == ScheduledPaymentStatus.FAILED)) {
                    payment.updateStatus(ScheduledPaymentStatus.PENDING);
                    duePayments.add(payment);
                }
            }
        }

        for (Payment payment : duePayments) {
//            Users uses concurrentHashMap that is multi-thread safe
            User user = users.get(payment.getUserId());
            User payee = users.get(payment.getPayeeId());
            BigDecimal amount = payment.getAmount();
            synchronized (payment) {
                payment.attempt();
            }

//            Avoid deadlock
            UUID firstUserUUID = payment.getUserId();
            UUID secondUserUUID = payment.getPayeeId();
            if (firstUserUUID.compareTo(secondUserUUID) > 0) {
                firstUserUUID = payment.getPayeeId();
                secondUserUUID = payment.getUserId();
            }
            try {
                synchronized (firstUserUUID) {
                    synchronized (secondUserUUID) {
                        user.withdraw(amount);
                        payee.deposit(amount);
                    }
                }
            } catch (Exception e) {
                synchronized (payment) {
                    payment.updateStatus(ScheduledPaymentStatus.FAILED);
                }
                throw e;
            }

            synchronized (payment) {
                payment.updateStatus(ScheduledPaymentStatus.SUCCESS);
            }
        }
    }

    public Payment getPayment(UUID paymentId) {
        Objects.requireNonNull(paymentId, "paymentId is null");
        if (!payments.containsKey(paymentId)) { throw new IllegalArgumentException("Payment not found"); }
        return payments.get(paymentId);
    }

    public User getUser(UUID userId) {
        Objects.requireNonNull(userId, "userId is null");
        if (!users.containsKey(userId)) { throw new IllegalArgumentException("User not found"); }
        return users.get(userId);
    }
}
