package ru.yandex.practicum.market.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.yandex.practicum.market.entity.Item;
import ru.yandex.practicum.market.entity.Order;
import ru.yandex.practicum.market.repository.ItemRepository;
import ru.yandex.practicum.market.repository.OrderRepository;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Testcontainers
@ImportTestcontainers(PostgreSQLTestContainer.class)
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ItemRepository itemRepository;

    @BeforeEach
    void setUp() {
        itemRepository.deleteAll();
        orderRepository.deleteAll();

        // Создаем заказы и сначала сохраняем их, чтобы они перестали быть transient
        Order order1 = Order.builder()
                .totalSum(BigDecimal.valueOf(1000))
                .build();

        Order order2 = Order.builder()
                .totalSum(BigDecimal.valueOf(4000))
                .build();

        // Сохраняем заказы ДО связывания с товарами
        order1 = orderRepository.save(order1);
        order2 = orderRepository.save(order2);

        // Теперь создаем товары и связываем их с уже сохранёнными заказами
        Item item1InOrder = Item.builder()
                .title("Мяч")
                .description("Футбольный мяч")
                .price(BigDecimal.valueOf(1000))
                .count(1)
                .imgPath("ball.jpg")
                .checkOrder(true)
                .order(order1) // устанавливаем связь
                .build();

        Item item2InOrder = Item.builder()
                .title("Хоккей")
                .description("Настольный хоккей")
                .price(BigDecimal.valueOf(2000))
                .count(2)
                .imgPath("hockey.jpg")
                .checkOrder(true)
                .order(order2) // устанавливаем связь
                .build();

        // Сохраняем товары
        itemRepository.save(item1InOrder);
        itemRepository.save(item2InOrder);
    }


    @Test
    void findAll_shouldReturnAllOrders() {
        // when
        List<Order> orders = orderService.findAll();
        System.out.println(orders);

        // then
        assertThat(orders).hasSize(2);
        assertThat(orders)
                .extracting(Order::getTotalSum)
                .containsExactlyInAnyOrder(BigDecimal.valueOf(1000), BigDecimal.valueOf(4000));
    }

    @Test
    void findById_withExistingId_shouldReturnOrder() {
        // given
        Order savedOrder = orderRepository.findAll().get(0);

        // when
        Order foundOrder = orderService.findById(savedOrder.getId());

        // then
        assertThat(foundOrder).isNotNull();
        assertThat(foundOrder.getId()).isEqualTo(savedOrder.getId());
        assertThat(foundOrder.getTotalSum()).isEqualByComparingTo(savedOrder.getTotalSum());
    }

    @Test
    void findById_withNonExistingId_shouldThrowException() {
        // given
        Long nonExistentId = 999L;

        // then
        assertThatThrownBy(() -> orderService.findById(nonExistentId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Order not found");
    }

}