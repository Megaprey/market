package ru.yandex.practicum.market.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@Configuration
@EnableR2dbcRepositories(basePackages = "ru.yandex.practicum.market.repository.r2dbc")
// Теперь репозитории в этом пакете будут привязаны ТОЛЬКО к R2DBC
public class R2dbcConfig {
}
