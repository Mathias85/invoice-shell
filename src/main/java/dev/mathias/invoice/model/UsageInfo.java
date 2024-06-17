package dev.mathias.invoice.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record UsageInfo(@NotNull PackageType packageType, @Min(0) int minutes, @Min(0) int sms) {

    public UsageInfo {
        if (packageType == null) {
            throw new IllegalArgumentException("Package type cannot be null");
        }
        if (minutes < 0) {
            throw new IllegalArgumentException("Minutes must be an integer number greater or equal to 0");
        }
        if (sms < 0) {
            throw new IllegalArgumentException("SMS must be an integer number greater or equal to 0");
        }
    }
}
