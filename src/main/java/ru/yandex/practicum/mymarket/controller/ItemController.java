package ru.yandex.practicum.mymarket.controller;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.mymarket.entity.Item;
import ru.yandex.practicum.mymarket.service.ItemService;
import ru.yandex.practicum.mymarket.util.Paging;
import java.util.List;


@Controller
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {
    private ItemService itemService;

    @GetMapping("/{id}")
    public String getItem(@PathVariable Long id,
                          Model model) {
        model.addAttribute(itemService.findById(id).get());

        return "item";
    }

    @GetMapping()
    public String getItems(
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            Model model) {
        Page<Item> itemPage = itemService.findAllOutCart(PageRequest.of(pageNumber - 1, pageSize));

        Paging paging = new Paging(itemPage.getNumber() + 1,
                pageSize,
                itemPage.getTotalElements());

        model.addAttribute("items", itemPage.getContent());
        model.addAttribute("paging", paging);

        return "items";
    }

//    @PostMapping
//    public String viewItems() {
//        return "items";
//    }

//    @GetMapping("/${id}")
//    public Item getItem() {
//
//    }
}
