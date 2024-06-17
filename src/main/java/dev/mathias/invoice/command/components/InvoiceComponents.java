package dev.mathias.invoice.command.components;

import dev.mathias.invoice.model.PackageType;
import org.jline.terminal.Terminal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.component.SingleItemSelector;
import org.springframework.shell.component.StringInput;
import org.springframework.shell.component.support.SelectorItem;
import org.springframework.shell.standard.AbstractShellComponent;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class InvoiceComponents extends AbstractShellComponent {

    public PackageType buildPackageSelectionComponent() {
        final  List<SelectorItem<PackageType>> items =
                Arrays.stream(PackageType.values()).map(
                        packageType -> SelectorItem.of(packageType.name(), packageType)
                ).toList();

        final SingleItemSelector<PackageType, SelectorItem<PackageType>> component =
                new SingleItemSelector<>(getTerminal(), items, "Package Type:", null);

        component.setResourceLoader(getResourceLoader());
        component.setTemplateExecutor(getTemplateExecutor());

        final SingleItemSelector.SingleItemSelectorContext<PackageType, SelectorItem<PackageType>> context =
                component.run(SingleItemSelector.SingleItemSelectorContext.empty());

        return context.getResultItem().flatMap(si -> Optional.ofNullable(si.getItem())).orElse(null);
    }

    public Integer buildSMSUsageComponent() {
        try {
            return Integer.parseInt(this.buildStringInputComponent("SMS used: "));
        } catch(NumberFormatException nfe) {
            throw new IllegalArgumentException("SMS must be an integer number greater or equal to 0");
        }
    }

    public Integer buildMinutesUsageComponent() {
        try {
            return Integer.parseInt(this.buildStringInputComponent("Minutes used: "));
        } catch(NumberFormatException nfe) {
            throw new IllegalArgumentException("Minutes must be an integer number greater or equal to 0");
        }
    }


    private String buildStringInputComponent(final String label) {
        final StringInput component = new StringInput(getTerminal(), label, null);
        component.setResourceLoader(getResourceLoader());
        component.setTemplateExecutor(getTemplateExecutor());

        final StringInput.StringInputContext context = component.run(StringInput.StringInputContext.empty());
        return context.getResultValue();
    }

}
