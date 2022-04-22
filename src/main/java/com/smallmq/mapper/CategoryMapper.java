package com.smallmq.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smallmq.pojo.Category;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
