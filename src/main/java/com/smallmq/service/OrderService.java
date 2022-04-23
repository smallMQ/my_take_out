package com.smallmq.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smallmq.pojo.Orders;

public interface OrderService extends IService<Orders> {

    public void submit(Orders orders);
}
