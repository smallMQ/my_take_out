package com.smallmq.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smallmq.pojo.Orders;
import com.smallmq.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/order")
public class OrderController {


    // 分页查询订单
    @RequestMapping("/page")
    public Response<Page> page(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,

            @RequestParam(value = "number", required = false) String number,
            // 开始日期-截止日期
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate
    ) {
        log.info("page={},size={}", page, pageSize);
        Page<Orders> ordersPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        if (number != null) {
            wrapper.eq(Orders::getNumber, number);
        }
        if (startDate != null && endDate != null) {
            wrapper.between(Orders::getOrderTime, startDate, endDate);
        }

        return Response.success(ordersPage);
    }

}

