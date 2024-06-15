package com.hezae.skylineservice.controller;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.hezae.skylineservice.model.User;
import com.hezae.skylineservice.service.api.LoginService;
import com.hezae.skylineservice.tools.StpKit;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Objects;

@Controller
@CrossOrigin
@Slf4j
public class LoginController {
    @Autowired
    private LoginService loginService;
    @PostMapping("/login")
    public ResponseEntity<SaTokenInfo> login(@RequestBody User user) {
        boolean isSuccess = loginService.verifyUser(user.getUsername(), user.getPassword());
        if (isSuccess) {
         User user1 = loginService.getUserInfo(user.getUsername(), user.getPassword());
           if (Objects.equals(user1.getRole(), "superUser")){
               StpKit.SuperUser.login(user1.getId());
            }else if (Objects.equals(user1.getRole(), "user")){
                StpKit.USER.login(user1.getId());
            }else {
                StpKit.ANONYMOUS.login(user1.getId());
            }
            SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
            return new ResponseEntity<>(tokenInfo, HttpStatus.OK);
        } else {
            log.error("失败"+user.getUsername()+user.getPassword());
            return new ResponseEntity<>((SaTokenInfo) null, HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<SaResult> logout(@RequestBody User user) {
        log.info("logout"+user.getUsername()+user.getPassword());
        User user1 = loginService.getUserInfo(user.getUsername(), user.getPassword());
        if (Objects.equals(user1.getRole(), "superUser")){
            StpKit.SuperUser.logout(user1.getId());
        }else if (Objects.equals(user1.getRole(), "user")){
            StpKit.USER.logout(user1.getId());
        }else {
            StpKit.ANONYMOUS.logout(user1.getId());
        }
        return new ResponseEntity<>(SaResult.ok(), HttpStatus.OK);
    }
}
