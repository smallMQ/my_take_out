package com.smallmq.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smallmq.dto.DishDto;
import com.smallmq.mapper.DishMapper;
import com.smallmq.pojo.Dish;
import com.smallmq.service.DishFlavorService;
import com.smallmq.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DIshServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    @Transactional
    @Override
    public void saveWithFlavor(DishDto dishDto) {
        this.save(dishDto);
        Long id = dishDto.getId();
        dishDto.getFlavors().forEach(flavor -> {
                    flavor.setDishId(id);
                });
        dishFlavorService.saveBatch(dishDto.getFlavors());
    }
}
