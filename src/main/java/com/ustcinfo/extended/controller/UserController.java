package com.ustcinfo.extended.controller;

import com.ustcinfo.extended.entity.User;
import com.ustcinfo.extended.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/list/user")
    public List<User> listUser(){
        return userService.listUser();
    }

}
