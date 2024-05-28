package com.hezae.skylineservice.mapper;

import com.hezae.skylineservice.model.User;
import java.util.List;
public interface UserMapper {
    //添加用户
    public User addUser(String username, String password, String email, String phone, String role, Long capacity);


    /*---------------------------查找--------------------------------------*/
    //根据用户名查找用户
    public User selectUserByUsername(String username);

    //根据用户id查找用户
    public User selectUserById(int id);
    //根据手机号查找用户
    public User selectUserByPhone(String phone);

    //根据邮箱查询用户
    public User selectUserByEmail(String email);

    //匹配用户的账户密码
    public User selectUserByUsernameAndPassword(String username, String password);

    /*---------------------------修改--------------------------------------*/
    //1.根据用户id修改用户信息
        //1.1修改用户名
        public void updateUsernameById(int id, String username);
        //1.2修改密码
        public void updatePasswordById(int id, String password);
        //1.3修改昵称
        public void updateNicknameById(int id, String nickname);
        //1.4修改手机号
        public void updatePhoneById(int id, String phone);
        //1.5修改邮箱
        public void updateEmailById(int id, String email);
        //1.6修改角色
        public void updateRoleById(int id, String role);
        //1.7修改容量
        public void updateCapacityById(int id, Long capacity);
        //1.8修改状态
        public void updateStatusById(int id, String status);
        //1.9修改签名
        public void updateSignatureById(int id ,String signature);

    //2.根据用户名修改用户信息
        //2.1修改用户名
        public void updateUsernameByUsername(String username, String newUsername);
        //2.2修改密码
        public void updatePasswordByUsername(String username, String password);
        //2.3修改昵称
        public void updateNicknameByUsername(String username, String nickname);
        //2.4修改手机号
        public void updatePhoneByUsername(String username, String phone);
        //2.5修改邮箱
        public void updateEmailByUsername(String username, String email);
        //2.6修改角色
        public void updateRoleByUsername(String username, String role);
        //2.7修改容量
        public void updateCapacityByUsername(String username, Long capacity);
        //2.8修改状态
        public void updateStatusByUsername(String username, String status);
        //2.9修改签名
        public void updateSignatureByUsername(String username ,String signature);


    /*---------------------------删除--------------------------------------*/
    //1.根据用户id删除用户
    public User deleteUserById(int id);
    //2.根据用户名删除用户
    public User deleteUserByUsername(String username);
    /*---------------------------其他--------------------------------------*/




}
