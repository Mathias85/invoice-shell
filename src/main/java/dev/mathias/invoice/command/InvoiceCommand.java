package dev.mathias.invoice.command;

import dev.mathias.invoice.command.components.InvoiceComponents;
import dev.mathias.invoice.model.Invoice;
import dev.mathias.invoice.model.PackageType;
import dev.mathias.invoice.model.UsageInfo;
import dev.mathias.invoice.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jline.terminal.Terminal;
import org.springframework.shell.command.CommandExecution;
import org.springframework.shell.command.CommandHandlingResult;
import org.springframework.shell.command.CommandParser;
import org.springframework.shell.command.annotation.ExceptionResolver;
import org.springframework.shell.standard.*;
import org.springframework.shell.table.ArrayTableModel;
import org.springframework.shell.table.BorderStyle;
import org.springframework.shell.table.TableBuilder;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@ShellComponent
@ShellCommandGroup("Invoice Generation")
public class InvoiceCommand extends AbstractShellComponent {

    private final InvoiceService invoiceService;

    private final InvoiceComponents invoiceComponents;

    private final Terminal terminal;

    @ShellMethod(key = "gen", value = "Generates invoice based on package type and usage. [interactive mode]")
    public void gen() {

        final PackageType packageType = this.invoiceComponents.buildPackageSelectionComponent();
        final Integer smsUsage = this.invoiceComponents.buildSMSUsageComponent();
        final Integer minutesUsage = this.invoiceComponents.buildMinutesUsageComponent();

        this.generate(packageType, smsUsage, minutesUsage);
    }

    @ShellMethod(key = "generate", value = "Generates invoice based on package type and usage. [non-interactive mode]")
    public void generate(
            @ShellOption(value = { "package", "-p" }, help = "Package type (Options: S, M, L)" ) final PackageType packageType,
            @ShellOption(value = {"sms", "-s"}, help = "Actual SMS used" ) final int smsAmount,
            @ShellOption(value = {"minutes", "-m"}, help = "Actual minutes used" )
            final int minutes
    ) {

        log.debug("Generating invoice for package type: {}, sms amount: {}, minutes amount: {}", packageType, smsAmount, minutes);
        final UsageInfo usageInfo = UsageInfo.builder()
                .packageType(packageType)
                .minutes(minutes)
                .sms(smsAmount)
                .build();

        final Invoice invoice = this.invoiceService.generateInvoice(usageInfo);
        log.debug("Invoice generated: {}", invoice);
        this.generateOutput(invoice);
        log.debug("Invoice generated successfully");
    }

    private void generateOutput(final Invoice invoice) {
        log.debug("Generating output");
        terminal.writer().println();
        terminal.writer().println("---------------------------------------");
        terminal.writer().println("Detailed invoice:");

        final List<String[]> outputRows = new ArrayList<>();
        outputRows.add(new String[]{String.format("%s (%s)", "Package", invoice.packageType()), String.format("%s %s", invoice.packageCost().toString(), invoice.currency())});
        if (invoice.extraMinutes() > 0) {
            outputRows.add(new String[]{String.format("Extra minutes (%s)", invoice.extraMinutes()), String.format(" %s %s", invoice.extraMinutesCost().toString(), invoice.currency())});
        }
        if (invoice.extraSms() > 0) {
            outputRows.add(new String[]{String.format("Extra SMS (%s)", invoice.extraSms()), String.format(" %s %s", invoice.extraSmsCost().toString(), invoice.currency())});
        }
        outputRows.add(new String[]{"", ""});
        outputRows.add(new String[]{String.format("%-25s", "Total"), String.format("%s %s", invoice.totalAmount().toString(), invoice.currency())});

        final Object[][] outputTable = outputRows.toArray(new Object[0][]);
        final ArrayTableModel model = new ArrayTableModel(outputTable);
        final TableBuilder tableBuilder = new TableBuilder(model);
        tableBuilder.addFullBorder(BorderStyle.fancy_light);

        terminal.writer().println(tableBuilder.build().render(80));
        terminal.writer().println("---------------------------------------");
        terminal.writer().println();
        terminal.flush();
        log.debug("Output generated successfully");
    }

    @ExceptionResolver({ IllegalArgumentException.class })
    CommandHandlingResult illegalArgumentExceptionHandler(IllegalArgumentException e) {
        log.error("Invalid Input: {}", e.getMessage());
        return CommandHandlingResult.of(e.getMessage() + System.lineSeparator());
    }

    @ExceptionResolver({ Exception.class })
    CommandHandlingResult exceptionHandler(Exception e) {
        if (e instanceof CommandExecution.CommandParserExceptionsException cpee) {
            throw cpee;
        }
        log.error("Unexpected Error: {}", e.getMessage());
        return CommandHandlingResult.of("Unexpected Error: " + e.getMessage() + System.lineSeparator());
    }
}
