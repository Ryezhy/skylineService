package com.hezae.skylineservice.service.impl;

import com.hezae.skylineservice.mapper.UserMapper;
import com.hezae.skylineservice.model.User;
import com.hezae.skylineservice.service.api.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service

public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Override
    public User selectUserById(int id) {
        return userMapper.selectUserById(id);
    }
}
