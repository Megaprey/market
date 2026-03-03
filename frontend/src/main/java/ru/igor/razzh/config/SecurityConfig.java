package ru.igor.razzh.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.authentication.logout.RedirectServerLogoutSuccessHandler;
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository;
import org.springframework.security.web.server.csrf.ServerCsrfTokenRequestAttributeHandler;
import org.springframework.security.web.server.csrf.WebSessionServerCsrfTokenRepository;
import org.springframework.web.server.WebFilter;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    // Защищаем пароли шифрованием
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSessionServerCsrfTokenRepository csrfTokenRepository() {
        return new WebSessionServerCsrfTokenRepository();
    }

    // Создаём in-memory пользователя
    @Bean
    public MapReactiveUserDetailsService userDetailsService() {
        UserDetails user = User.withUsername("user")
            .password(passwordEncoder().encode("password"))
            .roles("lababu")
            .build();
        return new MapReactiveUserDetailsService(user);
    }

    // Настраиваем поведение при выходе
    @Bean
    public RedirectServerLogoutSuccessHandler redirectServerLogoutSuccessHandler() {
        RedirectServerLogoutSuccessHandler logoutSuccessHandler = new RedirectServerLogoutSuccessHandler();
        // При выходе перенаправляем его на домашнюю страницу
        logoutSuccessHandler.setLogoutSuccessUrl(URI.create("/"));
        return logoutSuccessHandler;
    }
    @Bean
    WebFilter csrfCookieWebFilter() {
        return (exchange, chain) -> {
            Mono<CsrfToken> csrfToken = exchange.getAttribute(CsrfToken.class.getName());
            if (csrfToken == null) {
                return chain.filter(exchange);
            }
            return csrfToken.flatMap(token -> {
                // Принудительно кладем токен в атрибуты как обычный объект, а не Mono
                exchange.getAttributes().put("_csrf", token);
                // Возвращаем цепочку дальше только после того, как токен вычислен
                return chain.filter(exchange);
            });
        };
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http,
                                                            RedirectServerLogoutSuccessHandler redirectServerLogoutSuccessHandler) {

        var server = CookieServerCsrfTokenRepository.withHttpOnlyFalse();
        server.setCookiePath("/");
        http
            .csrf(csrf -> csrf
                .csrfTokenRepository(server)
                .csrfTokenRequestHandler(new ServerCsrfTokenRequestAttributeHandler())
            )
            .authorizeExchange(exchanges -> exchanges
            .pathMatchers("/static/**", "/login", "/items").permitAll()
//            .pathMatchers("/**").permitAll()
            .anyExchange().authenticated()
        )
            // Настраиваем форму логина
            .formLogin(form -> form
                // URL страницы логина
                .loginPage("/login")
                .authenticationSuccessHandler(
                    // В случае успешного логина, перенаправляем на /items
                    new RedirectServerAuthenticationSuccessHandler("/items")
                )
            )
            // Настраиваем обработку при выходе
            .logout(logout -> logout
                // URL страницы выхода
                .logoutUrl("/logout")
                .logoutSuccessHandler(redirectServerLogoutSuccessHandler)
            )
            // OAuth2 Client для WebClient
            .oauth2Client(withDefaults());

        return http.build();
    }
}