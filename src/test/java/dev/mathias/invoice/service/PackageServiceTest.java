package dev.mathias.invoice.service;

import dev.mathias.invoice.model.Invoice;
import dev.mathias.invoice.model.PackageType;
import dev.mathias.invoice.model.UsageInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
public class PackageServiceTest {

    @Autowired
    private InvoiceService invoiceService;

    @Test
    public void shouldGenerateInvoice_forPackageS_with10MinutesAnd5Sms() {
        final UsageInfo usageInfo = UsageInfo.builder()
                .packageType(PackageType.S)
                .minutes(10)
                .sms(5)
                .build();

        final Invoice invoice = invoiceService.generateInvoice(usageInfo);

        assertThat(invoice).isNotNull();
        assertThat(invoice.packageType()).isEqualTo(PackageType.S);
        assertThat(invoice.packageCost()).isEqualTo(new BigDecimal("5.00"));
        assertThat(invoice.extraMinutes()).isEqualTo(0);
        assertThat(invoice.extraMinutesCost()).isEqualTo(new BigDecimal("0.00"));
        assertThat(invoice.extraSms()).isEqualTo(0);
        assertThat(invoice.extraSmsCost()).isEqualTo(new BigDecimal("0.00"));
        assertThat(invoice.totalAmount()).isEqualTo(new BigDecimal("5.00"));
        assertThat(invoice.currency()).isEqualTo("EUR");

    }

    @Test
    public void shouldGenerateInvoice_forPackageM_with10MinutesAnd300Sms() {
        final UsageInfo usageInfo = UsageInfo.builder()
                .packageType(PackageType.M)
                .minutes(10)
                .sms(300)
                .build();

        final Invoice invoice = invoiceService.generateInvoice(usageInfo);

        assertThat(invoice).isNotNull();
        assertThat(invoice.packageType()).isEqualTo(PackageType.M);
        assertThat(invoice.packageCost()).isEqualTo(new BigDecimal("10.00"));
        assertThat(invoice.extraMinutes()).isEqualTo(0);
        assertThat(invoice.extraMinutesCost()).isEqualTo(new BigDecimal("0.00"));
        assertThat(invoice.extraSms()).isEqualTo(200);
        assertThat(invoice.extraSmsCost()).isEqualTo(new BigDecimal("60.00"));
        assertThat(invoice.totalAmount()).isEqualTo(new BigDecimal("70.00"));
        assertThat(invoice.currency()).isEqualTo("EUR");

    }

    @Test
    public void shouldGenerateInvoice_forPackageL_with3000MinutesAnd100Sms() {
        final UsageInfo usageInfo = UsageInfo.builder()
                .packageType(PackageType.L)
                .minutes(3000)
                .sms(100)
                .build();

        final Invoice invoice = invoiceService.generateInvoice(usageInfo);
        System.out.println(invoice);

        assertThat(invoice).isNotNull();
        assertThat(invoice.packageType()).isEqualTo(PackageType.L);
        assertThat(invoice.packageCost()).isEqualTo(new BigDecimal("20.00"));
        assertThat(invoice.extraMinutes()).isEqualTo(2500);
        assertThat(invoice.extraMinutesCost()).isEqualTo(new BigDecimal("500.00"));
        assertThat(invoice.extraSms()).isEqualTo(0);
        assertThat(invoice.extraSmsCost()).isEqualTo(new BigDecimal("0.00"));
        assertThat(invoice.totalAmount()).isEqualTo(new BigDecimal("520.00"));
        assertThat(invoice.currency()).isEqualTo("EUR");
    }

    @Test
    public void shouldGenerateInvoice_forPackageL_with250MinutesAnd250Sms() {
        final UsageInfo usageInfo = UsageInfo.builder()
                .packageType(PackageType.L)
                .minutes(250)
                .sms(250)
                .build();

        final Invoice invoice = invoiceService.generateInvoice(usageInfo);
        System.out.println(invoice);

        assertThat(invoice).isNotNull();
        assertThat(invoice.packageType()).isEqualTo(PackageType.L);
        assertThat(invoice.packageCost()).isEqualTo(new BigDecimal("20.00"));
        assertThat(invoice.extraMinutes()).isEqualTo(0);
        assertThat(invoice.extraMinutesCost()).isEqualTo(new BigDecimal("0.00"));
        assertThat(invoice.extraSms()).isEqualTo(0);
        assertThat(invoice.extraSmsCost()).isEqualTo(new BigDecimal("0.00"));
        assertThat(invoice.totalAmount()).isEqualTo(new BigDecimal("20.00"));
        assertThat(invoice.currency()).isEqualTo("EUR");
    }

    @Test
    public void shouldShouldThrowIllegalArgumentException_forPackageNull() {
        assertThatThrownBy(() -> invoiceService.generateInvoice(UsageInfo.builder().build()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid package type: null");
    }

}
