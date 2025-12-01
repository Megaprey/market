package ru.yandex.practicum.mymarket.util;

import org.springframework.data.domain.Page;
import ru.yandex.practicum.mymarket.dto.ItemDto;
import ru.yandex.practicum.mymarket.entity.Item;

import java.math.BigDecimal;

public class Utils {
    public static BigDecimal getTotalPriceFromItems(Page<Item> items) {
        return items.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getCount())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public static BigDecimal getTotalPriceFromItemsDto(Page<ItemDto> items) {
        return items.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getCount())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public static ItemDto mapItemToItemDto(Item item, Cart cart) {
        return ItemDto.builder()
                .id(item.getId())
                .title(item.getTitle())
                .price(item.getPrice())
                .count(cart.getItemCount(item.getId()))
                .description(item.getDescription())
                .imgPath(item.getImgPath())
                .build();
    }
}
