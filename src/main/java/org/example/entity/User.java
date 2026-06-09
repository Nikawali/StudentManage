package org.example.entity;

import org.example.exception.BusinessesException;

public class User {
    private Long userId;
    private String password;
    private String role;
    public User(){

    }

    public void setUserId(Long userId){
        if (userId != null && userId <= 0) {
            throw new BusinessesException(400,"学生ID必须大于0");
        }
        this.userId = userId;
    }

    public Long getUserId(){
        return userId;
    }

    public void setPassword(String password){
        if (password== null || password.trim().isEmpty()) {
            throw new BusinessesException(400,"密码不能为空");
        }

        if (password.length() < 6) {
            throw new BusinessesException(400,"密码长度不能少于6");
        }

        this.password = password.trim();
    }

    public String getPassword(){
        return password;
    }

    public void setRole(String role){
        if (!"student".equals(role) && !"teacher".equals(role)) {
            throw new BusinessesException(400,"角色为student或teacher");
        }

        this.role = role;
    }

    public String getRole(){
        return role;
    }

}
