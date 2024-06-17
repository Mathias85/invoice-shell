package dev.mathias.invoice.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.mathias.invoice.model.PackageDetail;
import dev.mathias.invoice.model.PackageType;
import dev.mathias.invoice.model.PricingData;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

@Slf4j
@Service
public class PricingService {

    private final PricingData pricingData;
    private final Currency currency;

    PricingService(final ObjectMapper objectMapper) {
        log.debug("Loading pricing data from /data/pricing.json");
        try (final InputStream inputStream = TypeReference.class.getResourceAsStream("/data/pricing.json")) {
            pricingData = objectMapper.readValue(inputStream, PricingData.class);
            currency = Currency.getInstance(pricingData.currency());

            log.debug("Pricing data loaded successfully");
        } catch (IOException e) {
            log.error("Failed to load pricing data", e);
            throw new RuntimeException("Failed to read JSON data", e);
        }
    }

    public PackageDetail findPackageDetail(final PackageType type) {
        return pricingData.findPackageDetail(type);
    }

    public BigDecimal calculateMinutesPrice(final int minutes) {
        final BigDecimal minutesPrice = pricingData.pricePerMinute().multiply(BigDecimal.valueOf(minutes));
        return roundForCurrency(minutesPrice);
    }

    public BigDecimal calculateSmsPrice(final int sms) {
        final BigDecimal smsPrice = pricingData.pricePerSms().multiply(BigDecimal.valueOf(sms));
        return roundForCurrency(smsPrice);
    }

    public BigDecimal calculatePackagePrice(final PackageType type) {
        final BigDecimal price = pricingData.findPackageDetail(type).price();
        return roundForCurrency(price);
    }

    public BigDecimal calculateTotalPrice(final PackageType type,final int minutes, final int sms) {
        final BigDecimal totalPrice = calculatePackagePrice(type).add(calculateMinutesPrice(minutes).add(calculateSmsPrice(sms)));
        return roundForCurrency(totalPrice);
    }

    public String getCurrency() {
        return currency.getCurrencyCode();
    }

    private BigDecimal roundForCurrency(final BigDecimal amount) {
        return amount.setScale(currency.getDefaultFractionDigits(), RoundingMode.HALF_UP);
    }
}
