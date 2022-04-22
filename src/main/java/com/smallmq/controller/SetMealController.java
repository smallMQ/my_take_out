package com.smallmq.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smallmq.dto.SetmealDto;
import com.smallmq.pojo.Setmeal;
import com.smallmq.service.CategoryService;
import com.smallmq.service.SetMealService;
import com.smallmq.utils.Response;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetMealController {

    @Autowired
    private SetMealService setMealService;

    @Autowired
    private CategoryService categoryService;


    // 分页查询数据
    @GetMapping("/page")
    public Response<Page> page(@RequestParam(defaultValue = "1") Integer page,
                               @RequestParam(defaultValue = "5") Integer pageSize) {
        Page<Setmeal> setmealPage = new Page<>(page, pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>();

        setMealService.page(setmealPage);
        BeanUtils.copyProperties(setmealPage, setmealDtoPage,"records");
        setmealDtoPage.setRecords(setmealPage.getRecords().stream().map(setmeal -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(setmeal, setmealDto);
            Long categoryId = setmeal.getCategoryId();
            if(categoryId != null) {
                setmealDto.setCategoryName(categoryService.getById(categoryId).getName());
                log.info(categoryService.getById(categoryId).getName());
            }
            return setmealDto;
        }).collect(Collectors.toList()));
        return Response.success(setmealDtoPage);
    }
    // 增加套餐
    @PostMapping
    public Response<String> save(@RequestBody SetmealDto setmealDto) {
        setMealService.saveWithDishes(setmealDto);
        return Response.success("添加成功");
    }

}
