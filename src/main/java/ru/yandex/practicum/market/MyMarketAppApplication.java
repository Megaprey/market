package ru.yandex.practicum.market;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.r2dbc.R2dbcDataAutoConfiguration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@SpringBootApplication
public class MyMarketAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyMarketAppApplication.class, args);
	}

}
