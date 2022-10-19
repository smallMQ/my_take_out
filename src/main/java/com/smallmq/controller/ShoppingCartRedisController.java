package com.smallmq.controller;

import com.smallmq.pojo.ShoppingCart;
import com.smallmq.service.ShoppingCartService;
import com.smallmq.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/shoppingCart")
public class ShoppingCartRedisController {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @RequestMapping("/add")
    public Response<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart,
                                      HttpSession session) {
        log.info("add shopping cart: {}", shoppingCart);
        Long user = (Long) session.getAttribute("user");
        shoppingCart.setUserId(user);
        // 判断购物车中是否有该商品
        String key = "shoppingCart:" + user;
        List<ShoppingCart> shoppingCarts = redisTemplate.opsForList().range(key, 0, -1);
        for (ShoppingCart cart : shoppingCarts) {
            if (cart.getDishId() != null && cart.getDishId().equals(shoppingCart.getDishId())) {
                cart.setNumber(cart.getNumber() + 1);
                redisTemplate.opsForList().set(key, shoppingCarts.indexOf(cart), cart);
                return Response.success(cart);
            }
            if (cart.getSetmealId() != null && cart.getSetmealId().equals(shoppingCart.getSetmealId())) {
                cart.setNumber(cart.getNumber() + 1);
                redisTemplate.opsForList().set(key, shoppingCarts.indexOf(cart), cart);
                return Response.success(cart);
            }
        }
        shoppingCart.setNumber(1);
        redisTemplate.opsForList().rightPush(key, shoppingCart);
        return Response.success(shoppingCart);
    }

    // 根据用户id显示list
    @GetMapping("/list")
    public Response<List<ShoppingCart>> list(HttpSession session) {
        Long user = (Long) session.getAttribute("user");
        // 从redis中获取购物车信息
        String key = "shoppingCart:" + user;
        List<ShoppingCart> shoppingCarts = redisTemplate.opsForList().range(key, 0, -1);
        return Response.success(shoppingCarts);

    }

    // 清空购物车
    @DeleteMapping("/clean")
    public Response<String> clean(HttpSession session) {
        Long user = (Long) session.getAttribute("user");
        // 删除redis中的购物车信息
        Boolean delete = redisTemplate.delete("shoppingCart:" + user);
        if (delete) {
            return Response.success("清空购物车成功");
        }
        return Response.error("清空购物车失败");
    }

    // 删除购物车中的商品
    @PostMapping("/sub")
    public Response<ShoppingCart> delete(
            @RequestBody ShoppingCart shoppingCart,
            HttpSession session
    ) {
        //获取用户id
        Long user = (Long) session.getAttribute("user");
        //获取redis中的购物车信息
        String key = "shoppingCart:" + user;
        List<ShoppingCart> shoppingCarts = redisTemplate.opsForList().range(key, 0, -1);
        for (ShoppingCart cart : shoppingCarts) {
            if (cart.getDishId() != null && cart.getDishId().equals(shoppingCart.getDishId())) {
                cart.setNumber(cart.getNumber() - 1);
                if (cart.getNumber() == 0) {
                    // 将该商品从购物车中删除
                    redisTemplate.opsForList().set(key, shoppingCarts.indexOf(cart), null);
                    redisTemplate.opsForList().remove(key, 0, null);
                } else {
                    redisTemplate.opsForList().set(key, shoppingCarts.indexOf(cart), cart);
                }
                return Response.success(cart);
            }
            if (cart.getSetmealId() != null && cart.getSetmealId().equals(shoppingCart.getSetmealId())) {
                cart.setNumber(cart.getNumber() - 1);
                if (cart.getNumber() == 0) {
                    // 将该商品从购物车中删除
                    redisTemplate.opsForList().set(key, shoppingCarts.indexOf(cart), null);
                    redisTemplate.opsForList().remove(key, 0, null);
                } else {
                    redisTemplate.opsForList().set(key, shoppingCarts.indexOf(cart), cart);
                }
                return Response.success(cart);
            }
        }
        return Response.success(shoppingCart);
    }

}
