package com.smallmq.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smallmq.pojo.ShoppingCart;
import com.smallmq.service.SetMealDishService;
import com.smallmq.service.ShoppingCartService;
import com.smallmq.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @RequestMapping("/add")
    public Response<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart,
                                      HttpSession session) {
        log.info("add shopping cart: {}", shoppingCart);
        Long user = (Long) session.getAttribute("user");
        shoppingCart.setUserId(user);
        // 查询菜品ID
        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, user);
        if(dishId != null) {
            wrapper.eq(ShoppingCart::getDishId, dishId);
        }else{
            wrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }
        // 查询购物车中是否有该商品
        ShoppingCart cart = shoppingCartService.getOne(wrapper);
        if (cart == null) {
            shoppingCart.setNumber(1);
            shoppingCartService.save(shoppingCart);
            cart = shoppingCart;
        }else {
            cart.setNumber(cart.getNumber() + 1);
            shoppingCartService.updateById(cart);
        }
        return Response.success(cart);
    }

    // 根据用户id显示list
    @GetMapping("/list")
    public Response<List<ShoppingCart>> list(HttpSession session) {
        Long user = (Long) session.getAttribute("user");
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, user);
        return Response.success(shoppingCartService.list(wrapper));
    }

    // 清空购物车
    @DeleteMapping("/clean")
    public Response<String> clean(HttpSession session) {
        Long user = (Long) session.getAttribute("user");
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, user);
        shoppingCartService.remove(wrapper);
        return Response.success("清空购物车成功");
    }
    // 删除购物车中的商品
    @PostMapping("/sub")
    public Response<ShoppingCart> delete(
            @RequestBody ShoppingCart shoppingCart,
            HttpSession session
    ) {
        //获取用户id
        Long user = (Long) session.getAttribute("user");
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, user);

        // 判断种类
        if(shoppingCart.getDishId() != null) {
            // 查询菜品数量
            wrapper.eq(ShoppingCart::getDishId, shoppingCart.getDishId());
            ShoppingCart cart = shoppingCartService.getOne(wrapper);
            if(cart.getNumber() == 1) {
                shoppingCartService.remove(wrapper);
            }
            else {
                cart.setNumber(cart.getNumber() - 1);
                shoppingCartService.updateById(cart);
            }
            shoppingCart = cart;
        }
        if(shoppingCart.getSetmealId() != null) {
            wrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
            ShoppingCart cart = shoppingCartService.getOne(wrapper);
            if(cart.getNumber() == 1) {
                shoppingCartService.remove(wrapper);
            }
            else {
                cart.setNumber(cart.getNumber() - 1);
                shoppingCartService.updateById(cart);
            }
            shoppingCart = cart;
        }
        return Response.success(shoppingCart);


    }
}
