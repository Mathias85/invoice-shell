package dev.mathias.invoice.model;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record Invoice(
        PackageType packageType,
        BigDecimal packageCost,
        int extraMinutes,
        BigDecimal extraMinutesCost,
        int extraSms,
        BigDecimal extraSmsCost,
        BigDecimal totalAmount,
        String currency
) {
}
