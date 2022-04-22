package com.smallmq.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smallmq.dto.DishDto;
import com.smallmq.mapper.DishMapper;
import com.smallmq.pojo.Dish;
import com.smallmq.pojo.DishFlavor;
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

    @Override
    public void updateWithFlavor(DishDto dishDto) {
        this.updateById(dishDto);
        LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(wrapper);

        dishDto.getFlavors().forEach(flavor -> {
            flavor.setDishId(dishDto.getId());
        });
        dishFlavorService.saveBatch(dishDto.getFlavors());
    }

    @Override
    public void deleteWithFlavor(DishDto dishDto) {

    }

    @Override
    public void updateStatus(Integer status, Long[] ids) {
        Dish dish = new Dish();
        dish.setStatus(status);
        for (Long id : ids) {
            dish.setId(id);
            this.updateById(dish);
        }
    }

}
