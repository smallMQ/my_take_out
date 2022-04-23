package com.smallmq.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smallmq.mapper.UserMapper;
import com.smallmq.pojo.User;
import com.smallmq.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
