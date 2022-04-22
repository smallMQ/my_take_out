package com.smallmq.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smallmq.dto.DishDto;
import com.smallmq.pojo.DishFlavor;

public interface DishFlavorService extends IService<DishFlavor> {
        public void deleteByDishId(Long dishId);
        public void insertByDishId(DishDto dishDto);
}
