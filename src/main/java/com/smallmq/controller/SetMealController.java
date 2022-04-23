package com.smallmq.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smallmq.dto.SetmealDto;
import com.smallmq.pojo.Setmeal;
import com.smallmq.pojo.SetmealDish;
import com.smallmq.service.CategoryService;
import com.smallmq.service.SetMealDishService;
import com.smallmq.service.SetMealService;
import com.smallmq.utils.Response;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetMealController {

    @Autowired
    private SetMealService setMealService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SetMealDishService setMealDishService;

    // 分页查询数据
    @GetMapping("/page")
    public Response<Page> page(@RequestParam(defaultValue = "1") Integer page,
                               @RequestParam(defaultValue = "5") Integer pageSize,
                               @RequestParam(defaultValue = "",required = false) String name) {
        Page<Setmeal> setmealPage = new Page<>(page, pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>();
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        if (name != null && name.length() > 0) {
            wrapper.like(Setmeal::getName, name);
        }
        setMealService.page(setmealPage, wrapper);
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
    // 修改套餐
    @PutMapping
    public Response<String> update(@RequestBody SetmealDto setmealDto) {
        setMealService.updateWithDishes(setmealDto);
        return Response.success("修改成功");
    }

    // id查询
    @GetMapping("/{id}")
    public Response<SetmealDto> getById(@PathVariable("id") Long id) {
        SetmealDto setmealDto = new SetmealDto();

        Setmeal setmeal = setMealService.getById(id);
        log.warn(setmeal.toString());

        LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SetmealDish::getSetmealId, id);
        List<SetmealDish> setmealDish = setMealDishService.list(wrapper);


        log.warn(setmealDish.toString());
        BeanUtils.copyProperties(setmeal, setmealDto);
        setmealDto.setSetmealDishes(setmealDish);
        log.warn(setmealDto.toString());

        return Response.success(setmealDto);
    }

    // 修改套餐状态
    @PostMapping("/status/{status}")
    public Response<String> updateStatus(@PathVariable("status") Integer status,
                                         @RequestParam("ids") Long[] ids) {

        setMealService.updateStatus(status, ids);
        return Response.success("修改成功");
    }
    // 删除套餐
    @Transactional
    @DeleteMapping
    public Response<String> delete(@RequestParam("ids") Long[] ids) {
        setMealService.removeByIds(Arrays.asList(ids));
        return Response.success("删除成功");
    }
}
