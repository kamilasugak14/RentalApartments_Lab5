package com.example.rentalapartments.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Явно створюємо ObjectMapper як @Bean, щоб не залежати від того,
// спрацює автоконфігурація Jackson чи ні. Його використовує Spring MVC
// для перетворення Java-об'єктів у JSON і навпаки (контрольне питання 13),
// а також ApartmentRestController для JSON Merge Patch (PATCH).
@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
}
