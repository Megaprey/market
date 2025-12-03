package ru.yandex.practicum.market.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.data.domain.Page;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.yandex.practicum.market.dto.ItemDto;
import ru.yandex.practicum.market.entity.Item;
import ru.yandex.practicum.market.repository.ItemRepository;
import ru.yandex.practicum.market.util.Cart;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@ImportTestcontainers(PostgreSQLTestContainer.class)
class ItemServiceTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    private Cart cart;

    @BeforeEach
    void setUp() {
        cart = new Cart();
        itemRepository.deleteAll();

        itemRepository.save(Item.builder()
                .title("Мяч")
                .description("Футбольный мяч")
                .price(BigDecimal.valueOf(1000))
                .count(10)
                .imgPath("ball.jpg")
                .checkOrder(true)
                .build());

        itemRepository.save(Item.builder()
                .title("Хоккей")
                .description("Настольный хоккей")
                .price(BigDecimal.valueOf(2000))
                .count(5)
                .imgPath("hockey.jpg")
                .checkOrder(true)
                .build());

        itemRepository.save(Item.builder()
                .title("Футбол")
                .description("Настольный футбол")
                .price(BigDecimal.valueOf(3000))
                .count(3)
                .imgPath("soccer.jpg")
                .checkOrder(true)
                .build());
    }

    @Test
    void getPageItems_shouldReturnAllItemsWithoutSearch() {
        Page<ItemDto> page = itemService.getPageItems(cart, 0, 10, null, "NO");
        assertThat(page.getContent()).hasSize(3);
        assertThat(page.getTotalElements()).isEqualTo(3);
    }

    @Test
    void getPageItems_withSearchByTitle_shouldReturnMatchingItems() {
        Page<ItemDto> page = itemService.getPageItems(cart, 0, 10, "мяч", "NO");
        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().get(0).getTitle()).isEqualTo("Мяч");
    }

    @Test
    void getPageItems_withSearchByDescription_shouldReturnMatchingItems() {
        Page<ItemDto> page = itemService.getPageItems(cart, 0, 10, "настольный", "NO");
        assertThat(page.getContent()).hasSize(2);
        assertThat(page.getContent()).allMatch(item ->
                item.getDescription().toLowerCase().contains("настольный"));
    }

    @Test
    void getPageItems_withSortAlpha_shouldReturnSortedByTitle() {
        Page<ItemDto> page = itemService.getPageItems(cart, 0, 10, null, "ALPHA");
        assertThat(page.getContent().get(0).getTitle()).isEqualTo("Мяч");
        assertThat(page.getContent().get(1).getTitle()).isEqualTo("Хоккей");
        assertThat(page.getContent().get(2).getTitle()).isEqualTo("Футбол");
    }

    @Test
    void getPageItems_withSortPrice_shouldReturnSortedByPriceAsc() {
        Page<ItemDto> page = itemService.getPageItems(cart, 0, 10, null, "PRICE");
        assertThat(page.getContent().get(0).getPrice()).isEqualByComparingTo("1000");
        assertThat(page.getContent().get(1).getPrice()).isEqualByComparingTo("2000");
        assertThat(page.getContent().get(2).getPrice()).isEqualByComparingTo("3000");
    }
}