package com.hezae.skylineservice.tools;

import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * StpLogic 门面类，管理项目中所有的 StpLogic 账号体系
 */
public class StpKit {

    /**
     * 默认原生会话对象
     */
    public static final StpLogic DEFAULT = StpUtil.stpLogic;

    /**
     * Admin 会话对象，管理 Admin 表所有账号的登录、权限认证
     */
    public static final StpLogic SuperUser = new StpLogic("superUser");

    /**
     * User 会话对象，管理 User 表所有账号的登录、权限认证
     */
    public static final StpLogic USER = new StpLogic("user");

    /**
     * XX 会话对象，（项目中有多少套账号表，就声明几个 StpLogic 会话对象）
     */
    public static final StpLogic ANONYMOUS= new StpLogic("anonymous");

    public  static List<String> getUserIdAndRole() {
        List<String> info = new ArrayList<>();
        StpLogic loggedInLogic = null;

        // 尝试获取用户角色
        try {
            loggedInLogic = StpKit.USER;
            info.add(StpKit.USER.getLoginId().toString());
            info.add("user");
        } catch (Exception e) {
            // 尝试获取管理员角色
            try {
                loggedInLogic = StpKit.SuperUser;
                info.add(StpKit.SuperUser.getLoginId().toString());
                info.add("admin");
            } catch (Exception e1) {
                // 尝试获取匿名用户角色
                try {
                    loggedInLogic = StpKit.ANONYMOUS;
                    info.add(StpKit.ANONYMOUS.getLoginId().toString());
                    info.add("anonymous");
                } catch (Exception ex) {
                    // 如果所有角色都未登录，则抛出异常
                    throw new RuntimeException("No role logged in");
                }
            }
        }
        // 如果有角色登录，则返回用户ID和角色信息
        if (loggedInLogic != null) {
            return info;
        } else {
            throw new RuntimeException("No role logged in");
        }
    }
}
