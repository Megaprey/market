package ru.yandex.practicum.market.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.market.dto.ItemDto;
import ru.yandex.practicum.market.entity.Item;
import ru.yandex.practicum.market.repository.ItemRepository;
import ru.yandex.practicum.market.util.Cart;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

import static ru.yandex.practicum.market.util.Utils.mapItemToItemDto;

@Service
@AllArgsConstructor
public class ItemService {
    private ItemRepository itemRepository;

    public Mono<Item> findById(Long id){
        return itemRepository.findById(id);
    }

    public Mono<Page<ItemDto>> getPageItems(Cart cart, int page, int size, String search, String sort) {
        // Загружаем только активные товары
        Flux<Item> itemFlux = itemRepository.findAllByCheckOrder(true);

        // Фильтрация по поиску
        if (search != null && !search.trim().isEmpty()) {
            String lowerSearch = search.toLowerCase();
            Predicate<Item> matchesSearch = item ->
                    item.getTitle().toLowerCase().contains(lowerSearch) ||
                            item.getDescription().toLowerCase().contains(lowerSearch);
            itemFlux = itemFlux.filter(matchesSearch);
        }

        // Преобразуем в DTO
        Flux<ItemDto> dtoFlux = itemFlux.map(item -> mapItemToItemDto(item, cart));

        // Сортировка
        Comparator<ItemDto> comparator = switch (sort) {
            case "ALPHA" -> Comparator.comparing(ItemDto::getTitle, String.CASE_INSENSITIVE_ORDER);
            case "PRICE" -> Comparator.comparing(ItemDto::getPrice);
            default -> (a, b) -> 0;
        };

        dtoFlux = dtoFlux.sort(comparator);

        // Пагинация: собираем в список и режем по странице
        return dtoFlux
                .collectList()
                .map(items -> getPageFromListItems(items, page, size));
    }
    public Mono<Page<ItemDto>> getPageItemsInCart(Cart cart, int page, int size) {
        return itemRepository.findAllByCheckOrder(true)
                .filter(i -> cart.contains(i.getId()))
                .map(i -> mapItemToItemDto(i, cart))
                .collectList()
                .map(items -> getPageFromListItems(items, page, size));

    }

    public Page<ItemDto> getPageFromListItems(List<ItemDto> items, int page, int size) {

        Pageable pageRequest = PageRequest.of(page, size);

        int start = (int) pageRequest.getOffset();
        int end = Math.min((start + pageRequest.getPageSize()), items.size());

        List<ItemDto> pageContent = items.subList(start, end);
        return new PageImpl<>(pageContent, pageRequest, items.size());
    }

}
