package dev.mathias.invoice;

import dev.mathias.invoice.command.components.InvoiceComponents;
import dev.mathias.invoice.model.Invoice;
import dev.mathias.invoice.model.PackageType;
import dev.mathias.invoice.model.UsageInfo;
import dev.mathias.invoice.service.InvoiceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.shell.test.ShellAssertions;
import org.springframework.shell.test.ShellTestClient;
import org.springframework.shell.test.autoconfigure.ShellTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.aot.DisabledInAotMode;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ShellTest
@ExtendWith(OutputCaptureExtension.class)
@DisabledInAotMode
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class InvoiceApplicationTests {

	@Autowired
	ShellTestClient client;

	@MockBean
	InvoiceService invoiceService;

	@SpyBean
	InvoiceComponents invoiceComponents;

	@Test
	void shouldShowInvoiceGenerateCommand(CapturedOutput output) {
		final ShellTestClient.InteractiveShellSession session = client
				.interactive()
				.run();

		assertThat(output).contains("Invoice generator application");
		assertThat(output).contains("generate [--package S|M|L] [--sms number] [--minutes number] --help");

		await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> {
			ShellAssertions.assertThat(session.screen())
					.containsText("shell");
		});

		session.write(session.writeSequence().text("help").carriageReturn().build());
		await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> {
			ShellAssertions.assertThat(session.screen())
					.containsText("Invoice Generation");
			ShellAssertions.assertThat(session.screen())
					.containsText("generate: Generates invoice based on package type and usage.");
		});
	}

	@Test
	void shouldShowInvoiceGenerateCommandOptions() {
		final ShellTestClient.InteractiveShellSession session = client
				.interactive()
				.run();

		await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> {
			ShellAssertions.assertThat(session.screen())
					.containsText("shell");
		});

		session.write(session.writeSequence().text("generate -h").carriageReturn().build());
		await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> {
			ShellAssertions.assertThat(session.screen())
					.containsText("--package or -p");
			ShellAssertions.assertThat(session.screen())
					.containsText("Package type (Options: S, M, L)");

			ShellAssertions.assertThat(session.screen())
					.containsText("--sms or -s");
			ShellAssertions.assertThat(session.screen())
					.containsText("Actual SMS used");

			ShellAssertions.assertThat(session.screen())
					.containsText("--minutes or -m");
			ShellAssertions.assertThat(session.screen())
					.containsText("Actual minutes used");
		});
	}

	@Test
	void shouldGenerateInvoice_PackageTypeL(CapturedOutput output) {

		when(invoiceService.generateInvoice(any(UsageInfo.class))).thenReturn(Invoice.builder()
				.packageType(PackageType.L)
				.packageCost(new BigDecimal("20.00"))
				.extraMinutes(0)
				.extraMinutesCost(new BigDecimal("0.00"))
				.extraSms(0)
				.extraSmsCost(new BigDecimal("0.00"))
				.totalAmount(new BigDecimal("20.00"))
				.currency("EUR")
				.build());

		final ShellTestClient.InteractiveShellSession session = client
				.interactive()
				.run();

		await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> {
			ShellAssertions.assertThat(session.screen())
					.containsText("shell");
		});

		session.write(session.writeSequence()
				.text("generate --package L --minutes 10 --sms 300")
				.carriageReturn().carriageReturn()
				.build());

		await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
			ShellAssertions.assertThat(session.screen())
					.containsText("Detailed invoice:");

			ShellAssertions.assertThat(session.screen())
					.containsText("Package (L)");
			ShellAssertions.assertThat(session.screen())
					.containsText("20.00 EUR");

			ShellAssertions.assertThat(session.screen())
					.containsText("Total");
			ShellAssertions.assertThat(session.screen())
					.containsText("20.00 EUR");
		});
	}


	@Test
	void shouldGenerateInvoice_PackageTypeS() {

		when(invoiceService.generateInvoice(any(UsageInfo.class))).thenReturn(Invoice.builder()
				.packageType(PackageType.S)
				.packageCost(new BigDecimal("10.00"))
				.extraMinutes(30)
				.extraMinutesCost(new BigDecimal("9.00"))
				.extraSms(10)
				.extraSmsCost(new BigDecimal("2.00"))
				.totalAmount(new BigDecimal("31.00"))
				.currency("EUR")
				.build());

		final ShellTestClient.InteractiveShellSession session = client
				.interactive()
				.run();

		await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> {
			ShellAssertions.assertThat(session.screen())
					.containsText("shell");
		});

		session.write(session.writeSequence()
				.text("generate --package L --minutes 10 --sms 300")
				.carriageReturn().carriageReturn()
				.build());

		await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
			ShellAssertions.assertThat(session.screen())
					.containsText("Detailed invoice:");

			ShellAssertions.assertThat(session.screen())
					.containsText("Package (S)");
			ShellAssertions.assertThat(session.screen())
					.containsText("10.00 EUR");

			ShellAssertions.assertThat(session.screen())
					.containsText("Extra minutes (30) ");
			ShellAssertions.assertThat(session.screen())
					.containsText("9.00 EUR");

			ShellAssertions.assertThat(session.screen())
					.containsText("Extra SMS (10) ");
			ShellAssertions.assertThat(session.screen())
					.containsText("2.00 EUR");

			ShellAssertions.assertThat(session.screen())
					.containsText("Total");
			ShellAssertions.assertThat(session.screen())
					.containsText("31.00 EUR");
		});
	}

	@Test
	void shouldNotGenerateInvoice_InvalidPackage() {

		final ShellTestClient.InteractiveShellSession session = client
				.interactive()
				.run();

		await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> {
			ShellAssertions.assertThat(session.screen())
					.containsText("shell");
		});

		session.write(session.writeSequence()
				.text("generate --package X --minutes Y --sms Z")
				.carriageReturn().carriageReturn()
				.build());

		await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
			ShellAssertions.assertThat(session.screen())
					.containsText("Illegal option value 'X'");
		});
	}

	@Test
	void shouldNotGenerateInvoice_NegativeMinutes() {

		final ShellTestClient.InteractiveShellSession session = client
				.interactive()
				.run();

		await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> {
			ShellAssertions.assertThat(session.screen())
					.containsText("shell");
		});

		session.write(session.writeSequence()
				.text("generate --package L --minutes -10 --sms 10")
				.carriageReturn().carriageReturn()
				.build());

		await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
			ShellAssertions.assertThat(session.screen())
					.containsText("Minutes must be an integer number greater or equal to 0");
		});
	}

	@Test
	void shouldNotGenerateInvoice_NegativeSMS() {

		final ShellTestClient.InteractiveShellSession session = client
				.interactive()
				.run();

		await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> {
			ShellAssertions.assertThat(session.screen())
					.containsText("shell");
		});

		session.write(session.writeSequence()
				.text("generate --package S --minutes 100 --sms -20")
				.carriageReturn().carriageReturn()
				.build());

		await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
			ShellAssertions.assertThat(session.screen())
					.containsText("SMS must be an integer number greater or equal to 0");
		});
	}
}
