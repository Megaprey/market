package ru.yandex.practicum.market.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;

import java.math.BigDecimal;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("orders")
@Getter
@Setter
public class Order {
    @Id
    private Long id;

    @Column("total_sum")
    private BigDecimal totalSum;

    @Column("user_id")
    private Long userId;

    @Transient
    private List<Item> items;
}
