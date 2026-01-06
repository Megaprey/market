package ru.igor.razzh.wallet.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.igor.razzh.wallet.dto.BalanceRs;
import ru.igor.razzh.wallet.dto.ErrorResponse;
import ru.igor.razzh.wallet.dto.PayRq;
import ru.igor.razzh.wallet.dto.PayRs;
import ru.igor.razzh.wallet.service.WalletService;

@CrossOrigin(origins = "http://localhost:8080")
@RestController
@RequestMapping("/wallet")
@AllArgsConstructor
public class WalletController {
    WalletService walletService;

    @GetMapping()
    public Mono<BalanceRs> getBalance() {
        return walletService.getBalance().map(BalanceRs::new);
    }

    @PutMapping()
    public Mono<PayRs> paySum(@RequestBody PayRq payRq) {
        return walletService.paySum(payRq.sum()).map(PayRs::new);
    }

    @ExceptionHandler(RuntimeException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleException(RuntimeException ex) {

        return Mono.just(ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorResponse(ex.getMessage())));
    }
}
