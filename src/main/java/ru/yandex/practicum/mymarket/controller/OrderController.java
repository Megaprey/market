package ru.yandex.practicum.mymarket.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.mymarket.entity.Order;
import ru.yandex.practicum.mymarket.service.OrderService;
import ru.yandex.practicum.mymarket.service.ShopService;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/orders")
@AllArgsConstructor
public class OrderController {
    private OrderService orderService;
    private ShopService shopService;

//    @GetMapping()
//    public String getItems(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size,
//            Model model) {
//
//        Page<Order> itemPage = oderService.findAll(PageRequest.of(page, size));
//
//        Paging paging = new Paging(1,
//                2,
//                2);
//
//        model.addAttribute("items", itemPage.getContent());
//        model.addAttribute("paging", paging);
//
//        return "items";
//    }

    @PostMapping("/buy")
    public String buy(
            @RequestParam(name = "total") BigDecimal totalSum,
            Model model
        ) {
        Order order = shopService.buy(totalSum);

        model.addAttribute("order", order);

        return "order";
    }

    @GetMapping
    public String getOrders(
            Model model
    ) {
        List<Order> orders = orderService.findAll();
        model.addAttribute("orders", orders);

        return "orders";
    }

    @GetMapping("/{id}")
    public String getOrder(
            @PathVariable(name = "id") Long orderId,
            Model model
    ) {
        Order order = orderService.findById(orderId);

        model.addAttribute("order", order);

        return "order";
    }
}
