package com.smallmq.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smallmq.pojo.Orders;
import com.smallmq.service.OrderService;
import com.smallmq.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@Slf4j
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;


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
        Page<Orders> orders = orderService.page(ordersPage, wrapper);
        return Response.success(orders);
    }

    @PostMapping("/submit")
    public Response<String> submit(@RequestBody Orders orders,
                           HttpSession session) {
        log.info("orders={}", orders);
        //获取用户id
        Long userId = (Long) session.getAttribute("user");
        orders.setUserId(userId);
        orderService.submit(orders);
        return Response.success("提交成功");
    }

    // 查询订单
    @GetMapping("/userPage")
    public Response<Page> userPage(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            HttpSession session
    ) {
        log.info("page={},size={}", page, pageSize);
        Page<Orders> ordersPage = new Page<>(page, pageSize);
        Long userId = (Long) session.getAttribute("user");
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Orders::getUserId, userId).orderByDesc(Orders::getOrderTime);
        Page<Orders> orders = orderService.page(ordersPage, wrapper);
        return Response.success(orders);
    }

    // 修改订单状态
    @PutMapping
    public Response<String> update(@RequestBody Orders orders) {
        log.info("orders={}", orders);
        orderService.updateById(orders);
        return Response.success("修改成功");
    }

}

