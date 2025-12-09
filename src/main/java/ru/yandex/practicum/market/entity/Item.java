package ru.yandex.practicum.market.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "count_item")
    private int count;

    @Column(name = "img_path")
    private String imgPath;

    @ToString.Exclude
    @JoinColumn(name = "order_id", referencedColumnName = "id", nullable = true)
    @ManyToOne(fetch = FetchType.LAZY)
    private Order order;

    @Column(name = "check_order")
    private boolean checkOrder;

    public void setId(Long id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getCount() {
        return count;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

}