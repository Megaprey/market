//package ru.igor.razzh.controller;
//
//import lombok.AllArgsConstructor;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.reactive.function.client.WebClient;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//import ru.yandex.practicum.market.dto.HandleItemDto;
//import ru.yandex.practicum.market.service.CartService;
//
//import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;
//
//
//@Controller
//@RequestMapping("cart")
//@AllArgsConstructor
//public class CartController {
//    private final WebClient webClient;
//
//    @GetMapping("/items")
//    public Mono<String> getItems(
//            @RequestParam(defaultValue = "1") int pageNumber,
//            @RequestParam(defaultValue = "10") int pageSize,
//            Model model,
//            ServerWebExchange exchange) {
//            return cartService.getItems(pageNumber, pageSize, model, exchange);
//    }
//
//    @PostMapping(value = "/items", consumes = APPLICATION_JSON_VALUE)
//    public Mono<String> handleItemAction(
//            @RequestBody HandleItemDto handleItemDto,
//            Model model,
//            ServerWebExchange exchange) {
//        return cartService.handleItemAction(handleItemDto, model, exchange);
//    }
//
//}