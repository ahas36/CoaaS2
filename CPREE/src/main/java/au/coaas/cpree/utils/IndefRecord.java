package au.coaas.cpree.utils;

import java.time.LocalDateTime;

public class IndefRecord {
    private Double expAR;
    private LocalDateTime expiryTime;

    public IndefRecord(double expAR, LocalDateTime expiry){
        this.expAR = expAR;
        this.expiryTime = expiry;
    }

    public Double getExpAR() {
        return expAR;
    }

    public LocalDateTime getExpiryTime() {
        return expiryTime;
    }
}

