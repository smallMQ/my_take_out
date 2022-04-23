package com.smallmq.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smallmq.dto.DishDto;
import com.smallmq.pojo.Dish;
import com.smallmq.pojo.DishFlavor;
import com.smallmq.service.CategoryService;
import com.smallmq.service.DishFlavorService;
import com.smallmq.service.DishService;
import com.smallmq.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;


    @Autowired
    private CategoryService categoryService;

    @GetMapping("page")
    public Response<Page> page(@RequestParam(value = "page", defaultValue = "1") Integer page,
                               @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                               @RequestParam(value = "name", required = false) String name) {
        // 分页查询改进 page<Dish> 查询数据 , page<DishDto> 返回数据
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();
        // 查询条件
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        if (name != null) {
            wrapper.like(Dish::getName, name);
        }
        dishService.page(pageInfo, wrapper);
        // 将数据转换为DishDto
        BeanUtils.copyProperties(pageInfo, dishDtoPage,"records");

        List<Dish> records = pageInfo.getRecords();
        // 将Dish的records转换为DishDto的records,并增加分类名称
        List<DishDto> list = records.stream().map(dish -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dish, dishDto);
            Long categoryId = dish.getCategoryId();
            if (categoryId != null) {
                dishDto.setCategoryName(categoryService.getById(categoryId).getName());
            }
            return dishDto;
        }).collect(Collectors.toList());
        dishDtoPage.setRecords(list);

        return Response.success(dishDtoPage);

    }

    @PostMapping
    public Response<String> save(@RequestBody DishDto dishDto) {
        log.info("dishDto:{}", dishDto);
        dishService.saveWithFlavor(dishDto);
        return Response.success("添加成功");
    }

    // 根据id查数据
    @GetMapping("{id}")
    public Response<DishDto> get(@PathVariable("id") Long id) {
//        DishDto dishDto = dishService.getDishDtoById(id);
        Dish dish = dishService.getById(id);
        // 根据dish id 查询flavor
        LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DishFlavor::getDishId, id);

        List<DishFlavor> dishFlavors = dishFlavorService.list(wrapper);

        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);
        dishDto.setFlavors(dishFlavors);

        return Response.success(dishDto);
    }
    // 根据id修改数据
    @PutMapping
    public Response<String> update(@RequestBody DishDto dishDto) {
        dishService.updateWithFlavor(dishDto);
        log.info("dishDto:{}", dishDto);
        return Response.success("修改成功");
    }

    // 修改状态
    @PostMapping("status/{status}")
    public Response<String> updateStatus(@PathVariable("status") Integer status,
                                         @RequestParam("ids") Long[] ids) {

        dishService.updateStatus(status, ids);
        return Response.success("修改成功");
    }

    // 删除
    @DeleteMapping
    public Response<String> delete(@RequestParam("ids") Long[] ids) {
        dishService.removeByIds(Arrays.asList(ids));
        return Response.success("删除成功");
    }
    // 根据分类id查询菜品
    @GetMapping("/list")
    public Response<List<DishDto>> list(@RequestParam("categoryId") Long categoryId,
                                        @RequestParam(value = "status",required = false) Integer status) {
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Dish::getCategoryId, categoryId);
        if (status != null) {
            wrapper.eq(Dish::getStatus, status);
        }

        List<Dish> list = dishService.list(wrapper);
        List<DishDto> dishDtos = list.stream().map(dish -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dish, dishDto);
            dishDto.setFlavors(dishFlavorService.list(new LambdaQueryWrapper<DishFlavor>().eq(DishFlavor::getDishId, dish.getId())));
            return dishDto;
        }).collect(Collectors.toList());
        return Response.success(dishDtos);
    }
}
