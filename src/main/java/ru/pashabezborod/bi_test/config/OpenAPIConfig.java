package ru.pashabezborod.bi_test.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI myOpenAPI() {

        Contact contact = new Contact();
        contact.setEmail("paul.bezborodov@yandex.ru");
        contact.setName("Pavel Bezborodov");

        Info info = new Info()
                .title("Run Tracker")
                .version("1.0")
                .contact(contact);

        return new OpenAPI().info(info);
    }
}
