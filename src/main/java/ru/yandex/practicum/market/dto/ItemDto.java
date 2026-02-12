package ru.yandex.practicum.market.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public class ItemDto {
    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private String imgPath;
    private int count;

    public ItemDto() {}

    public ItemDto(Long id, String title, String description, BigDecimal price, String imgPath, int count) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.imgPath = imgPath;
        this.count = count;
    }

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}