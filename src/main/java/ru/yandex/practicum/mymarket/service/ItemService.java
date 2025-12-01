package ru.yandex.practicum.mymarket.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.mymarket.dto.ItemDto;
import ru.yandex.practicum.mymarket.entity.Item;
import ru.yandex.practicum.mymarket.repository.ItemRepository;
import ru.yandex.practicum.mymarket.util.Cart;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.yandex.practicum.mymarket.util.Utils.mapItemToItemDto;

@Service
@AllArgsConstructor
public class ItemService {
    private ItemRepository itemRepository;

    public Optional<Item> findById(Long id){
        return itemRepository.findById(id);
    }

    public Page<ItemDto> getPageItems(Cart cart, int page, int size, String search, String sort) {
        List<Item> itemList = itemRepository.findAllByCheckOrder(true);
        List<ItemDto> itemDtoList = itemList.stream().map(i -> mapItemToItemDto(i, cart)).toList();
        if (search != null && !search.trim().isEmpty()) {
            String lowerSearch = search.toLowerCase();
            itemDtoList = itemDtoList.stream()
                    .filter(item -> item.getTitle().toLowerCase().contains(lowerSearch) ||
                            item.getDescription().toLowerCase().contains(lowerSearch))
                    .collect(Collectors.toList());
        }
        switch (sort) {
            case "ALPHA"-> itemDtoList.stream()
                    .sorted((a, b) -> a.getTitle().compareToIgnoreCase(b.getTitle()))
                    .collect(Collectors.toList());
            case "PRICE"-> itemDtoList.stream()
                    .sorted(Comparator.comparing(ItemDto::getPrice))
                    .collect(Collectors.toList());
        }
        return getPageFromListItems(itemDtoList, page, size);
    }

    public Page<ItemDto> getPageItemsInCart(Cart cart, int page, int size) {
        List<Item> itemList = itemRepository.findAllByCheckOrder(true);
        List itemDtoList = itemList.stream().filter(i -> cart.contains(i.getId())).map(i -> mapItemToItemDto(i, cart)).toList();
        return getPageFromListItems(itemDtoList, page, size);
    }

    public Page<ItemDto> getPageFromListItems(List<ItemDto> items, int page, int size) {

        Pageable pageRequest = PageRequest.of(page, size);

        int start = (int) pageRequest.getOffset();
        int end = Math.min((start + pageRequest.getPageSize()), items.size());

        List<ItemDto> pageContent = items.subList(start, end);
        return new PageImpl<>(pageContent, pageRequest, items.size());
    }

    public List<Item> getListItems(Cart cart, int page, int size) {
        return itemRepository.findAllById(cart.getIds());
    }

}
