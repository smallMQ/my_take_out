package com.smallmq.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smallmq.dto.SetmealDto;
import com.smallmq.mapper.SetMealMapper;
import com.smallmq.pojo.Setmeal;
import com.smallmq.service.DishService;
import com.smallmq.service.SetMealDishService;
import com.smallmq.service.SetMealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SetMealServiceImpl extends ServiceImpl<SetMealMapper, Setmeal> implements SetMealService {
    @Autowired
    private SetMealDishService setMealDishService;

    @Override
    public void saveWithDishes(SetmealDto setmealDto) {
        this.save(setmealDto);
        Long id = setmealDto.getId();
        setmealDto.getSetmealDishes().forEach(dishes -> {
            dishes.setSetmealId(id);
        });
        setMealDishService.saveBatch(setmealDto.getSetmealDishes());
        }

}
