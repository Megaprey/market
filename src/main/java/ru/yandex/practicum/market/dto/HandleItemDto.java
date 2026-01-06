package ru.yandex.practicum.market.dto;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class HandleItemDto {
    private Long id;
    private String action;
    private String search;
    private String sort;
    private int pageSize;
    private int pageNumber;
}
