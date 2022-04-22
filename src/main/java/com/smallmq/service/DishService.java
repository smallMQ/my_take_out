package com.smallmq.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smallmq.dto.DishDto;
import com.smallmq.pojo.Dish;

public interface DishService extends IService<Dish> {
    public void saveWithFlavor(DishDto dishDto);
}
