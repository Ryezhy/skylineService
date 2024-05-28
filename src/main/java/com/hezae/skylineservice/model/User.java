package com.hezae.skylineservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "users")
public class User {
    //用户id
    @Id
    @Column(name = "id")
    int id;
    //用户名
    @Column(name = "username")
    String username;
    //密码
    @Column(name = "password")
    String password;
    //昵称
    @Column(name = "nickname")
    String nickname;
    //邮箱
    @Column(name = "email")
    String email;
    //手机号
    @Column(name = "phone")
    String phone;
    //角色
    @Column(name = "role")
    String role;
    //剩余容量
    @Column(name = "curren_capability")
    Long curren_capability;
    //容量
    @Column(name = "capacity")
    Long capacity;

    //状态
    @Column(name = "status")
    String status;

    //签名
    @Column(name = "signature")
    String signature;

    public User() {
    }

    public User(int id, String username, String password, String email, String phone, String role, Long capacity,Long curren_capability, String status,String signature) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.capacity = capacity;
        this.curren_capability = curren_capability;
        this.status = status;
        this.signature = signature;
    }


}
