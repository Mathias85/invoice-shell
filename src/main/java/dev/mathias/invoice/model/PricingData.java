package dev.mathias.invoice.model;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record PricingData(List<PackageDetail> packages, BigDecimal pricePerMinute, BigDecimal pricePerSms, String currency) {

    public PackageDetail findPackageDetail(final PackageType type) {
        return packages.stream()
                .filter(packageDetail -> packageDetail.type() == type)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid package type: " + type));
    }
}
