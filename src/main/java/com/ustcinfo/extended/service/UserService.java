package com.ustcinfo.extended.service;

import com.ustcinfo.extended.entity.User;
import com.ustcinfo.extended.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> listUser(){
        return userRepository.findAll();
    }
}
