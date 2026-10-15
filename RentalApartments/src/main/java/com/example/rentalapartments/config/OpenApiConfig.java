package com.example.rentalapartments.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI rentalApartmentsOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Rental Apartments API")
                .version("1.0")
                .description("REST API для сервісу оренди квартир: пошук, створення, "
                        + "редагування та видалення оголошень про квартири.")
                .contact(new Contact().name("Сугак К.Р., Меньша Д.С.")));
    }
}
