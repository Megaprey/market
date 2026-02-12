package ru.yandex.practicum.market.config;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import ru.yandex.practicum.market.entity.User;
import ru.yandex.practicum.market.repository.UserRepository;

import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        userRepository.count()
                .filter(count -> count == 0)
                .flatMapMany(count -> {
                    List<User> users = List.of(
                            User.builder()
                                    .username("user1")
                                    .password(passwordEncoder.encode("password1"))
                                    .role("ROLE_USER")
                                    .build(),
                            User.builder()
                                    .username("user2")
                                    .password(passwordEncoder.encode("password2"))
                                    .role("ROLE_USER")
                                    .build()
                    );
                    return Flux.fromIterable(users).flatMap(userRepository::save);
                })
                .doOnNext(user -> log.info("Создан пользователь: {}", user.getUsername()))
                .blockLast();
    }
}
