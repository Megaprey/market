package ru.yandex.practicum.mymarket.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.mymarket.entity.Item;
import ru.yandex.practicum.mymarket.repository.ItemRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ItemService {
    private ItemRepository itemRepository;

    public Optional<Item> findById(Long id){
        return itemRepository.findById(id);
    }

    public Page<Item> findAll(PageRequest of) {
        return itemRepository.findAll(of);
    }

    public Page<Item> findAllInCart(PageRequest of) {
        return itemRepository.findByCartFlg(of,true);
    }

    public Page<Item> findAllOutCart(PageRequest of) {
        Page<Item> result = itemRepository.findByCartFlg(of,false);
        result.stream().forEach(item -> item.setCount(2));
        return result;
    }
}
