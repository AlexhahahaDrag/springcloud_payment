package com.alex.springcloud.controller;

import com.alex.springcloud.dao.UserMapper;
import com.alex.springcloud.entity.User;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/test/insert/{id}")
    public String testInsert(@PathVariable("id")Integer id) {
        User user = new User();
        user.setId(id);
        user.setName("1");
        userMapper.insert(user);
        return "success";
    }

    @GetMapping("/test/update/{id}")
    public String test(@PathVariable("id")Integer id) {
        User user = new User();
        user.setId(id);
        user.setName("11");
        UpdateWrapper<User> entityWrapper = new UpdateWrapper<>();
        entityWrapper.eq("name", user.getName());
        userMapper.update(user, entityWrapper);
        return "success";
    }

    @GetMapping("/test/delete/{id}")
    public String testDelete(@PathVariable("id") Integer id) {
        userMapper.deleteById(id);
        return "success";
    }
}
