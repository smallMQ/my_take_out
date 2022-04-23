package com.smallmq.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smallmq.pojo.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderDetailMapper extends BaseMapper<OrderDetail> {
}
