package com.example.rentalapartments.config;

import com.example.rentalapartments.model.SearchLog;
import com.example.rentalapartments.util.PriceFormatter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class AppConfig {

    @Bean
    public PriceFormatter priceFormatter() {
        return new PriceFormatter();
    }

    @Bean
    @Scope("prototype")
    public SearchLog searchLog() {
        return new SearchLog();
    }
}