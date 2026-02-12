package ru.yandex.practicum.market.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.market.entity.Item;
import ru.yandex.practicum.market.entity.Order;
import ru.yandex.practicum.market.repository.ItemRepository;
import ru.yandex.practicum.market.repository.OrderRepository;
import ru.yandex.practicum.market.repository.UserRepository;
import ru.yandex.practicum.market.util.Cart;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class ShopService {
    private final ItemRepository itemRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final PaymentClient paymentClient;

    @Transactional
    public Mono<Order> buy(BigDecimal totalSum, Cart cart, String username) {
        return paymentClient.makePayment(username, totalSum)
                .flatMap(paymentSuccess -> {
                    if (!paymentSuccess) {
                        return Mono.error(new RuntimeException("Оплата не прошла. Недостаточно средств."));
                    }
                    return processOrder(totalSum, cart, username);
                });
    }

    private Mono<Order> processOrder(BigDecimal totalSum, Cart cart, String username) {
        return userRepository.findByUsername(username)
                .flatMap(user -> Flux.fromIterable(cart.getIds())
                        .collectList()
                        .flatMapMany(ids -> itemRepository.findAllById(ids))
                        .collectList()
                        .flatMap(itemListInCart -> {
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

                            return itemRepository.saveAll(updatedItems)
                                    .then(Mono.just(copyItemList));
                        })
                        .flatMap(copyItemList -> {
                            Order order = Order.builder()
                                    .totalSum(totalSum)
                                    .userId(user.getId())
                                    .build();
                            copyItemList.forEach(item -> item.setOrder(order));
                            return orderRepository.save(order)
                                    .doOnSuccess(savedOrder -> cart.removeAllItems())
                                    .thenReturn(order);
                        })
                );
    }
}
