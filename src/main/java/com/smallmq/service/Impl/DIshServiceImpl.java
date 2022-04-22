package com.smallmq.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smallmq.mapper.DishMapper;
import com.smallmq.pojo.Dish;
import com.smallmq.service.DishService;
import org.springframework.stereotype.Service;

@Service
public class DIshServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

}
