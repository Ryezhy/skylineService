package com.hezae.skylineservice.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckOr;
import cn.dev33.satoken.stp.StpUtil;
import com.hezae.skylineservice.model.User;
import com.hezae.skylineservice.service.api.LoginService;
import com.hezae.skylineservice.tools.StpKit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@CrossOrigin
public class UserController {
    @Autowired
    private LoginService loginService;

    // 测试登录，浏览器访问： http://localhost:8081/user/doLogin?username=zhang&password=123456
    @RequestMapping("doLogin")
    public String doLogin(String username, String password) {
        // 此处仅作模拟示例，真实项目需要从数据库中查询数据进行比对
        if("zhang".equals(username) && "123456".equals(password)) {
            StpUtil.login(10001);
            return "登录成功";
        }
        return "登录失败";
    }

    @RequestMapping("isLogin")
    public String isLogin() {
        return "当前会话是否登录：" + StpUtil.isLogin();
    }

    @SaCheckOr(
            login = {  @SaCheckLogin(type = "user"),@SaCheckLogin(type = "superUser") }
    )
    @PostMapping("/getUserInfo")
    @ResponseBody
    public ResponseEntity<User> getUserInfo(@RequestHeader("satoken") String satoken) {
        List<String> keys = StpKit.getUserIdAndRole();
        User user = loginService.getUserInfo(Integer.parseInt(keys.get(0)));
        if (user!= null) {
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            log.error("失败"+user.getUsername()+user.getPassword());
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
    }
}
