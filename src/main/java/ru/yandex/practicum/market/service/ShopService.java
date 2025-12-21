package ru.yandex.practicum.market.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.market.entity.Item;
import ru.yandex.practicum.market.entity.Order;
import ru.yandex.practicum.market.repository.ItemRepository;
import ru.yandex.practicum.market.repository.OrderRepository;
import ru.yandex.practicum.market.util.Cart;

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
    public Mono<Order> buy(BigDecimal totalSum) {
        return Flux.fromIterable(cart.getIds())
                .collectList()
                .flatMapMany(ids -> itemRepository.findAllById(ids))
                .collectList()
                .flatMapMany(itemListInCart -> {
                         List<Item> copyItemList = itemListInCart.stream().map(item -> Item.builder()
                                 .title(item.getTitle())
                                 .description(item.getDescription())
                                 .price(item.getPrice())
                                 .imgPath(item.getImgPath())
                                 .count(cart.getItemCount(item.getId()))
                                 .order(item.getOrder())
                                 .build())
                                 .collect(Collectors.toList());
                         Flux<Item> updatedItems = Flux.fromIterable(itemListInCart).map(i -> {
                             i.setCount(i.getCount() - cart.getItemCount(i.getId()));
                             return i;
                         });
                         return itemRepository.saveAll(updatedItems).thenMany(Flux.fromIterable(copyItemList));
                        })
                .collectList()
                .flatMap(copyItemList -> {
                    Order order = Order.builder()
                            .totalSum(totalSum)
                            .build();
                    copyItemList.forEach(item -> item.setOrder(order));
                    return orderRepository.save(order)
                            .doOnSuccess(saveOrder -> cart.removeAllItems())
                            .thenReturn(order);
                });
    }
}
