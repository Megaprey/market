package ru.yandex.practicum.market.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebConfig {

    @Value("${webservers.wallet.host:}")
    String host;

    @Bean
    public WebClient webClient() {
        return WebClient.create().mutate()
                .baseUrl(host)
                .build();
    }
}
