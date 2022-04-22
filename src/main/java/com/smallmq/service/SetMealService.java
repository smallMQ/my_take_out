package com.smallmq.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smallmq.dto.SetmealDto;
import com.smallmq.pojo.Setmeal;

public interface SetMealService extends IService<Setmeal> {
    public void saveWithDishes(SetmealDto setmealDto);
    public void updateWithDishes(SetmealDto setmealDto);
    public void updateStatus(Integer status,Long[] ids);
}
