package ru.igor.razzh.wallet.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Table("wallet")
public class Wallet {
    @Id
    private Long id;

    @Column("balance")
    private BigDecimal balance;

    @Column("userId")
    private Long userId;
}
