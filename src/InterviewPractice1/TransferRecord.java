package InterviewPractice1;

import java.math.BigDecimal;

public record TransferRecord(long fromUID, long toUID, BigDecimal amount, TransferStatus status) {
}
