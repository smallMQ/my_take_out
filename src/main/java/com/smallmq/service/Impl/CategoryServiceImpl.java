package com.smallmq.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smallmq.mapper.CategoryMapper;
import com.smallmq.pojo.Category;
import com.smallmq.service.CategoryService;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl
        extends ServiceImpl<CategoryMapper, Category>
        implements CategoryService {
}
