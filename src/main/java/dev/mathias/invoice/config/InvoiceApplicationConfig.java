package dev.mathias.invoice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.shell.jline.PromptProvider;

@Configuration
public class InvoiceApplicationConfig {
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public PromptProvider invoicePromptProvider() {
        return () -> new AttributedString("invoice:>", AttributedStyle.DEFAULT.foreground(AttributedStyle.BLUE));
    }
}
