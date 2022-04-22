package com.smallmq.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smallmq.dto.SetmealDto;
import com.smallmq.mapper.SetMealMapper;
import com.smallmq.pojo.Setmeal;
import com.smallmq.pojo.SetmealDish;
import com.smallmq.service.DishService;
import com.smallmq.service.SetMealDishService;
import com.smallmq.service.SetMealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SetMealServiceImpl extends ServiceImpl<SetMealMapper, Setmeal> implements SetMealService {
    @Autowired
    private SetMealDishService setMealDishService;

    @Autowired
    private SetMealMapper setMealMapper;

    @Override
    public void saveWithDishes(SetmealDto setmealDto) {
        this.save(setmealDto);
        Long id = setmealDto.getId();
        setmealDto.getSetmealDishes().forEach(dishes -> {
            dishes.setSetmealId(id);
        });
        setMealDishService.saveBatch(setmealDto.getSetmealDishes());
        }

    @Override
    public void updateWithDishes(SetmealDto setmealDto) {
        this.updateById(setmealDto);

        LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SetmealDish::getSetmealId, setmealDto.getId());
        setMealDishService.remove(wrapper);

        setmealDto.getSetmealDishes().forEach(dishes -> {
            dishes.setSetmealId(setmealDto.getId());
        });
        setMealDishService.saveBatch(setmealDto.getSetmealDishes());


    }

    @Override
    public void updateStatus(Integer status, Long[] ids) {
        Setmeal setmeal = new Setmeal();
        setmeal.setStatus(status);
        this.update(setmeal, new LambdaQueryWrapper<Setmeal>().in(Setmeal::getId, ids));

    }



}
