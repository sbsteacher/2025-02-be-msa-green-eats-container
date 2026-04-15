package com.green.eats.store.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.green.eats.store.application.model.MenuPostReq;
import com.green.eats.store.enumcode.EnumMenuCategory;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Menu {
    @Id @Tsid
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    private Integer stockQuantity;

    @Column(nullable = false, name="cd_category")
    @JsonProperty("menuCategory")
    private EnumMenuCategory enumMenuCategory;

    public Menu(MenuPostReq req) {
        this.name = req.getName();
        this.price = req.getPrice();
        this.stockQuantity = req.getStockQuantity();
        this.enumMenuCategory = req.getMenuCategory();
    }

    public void removeStock(int quantity) {
        int restStock = this.stockQuantity - quantity;
        if (restStock < 0) {
            throw new RuntimeException("상품 '" + name + "'의 재고가 부족합니다. (현재: " + stockQuantity + ")");
        }
        this.stockQuantity = restStock;
    }
}
