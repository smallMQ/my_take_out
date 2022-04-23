package com.smallmq.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smallmq.mapper.ShoppingCartMapper;
import com.smallmq.pojo.ShoppingCart;
import com.smallmq.service.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart>  implements ShoppingCartService {
}
