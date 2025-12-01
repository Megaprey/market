package ru.yandex.practicum.mymarket.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.mymarket.entity.Item;
import ru.yandex.practicum.mymarket.entity.Order;
import ru.yandex.practicum.mymarket.repository.ItemRepository;
import ru.yandex.practicum.mymarket.repository.OrderRepository;
import ru.yandex.practicum.mymarket.util.Cart;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ShopService {
    Cart cart;
    ItemRepository itemRepository;
    OrderRepository orderRepository;

    @Transactional
    public Order buy(BigDecimal totalSum) {
        List<Item> itemListInCart = itemRepository.findAllById(cart.getIds());
        List<Item> copyItemList = itemListInCart.stream()
                .map(item -> Item.builder()
                        .title(item.getTitle())
                        .description(item.getDescription())
                        .price(item.getPrice())
                        .imgPath(item.getImgPath())
                        .count(cart.getItemCount(item.getId()))
                        .order(item.getOrder())
                        .build())
                .collect(Collectors.toList());
        itemListInCart.stream().forEach(i -> i.setCount(i.getCount() - cart.getItemCount(i.getId())));
        itemRepository.saveAll(itemListInCart);
        Order order = Order.builder().items(copyItemList).totalSum(totalSum).build();
        copyItemList.forEach(item -> item.setOrder(order));
        orderRepository.save(order);
        cart.removeAllItems();

        return order;
    }
}
