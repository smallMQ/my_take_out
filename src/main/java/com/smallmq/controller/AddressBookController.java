package com.smallmq.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.smallmq.pojo.AddressBook;
import com.smallmq.service.AddressBookService;
import com.smallmq.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;

/**
 * 地址簿管理
 */
@Slf4j
@RestController
@RequestMapping("/addressBook")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    /**
     * 新增
     */
    @PostMapping
    public Response<AddressBook> save(@RequestBody AddressBook addressBook,
                                      HttpSession session) {
        addressBook.setUserId((Long)session.getAttribute("user"));
        log.info("addressBook:{}", addressBook);
        addressBookService.save(addressBook);
        return Response.success(addressBook);
    }

    /**
     * 设置默认地址
     */
    @PutMapping("default")
    public Response<AddressBook> setDefault(@RequestBody AddressBook addressBook,
                                            HttpSession session) {
        log.info("addressBook:{}", addressBook);
        // 先将所有地址设置为非默认
        LambdaUpdateWrapper<AddressBook> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(AddressBook::getUserId, session.getAttribute("user"));
        wrapper.set(AddressBook::getIsDefault, 0);
        addressBookService.update(wrapper);
        // 再将指定地址设置为默认
        addressBook.setIsDefault(1);
        addressBookService.updateById(addressBook);
        return Response.success(addressBook);
    }

    /**
     * 根据id查询地址
     */
    @GetMapping("/{id}")
    public Response get(@PathVariable Long id) {
        AddressBook addressBook = addressBookService.getById(id);
        if (addressBook != null) {
            return Response.success(addressBook);
        } else {
            return Response.error("没有找到该对象");
        }
    }

    /**
     * 查询默认地址
     */
    @GetMapping("default")
    public Response<AddressBook> getDefault(
            HttpSession session
    ) {

        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId, session.getAttribute("user"));
        queryWrapper.eq(AddressBook::getIsDefault, 1);

        // 查询默认地址
        AddressBook addressBook = addressBookService.getOne(queryWrapper);

        if (null == addressBook) {
            return Response.error("没有找到该对象");
        } else {
            return Response.success(addressBook);
        }
    }

    /**
     * 查询指定用户的全部地址
     */
    @GetMapping("/list")
    public Response<List<AddressBook>> list(AddressBook addressBook,
                                            HttpSession session) {
        // 通过session获取用户id
        addressBook.setUserId((Long)session.getAttribute("user"));
        log.info("addressBook:{}", addressBook);

        //条件构造器
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(addressBook.getUserId() != null, AddressBook::getUserId, addressBook.getUserId());
        queryWrapper.orderByDesc(AddressBook::getUpdateTime);

        return Response.success(addressBookService.list(queryWrapper));
    }

    /**
     * 更新
     */
    @PutMapping
    public Response<AddressBook> update(@RequestBody AddressBook addressBook,
                                        HttpSession session) {
        addressBook.setUserId((Long)session.getAttribute("user"));
        log.info("addressBook:{}", addressBook);
        addressBookService.updateById(addressBook);
        return Response.success(addressBook);
    }

    /**
     * 删除
     */
    @DeleteMapping()
    public Response delete(@RequestParam Long[] ids) {
        addressBookService.removeByIds(Arrays.asList(ids));
        return Response.success("删除成功");
    }
}
