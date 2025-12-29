package ru.yandex.practicum.market.dto;

import lombok.*;

import java.math.BigDecimal;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BuyRequestDto {
    private Double total;
}
