package com.green.eats.store.application;

import com.green.eats.common.Dummy;
import com.green.eats.store.entity.Menu;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class MenuDummy extends Dummy {
    @Autowired
    MenuRepository menuRepository;

    @Test
    void insDummyList() {
        final int size = 20;

        for(int i=0; i<size; i++) {
            Menu menu = new Menu();
            menu.setName( koFaker.food().dish() );
            menu.setPrice( koFaker.random().nextInt(10_000, 50_000) );
            menu.setStockQuantity( koFaker.random().nextInt(5, 40) );
            menuRepository.save(menu);
        }
        menuRepository.flush(); //필수
    }
}
