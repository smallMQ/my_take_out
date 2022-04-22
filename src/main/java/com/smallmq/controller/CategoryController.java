package com.smallmq.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smallmq.pojo.Category;
import com.smallmq.service.CategoryService;
import com.smallmq.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
