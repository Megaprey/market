package ru.yandex.practicum.market.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.yandex.practicum.market.config.TestR2dbcConfig;
import ru.yandex.practicum.market.dto.ItemDto;
import ru.yandex.practicum.market.entity.Item;
import ru.yandex.practicum.market.repository.ItemRepository;
import ru.yandex.practicum.market.util.Cart;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@ImportTestcontainers(PostgreSQLTestContainer.class)
@Import({TestR2dbcConfig.class})
class ItemServiceTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    @MockBean
    private PaymentClient paymentClient;

    private Cart cart;

    @BeforeEach
    void setUp() {
        cart = new Cart();

        itemRepository.deleteAll()
                .thenMany(Flux.just(
                        Item.builder()
                                .title("Мяч")
                                .description("Футбольный мяч")
                                .price(BigDecimal.valueOf(1000))
                                .count(10)
                                .imgPath("ball.jpg")
                                .checkOrder(true)
                                .build(),
                        Item.builder()
                                .title("Хоккей")
                                .description("Настольный хоккей")
                                .price(BigDecimal.valueOf(2000))
                                .count(5)
                                .imgPath("hockey.jpg")
                                .checkOrder(true)
                                .build(),
                        Item.builder()
                                .title("Футбол")
                                .description("Настольный футбол")
                                .price(BigDecimal.valueOf(3000))
                                .count(3)
                                .imgPath("soccer.jpg")
                                .checkOrder(true)
                                .build()
                ).flatMap(itemRepository::save))
                .blockLast();
    }

    @Test
    void getPageItems_shouldReturnAllItemsWithoutSearch() {
        Mono<Page<ItemDto>> pageMono = itemService.getPageItems(cart, 0, 10, null, "NO");

        StepVerifier.create(pageMono)
                .assertNext(page -> {
                    assertThat(page.getContent()).hasSize(3);
                    assertThat(page.getTotalElements()).isEqualTo(3);
                })
                .verifyComplete();
    }

    @Test
    void getPageItems_withSearchByTitle_shouldReturnMatchingItems() {
        Mono<Page<ItemDto>> pageMono = itemService.getPageItems(cart, 0, 10, "мяч", "NO");

        StepVerifier.create(pageMono)
                .assertNext(page -> {
                    assertThat(page.getContent()).hasSize(1);
                    assertThat(page.getContent().get(0).getTitle()).isEqualTo("Мяч");
                })
                .verifyComplete();
    }

    @Test
    void getPageItems_withSearchByDescription_shouldReturnMatchingItems() {
        Mono<Page<ItemDto>> pageMono = itemService.getPageItems(cart, 0, 10, "настольный", "NO");

        StepVerifier.create(pageMono)
                .assertNext(page -> {
                    assertThat(page.getContent()).hasSize(2);
                    assertThat(page.getContent()).allMatch(item ->
                            item.getDescription().toLowerCase().contains("настольный"));
                })
                .verifyComplete();
    }

    @Test
    void getPageItems_withSortPrice_shouldReturnSortedByPriceAsc() {
        Mono<Page<ItemDto>> pageMono = itemService.getPageItems(cart, 0, 10, null, "PRICE");

        StepVerifier.create(pageMono)
                .assertNext(page -> {
                    assertThat(page.getContent()).hasSize(3);
                    assertThat(page.getContent().get(0).getPrice()).isEqualByComparingTo("1000");
                    assertThat(page.getContent().get(1).getPrice()).isEqualByComparingTo("2000");
                    assertThat(page.getContent().get(2).getPrice()).isEqualByComparingTo("3000");
                })
                .verifyComplete();
    }
}
