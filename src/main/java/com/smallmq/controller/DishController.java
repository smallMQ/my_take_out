package com.smallmq.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smallmq.pojo.Dish;
import com.smallmq.service.DishService;
import com.smallmq.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @GetMapping("page")
    public Response<Page> page(@RequestParam(value = "page", defaultValue = "1") Integer page,
                               @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                               @RequestParam(value = "name", required = false) String name) {
        // 分页查询
        IPage<Dish> pageInfo = new Page<>(page, pageSize);
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        if (name != null) {
            wrapper.like(Dish::getName, name);
        }
        Page<Dish> page1 = dishService.page((Page)pageInfo, wrapper);
        log.info("pageInfo:{}", page1);
        return Response.success(page1);

    }




}
