package com.hezae.skylineservice.controller;

import com.hezae.skylineservice.model.User;
import com.hezae.skylineservice.service.api.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@CrossOrigin
@Slf4j
public class LoginController {
    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user) {
        boolean isSuccess = loginService.verifyUser(user.getUsername(), user.getPassword());
        if (isSuccess) {
            log.info("成功");
            return new ResponseEntity<>("账户或密码正常", HttpStatus.OK);
        } else {
            log.error("失败"+user.getUsername()+user.getPassword());
            return new ResponseEntity<>("账户或密码错误", HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/getUserInfo")
    public ResponseEntity<User> getUserInfo(@RequestBody User user) {
        User User = loginService.getUserInfo(user.getUsername(), user.getPassword());
        if (User!= null) {
            log.info("成功"+User.getCurren_capability());
            return new ResponseEntity<>(User, HttpStatus.OK);
        } else {
            log.error("失败"+user.getUsername()+user.getPassword());
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
    }
}
