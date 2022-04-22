package com.smallmq.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smallmq.pojo.Category;
import com.smallmq.service.CategoryService;
import com.smallmq.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    // 分页查询

    /**
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public Response<Page> page(@RequestParam(defaultValue = "1") Integer page,
                               @RequestParam(defaultValue = "10") Integer pageSize) {

        Page<Category> pageInfo = new Page(page, pageSize);
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Category::getSort);
        Page<Category> page1 = categoryService.page(pageInfo, wrapper);
        log.info("pageInfo success");
        return Response.success(page1);
    }

    // 新增菜品/套餐分类
    @PostMapping
    public Response<Category> save(@RequestBody Category category) {
        // 判断分类名称是否存在
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getName, category.getName());
        Category category1 = categoryService.getOne(wrapper);
        if (category1 != null) {
            return Response.error("分类名称已存在");
        }
        categoryService.save(category);
        return Response.success(category);
    }
    // 删除分类
    @DeleteMapping
    public Response<Object> delete(@RequestParam("ids") Integer id) {
        categoryService.removeById(id);
        return Response.success("删除成功");
    }
    // 修改分类
    @PutMapping
    public Response<Category> update(@RequestBody Category category) {
        // 判断分类名称是否存在
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getName, category.getName());
        Category category1 = categoryService.getOne(wrapper);
        if (category1 != null) {
            return Response.error("分类名称已存在");
        }
        categoryService.updateById(category);
        return Response.success(category);
    }

}
