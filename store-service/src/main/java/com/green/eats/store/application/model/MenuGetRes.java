package com.green.eats.store.application.model;


import com.green.eats.store.entity.Menu;
import com.green.eats.store.enumcode.EnumMenuCategory;
import lombok.Getter;

@Getter
public class MenuGetRes {
    private Long id;
    private String name;
    private Integer price;
    private Integer stockQuantity;
    private EnumMenuCategory menuCategory;

    public MenuGetRes(Menu menu) {
        this.id = menu.getId();
        this.name = menu.getName();
        this.price = menu.getPrice();
        this.stockQuantity = menu.getStockQuantity();
        this.menuCategory = menu.getEnumMenuCategory();
    }
}
