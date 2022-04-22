package com.smallmq.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smallmq.pojo.Employee;
import com.smallmq.service.EmployeeService;
import com.smallmq.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

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
     * @param name
     */
    @GetMapping("/page")
    public Response<Page> page(@RequestParam(defaultValue = "1") Integer page,
                               @RequestParam(defaultValue = "10") Integer pageSize,
                                @RequestParam(defaultValue = "",required = false) String name) {

        log.info("page");
        Page<Employee> page1 = new Page<>(page, pageSize);
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        if (name != null && !name.equals("")) {
            log.info("name is not null");
            wrapper.like(Employee::getName, name);
        }
        Page<Employee> employeePage = employeeService.page(page1, wrapper);
        log.info("page success");
        return Response.success(employeePage);
    }

    /**
     * 添加员工
     * @param employee
     * @return
     */
    @PostMapping
    public Response<String> add(@RequestBody Employee employee, HttpServletRequest request) {
        log.info("add");
        // 查询用户是否存在
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Employee::getUsername, employee.getUsername());
        Employee employee1 = employeeService.getOne(wrapper);
        if (employee1 != null) {
            log.info("add fail username is exist");
            return Response.error("用户名已存在");
        }

        // 设置默认密码并进行md5加密
        String hex = DigestUtils.md5DigestAsHex("123456".getBytes());
        employee.setPassword(hex);
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());
        employee.setStatus(1);
        Long UserId = (Long)request.getSession().getAttribute("employee");
        employee.setCreateUser(UserId);
        employee.setUpdateUser(UserId);

        employeeService.save(employee);
        log.info("add success");
        return Response.success("添加成功");
    }

    /**
     * 根据id 设置用户状态
     * @param employee
     * @return
     */
    @PutMapping
    public Response<String> SetStatus(@RequestBody Employee employee, HttpServletRequest request) {
        log.info("SetStatus");
        Long UserId = (Long)request.getSession().getAttribute("employee");
        employee.setUpdateUser(UserId);
        employee.setStatus(employee.getStatus());
        employeeService.updateById(employee);
        log.info("SetStatus success");
        return Response.success("修改成功");
    }
}
