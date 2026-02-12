package ru.yandex.practicum.market.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.yandex.practicum.market.config.TestR2dbcConfig;
import ru.yandex.practicum.market.entity.Item;
import ru.yandex.practicum.market.entity.Order;
import ru.yandex.practicum.market.entity.User;
import ru.yandex.practicum.market.repository.ItemRepository;
import ru.yandex.practicum.market.repository.OrderRepository;
import ru.yandex.practicum.market.repository.UserRepository;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@ImportTestcontainers(PostgreSQLTestContainer.class)
@Import({TestR2dbcConfig.class})
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private PaymentClient paymentClient;

    private User testUser;

    @BeforeEach
    void setUp() {
        itemRepository.deleteAll()
                .then(orderRepository.deleteAll())
                .then(userRepository.deleteAll())
                .then(Mono.defer(() -> {
                    User user = User.builder()
                            .username("testuser")
                            .password(passwordEncoder.encode("testpass"))
                            .role("ROLE_USER")
                            .build();
                    return userRepository.save(user);
                }))
                .flatMap(savedUser -> {
                    testUser = savedUser;

                    Order order1 = Order.builder()
                            .totalSum(BigDecimal.valueOf(1000))
                            .userId(savedUser.getId())
                            .build();
                    Order order2 = Order.builder()
                            .totalSum(BigDecimal.valueOf(4000))
                            .userId(savedUser.getId())
                            .build();

                    return orderRepository.save(order1)
                            .flatMap(savedOrder1 ->
                                    orderRepository.save(order2)
                                            .flatMap(savedOrder2 -> {
                                                Item item1 = Item.builder()
                                                        .title("Мяч")
                                                        .description("Футбольный мяч")
                                                        .price(BigDecimal.valueOf(1000))
                                                        .count(1)
                                                        .imgPath("ball.jpg")
                                                        .checkOrder(true)
                                                        .orderId(savedOrder1.getId())
                                                        .build();
                                                Item item2 = Item.builder()
                                                        .title("Хоккей")
                                                        .description("Настольный хоккей")
                                                        .price(BigDecimal.valueOf(2000))
                                                        .count(2)
                                                        .imgPath("hockey.jpg")
                                                        .checkOrder(true)
                                                        .orderId(savedOrder2.getId())
                                                        .build();
                                                return itemRepository.save(item1)
                                                        .then(itemRepository.save(item2));
                                            })
                            );
                })
                .block();
    }

    @Test
    void findAllByUsername_shouldReturnUserOrders() {
        StepVerifier.create(orderService.findAllByUsername("testuser").collectList())
                .assertNext(orders -> {
                    assertThat(orders).hasSize(2);
                    assertThat(orders)
                            .extracting(Order::getTotalSum)
                            .containsExactlyInAnyOrder(
                                    BigDecimal.valueOf(1000),
                                    BigDecimal.valueOf(4000)
                            );
                })
                .verifyComplete();
    }

    @Test
    void findAllByUsername_shouldReturnEmptyForOtherUser() {
        StepVerifier.create(orderService.findAllByUsername("otheruser").collectList())
                .assertNext(orders -> assertThat(orders).isEmpty())
                .verifyComplete();
    }

    @Test
    void findByIdAndUsername_withCorrectUser_shouldReturnOrder() {
        Order savedOrder = orderRepository.findAll()
                .collectList()
                .map(list -> list.get(0))
                .block();

        StepVerifier.create(orderService.findByIdAndUsername(savedOrder.getId(), "testuser"))
                .assertNext(foundOrder -> {
                    assertThat(foundOrder).isNotNull();
                    assertThat(foundOrder.getId()).isEqualTo(savedOrder.getId());
                })
                .verifyComplete();
    }

    @Test
    void findByIdAndUsername_withWrongUser_shouldReturnEmpty() {
        Order savedOrder = orderRepository.findAll()
                .collectList()
                .map(list -> list.get(0))
                .block();

        StepVerifier.create(orderService.findByIdAndUsername(savedOrder.getId(), "wronguser"))
                .verifyComplete();
    }
}
