package ru.yandex.practicum.market.controller;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.yandex.practicum.market.dto.ItemDto;
import ru.yandex.practicum.market.service.ItemService;
import ru.yandex.practicum.market.util.Paging;
import ru.yandex.practicum.market.util.Cart;

import static ru.yandex.practicum.market.util.Utils.mapItemToItemDto;


@Controller
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {
    private ItemService itemService;
    private Cart cart;

    @GetMapping("/{id}")
    public String getItem(@PathVariable Long id,
                          Model model) {
        model.addAttribute("item", mapItemToItemDto(itemService.findById(id).get(), cart));

        return "item";
    }

    @GetMapping()
    public String getItems(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "NO") String sort,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            Model model) {
        Page<ItemDto> itemPage = itemService.getPageItems(cart, pageNumber - 1, pageSize, search, sort);

        Paging paging = new Paging(itemPage.getNumber() + 1,
                pageSize,
                itemPage.getTotalElements());

        model.addAttribute("items", itemPage.getContent());
        model.addAttribute("paging", paging);
        model.addAttribute("search", search);
        model.addAttribute("sort", sort);

        return "items";
    }

    @PostMapping
    public String handleItemAction(
            @RequestParam Long id,
            @RequestParam String action,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "NO") String sort,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "1") int pageNumber,
            RedirectAttributes redirectAttributes) {
        cart.handleItemAction(action, id);

        redirectAttributes.addAttribute("search", search);
        redirectAttributes.addAttribute("sort", sort);
        redirectAttributes.addAttribute("pageSize", pageSize);
        redirectAttributes.addAttribute("pageNumber", pageNumber);

        return "redirect:/items";
    }

}
