package com.green.eats.store.application;

import com.green.eats.store.application.model.MenuGetRes;
import com.green.eats.store.application.model.MenuPostReq;
import com.green.eats.store.entity.Menu;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreService {
    private final MenuRepository menuRepository;

    public void addMenu(MenuPostReq req) {
        Menu menu = new Menu(req);
        menuRepository.save(menu);
    }

    public List<MenuGetRes> getAllMenus() {
        List<Menu> menuList = menuRepository.findAll();

        List<MenuGetRes> resList = new ArrayList<>( menuList.size() );
        //?? 박스갈이 작업
        for(Menu menu : menuList) {
            MenuGetRes menuGetRes = new MenuGetRes( menu );
            resList.add( menuGetRes );
        }

        //보지마세요.
        List<MenuGetRes> resList2 = menuList.stream()
                .map(MenuGetRes::new).toList();

        return resList;
    }
}
