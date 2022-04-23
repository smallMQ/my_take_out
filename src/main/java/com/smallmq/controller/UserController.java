package com.smallmq.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smallmq.pojo.User;
import com.smallmq.service.UserService;
import com.smallmq.utils.Response;
import com.smallmq.utils.SmsUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    // 发送验证码
    @RequestMapping("/sendMsg")
    public Response<String> sendMsg(
           @RequestBody User user,
           HttpSession session
    ) {
        log.info("sendCode");

        String sms = SmsUtils.sendSms(user.getPhone());
        if (sms == null) {
            return Response.error("发送失败");
        }
        session.setAttribute(user.getPhone(),sms);
        return Response.success("发送成功");
    }
    // 登录
    @RequestMapping("/login")
    public Response<User> login(
            @RequestBody Map map,

            HttpSession session
    ) {
        log.info("login");
        String phone = (String) map.get("phone");
        String code = (String) map.get("code");
        String sms = (String) session.getAttribute(phone);
        if (sms == null) {
            return Response.error("验证码已过期");
        }
        if (!sms.equals(code)) {
            return Response.error("验证码错误");
        }
        // 查询用户是否存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, phone);
        User user = userService.getOne(wrapper);
        if (user == null) {
            // 生成新用户
            user = new User();
            user.setPhone(phone);
            user.setStatus(1);
            userService.save(user);
        }
        session.setAttribute("user",user.getId());
        return Response.success(user);
    }
}
