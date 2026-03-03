package ru.yandex.practicum.market.dto;

import lombok.*;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BuyRequestDto {
    private Double total;
}
