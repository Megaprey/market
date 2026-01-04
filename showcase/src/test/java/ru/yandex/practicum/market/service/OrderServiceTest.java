package ru.yandex.practicum.market.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.yandex.practicum.market.config.TestR2dbcConfig;
import ru.yandex.practicum.market.entity.Item;
import ru.yandex.practicum.market.entity.Order;
import ru.yandex.practicum.market.repository.ItemRepository;
import ru.yandex.practicum.market.repository.OrderRepository;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@ImportTestcontainers(PostgreSQLTestContainer.class)
@Import({TestR2dbcConfig.class, OrderService.class})
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ItemRepository itemRepository;

    @BeforeEach
    void setUp() {
        // Очищаем данные в реактивном стиле
        itemRepository.deleteAll()
                .then(orderRepository.deleteAll())
                .then(Mono.defer(() -> {
                    // Создаем и сохраняем заказы
                    Order order1 = Order.builder()
                            .totalSum(BigDecimal.valueOf(1000))
                            .build();

                    Order order2 = Order.builder()
                            .totalSum(BigDecimal.valueOf(4000))
                            .build();

                    return orderRepository.save(order1)
                            .flatMap(savedOrder1 -> {
                                order1.setId(savedOrder1.getId());
                                return orderRepository.save(order2);
                            })
                            .flatMap(savedOrder2 -> {
                                order2.setId(savedOrder2.getId());

                                // Создаем товары и связываем с заказами
                                Item item1InOrder = Item.builder()
                                        .title("Мяч")
                                        .description("Футбольный мяч")
                                        .price(BigDecimal.valueOf(1000))
                                        .count(1)
                                        .imgPath("ball.jpg")
                                        .checkOrder(true)
                                        .orderId(order1.getId())
                                        .build();

                                Item item2InOrder = Item.builder()
                                        .title("Хоккей")
                                        .description("Настольный хоккей")
                                        .price(BigDecimal.valueOf(2000))
                                        .count(2)
                                        .imgPath("hockey.jpg")
                                        .checkOrder(true)
                                        .orderId(order2.getId())
                                        .build();

                                return itemRepository.save(item1InOrder)
                                        .then(itemRepository.save(item2InOrder));
                            });
                }))
                .block();
    }

    @Test
    void findAll_shouldReturnAllOrders() {
        // when
        Flux<Order> ordersFlux = orderService.findAll();

        // then
        StepVerifier.create(ordersFlux.collectList())
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
    void findById_withExistingId_shouldReturnOrder() {
        // given
        Order savedOrder = orderRepository.findAll()
                .collectList()
                .map(list -> list.get(0))
                .block();

        // when
        Mono<Order> orderMono = orderService.findById(savedOrder.getId());

        // then
        StepVerifier.create(orderMono)
                .assertNext(foundOrder -> {
                    assertThat(foundOrder).isNotNull();
                    assertThat(foundOrder.getId()).isEqualTo(savedOrder.getId());
                    assertThat(foundOrder.getTotalSum()).isEqualByComparingTo(savedOrder.getTotalSum());
                })
                .verifyComplete();
    }
}