//package ru.igor.razzh.controller;
//
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.reactive.function.client.WebClient;
//import reactor.core.publisher.Mono;
//
//@Controller
//public class HomeController {
//
//    private final WebClient webClient;
//
//    public HomeController(WebClient webClient) {
//        this.webClient = webClient;
//    }
//
//    @GetMapping("/")
//    public String index() {
//        return "home";
//    }
//
//    @GetMapping("/message")
//    public Mono<String> getMessage(Model model) {
//        return webClient
//            .get()
//            .uri("http://localhost:8082/api/message")
//            .retrieve()
//            .bodyToMono(String.class)
//            .doOnNext(message -> model.addAttribute("message", message))
//            .thenReturn("message"); // Шаблон: message.html
//    }
//}
