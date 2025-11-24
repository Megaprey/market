package ru.yandex.practicum.mymarket.controller;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.mymarket.entity.Order;
import ru.yandex.practicum.mymarket.service.OrderService;
import ru.yandex.practicum.mymarket.util.Paging;

@Controller
@RequestMapping("/orders")
@AllArgsConstructor
public class OrderController {
    private OrderService oderService;

    @GetMapping()
    public String getItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Page<Order> itemPage = oderService.findAll(PageRequest.of(page, size));

        Paging paging = new Paging(1,
                2,
                2);

        model.addAttribute("items", itemPage.getContent());
        model.addAttribute("paging", paging);

        return "items";
    }
}
