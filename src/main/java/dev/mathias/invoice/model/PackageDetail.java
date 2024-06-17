package dev.mathias.invoice.model;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PackageDetail(PackageType type, int minutes, int sms, BigDecimal price) {
}
