package net.troja.eve.pve;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PvEApplication {
    public static void main(final String[] args) {
        SpringApplication.run(PvEApplication.class, args);
    }

    // @Bean
    // public SpringTemplateEngine templateEngine(final ITemplateResolver
    // templateResolver, final SpringSecurityDialect sec) {
    // final SpringTemplateEngine templateEngine = new SpringTemplateEngine();
    // templateEngine.setTemplateResolver(templateResolver);
    // templateEngine.addDialect(sec);
    // return templateEngine;
    // }
}
