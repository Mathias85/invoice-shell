package dev.mathias.invoice.service;

import dev.mathias.invoice.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

@Slf4j
@RequiredArgsConstructor
@Service
public class InvoiceService {
    private final PricingService pricingService;

    public Invoice generateInvoice(final UsageInfo usageInfo) {
        log.debug("Generating invoice for package usage: {}", usageInfo);

        final PackageDetail packageDetail = this.pricingService.findPackageDetail(usageInfo.packageType());
        final int exceededMinutes = Math.max(0, usageInfo.minutes() - packageDetail.minutes());
        final int exceededSms = Math.max(0, usageInfo.sms() - packageDetail.sms());
        log.debug("Package {} Limits: {}, {}", packageDetail.type(), packageDetail.minutes(), packageDetail.sms());
        log.debug("Exceeded minutes: {}, exceeded sms: {}", exceededMinutes, exceededSms);

        log.debug("Calculating costs for package usage: {}", usageInfo);
        final BigDecimal extraMinutesCost = this.pricingService.calculateMinutesPrice(exceededMinutes);
        final BigDecimal extraSmsCost = this.pricingService.calculateSmsPrice(exceededSms);

        final BigDecimal totalAmount = this.pricingService.calculateTotalPrice(usageInfo.packageType(), exceededMinutes, exceededSms);
        final BigDecimal packagePrice = this.pricingService.calculatePackagePrice(usageInfo.packageType());
        log.debug("Total amount: {}, package price: {}, extraMinutesCost: {}, extraSmsCost: {}", totalAmount, packagePrice, extraMinutesCost, extraSmsCost);

        return Invoice.builder()
                .packageType(usageInfo.packageType())
                .packageCost(packagePrice)
                .extraMinutes(exceededMinutes)
                .extraMinutesCost(extraMinutesCost)
                .extraSms(exceededSms)
                .extraSmsCost(extraSmsCost)
                .totalAmount(totalAmount)
                .currency(this.pricingService.getCurrency())
                .build();
    }

}
