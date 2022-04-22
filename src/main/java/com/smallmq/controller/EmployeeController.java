package com.smallmq.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smallmq.pojo.Employee;
import com.smallmq.service.EmployeeService;
import com.smallmq.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 登录
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public Response<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        log.info("login");

        // 获取密码进行md5加密
        String pwd = employee.getPassword();
        pwd = DigestUtils.md5DigestAsHex(pwd.getBytes());
        // 查询数据库根据用户名
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Employee::getUsername, employee.getUsername());
        Employee employee1 = employeeService.getOne(wrapper);
        if (employee1 == null) {
            return Response.error("用户名不存在");
        }
        // 校验密码
        if (!pwd.equals(employee1.getPassword())) {
            return Response.error("密码错误");
        }
        // 查看用户是否被禁用
        if (employee1.getStatus() == 0) {
            return Response.error("用户被禁用");
        }
        // 将用户id存入到session中
        request.getSession().setAttribute("employee", employee1.getId());
        log.info("login success");
        return Response.success(employee1);
    }

    /**
     * 退出登录
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public Response<String> logout(HttpServletRequest request) {
        log.info("logout");
        request.getSession().removeAttribute("employee");
        log.info("logout success");
        return Response.success("退出成功");
    }

    /**
     * 展示员工分页信息
     * @param page
     * @param pageSize
     */
    @GetMapping("/page")
    public Response<Page> page(@RequestParam(defaultValue = "1") Integer page,
                               @RequestParam(defaultValue = "10") Integer pageSize) {

        log.info("page");
        Page<Employee> page1 = new Page<>(page, pageSize);
        Page<Employee> employeePage = employeeService.page(page1);
        log.info("page success");
        return Response.success(employeePage);
    }
}
