package com.smallmq.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smallmq.mapper.OrderMapper;
import com.smallmq.pojo.*;
import com.smallmq.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {

    @Autowired
    private ShoppingCartService shoppingCartService;


    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    @Transactional
    public void submit(Orders orders) {
        // 查询用户购物车中的商品
//        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
//        wrapper.eq(ShoppingCart::getUserId, orders.getUserId());
//        List<ShoppingCart> list = shoppingCartService.list(wrapper);
        // 从redis查询用户购物车中的商品
        List<ShoppingCart> list = redisTemplate.opsForList().range("shoppingCart:" + orders.getUserId(), 0, -1);

        if (list.size() <= 0 || list == null) {
            throw new RuntimeException("购物车中没有商品");
        }
        // 查询用户数据
        User user = userService.getById(orders.getUserId());
        // 查询地址信息
        AddressBook addressBook = addressBookService.getById(orders.getAddressBookId());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (addressBook == null) {
            throw new RuntimeException("地址不存在");
        }
        // 判断余额是否充足
        BigDecimal amount1 = orders.getAmount();
        Integer amount2 = user.getBalance();
        if (amount1.compareTo(new BigDecimal(amount2)) > 0) {
            throw new RuntimeException("余额不足");
        }
        // 插入订单号
        Long orderId = System.currentTimeMillis();

        AtomicInteger amount = new AtomicInteger(0);


        orders.setNumber(orderId + "");
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));//总金额
        orders.setNumber(String.valueOf(orderId));
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));


        this.save(orders);


        List<OrderDetail> orderDetails = list.stream().map((item) -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orders.getId());
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());

        orders.setAmount(new BigDecimal(amount.get()));
        this.updateById(orders);


        //向订单明细表插入数据，多条数据
        orderDetailService.saveBatch(orderDetails);

        // 通过用户id加锁
        synchronized (user.getId().toString().intern()) {
            // 用户余额减少
            user.setBalance(user.getBalance() - amount.get());
            // 判断用户余额是否小于0
            if (user.getBalance() < 0) {
                throw new RuntimeException("余额不足");
            }
        }
        userService.updateById(user);


        //清空购物车数据
        redisTemplate.delete("shoppingCart:" + orders.getUserId());

    }
}
