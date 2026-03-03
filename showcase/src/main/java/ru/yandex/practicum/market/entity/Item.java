package ru.yandex.practicum.market.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;
import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("items")
public class Item implements Serializable {
    private static final long serialVersionUID = 1l;
    @Id
    private Long id;

    @Column("title")
    private String title;

    @Column("description")
    private String description;

    @Column("price")
    private BigDecimal price;

    @Column("count_item")
    private int count;

    @Column("img_path")
    private String imgPath;

    @Column("order_id")
    private Long orderId;

    @Column("check_order")
    private boolean checkOrder;

    @Transient
    private Order order;

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
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

    public void setOrderId(Order order) {
        this.orderId = orderId;
    }

}