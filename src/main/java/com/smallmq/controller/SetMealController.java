package com.smallmq.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smallmq.dto.SetmealDto;
import com.smallmq.pojo.Setmeal;
import com.smallmq.service.SetMealService;
import com.smallmq.utils.Response;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetMealController {

    @Autowired
    private SetMealService setMealService;

    // 分页查询数据
    @GetMapping("/page")
    public Response<Page> page(@RequestParam(defaultValue = "1") Integer page,
                               @RequestParam(defaultValue = "5") Integer pageSize) {
        Page<Setmeal> setmealPage = new Page<>(page, pageSize);
        setMealService.page(setmealPage);
        return Response.success(setmealPage);
    }
    // 增加套餐
    @PostMapping
    public Response<String> save(@RequestBody SetmealDto setmealDto) {
        setMealService.saveWithDishes(setmealDto);
        return Response.success("添加成功");
    }

}
