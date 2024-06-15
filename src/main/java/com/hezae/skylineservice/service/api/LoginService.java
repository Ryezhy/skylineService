package com.hezae.skylineservice.service.api;

import com.hezae.skylineservice.model.User;

public interface LoginService {
    //验证客户
    public boolean verifyUser(String username, String password);
    //获取用户信息
    public User getUserInfo(String username, String password);

    public User getUserInfo(int userId);
}
