package au.coaas.cpree.utils;

import java.time.LocalDateTime;

public class DefRecord {
    private LocalDateTime delayTime;
    private LocalDateTime expiryTime;

    public DefRecord(LocalDateTime delayTime, LocalDateTime expiry) {
        this.delayTime = delayTime;
        this.expiryTime = expiry;
    }

    public LocalDateTime getDelayTime() {
        return delayTime;
    }

    public LocalDateTime getExpiryTime() {
        return expiryTime;
    }
}
