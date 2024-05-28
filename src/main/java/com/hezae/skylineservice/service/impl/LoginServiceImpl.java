package com.hezae.skylineservice.service.impl;

import com.hezae.skylineservice.mapper.UserMapper;
import com.hezae.skylineservice.model.User;
import com.hezae.skylineservice.service.api.LoginService;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceImpl implements LoginService {
    @Autowired
    private UserMapper userMapper;
    @Override
    public boolean verifyUser(String username, String password) {
        return userMapper.selectUserByUsernameAndPassword(username, password) != null;
    }

    @Override
    public User getUserInfo(String username, String password) {
        return userMapper.selectUserByUsernameAndPassword(username, password);
    }
}
