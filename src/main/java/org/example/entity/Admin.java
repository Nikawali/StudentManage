package org.example.entity;

import org.example.exception.BusinessesException;

public class Admin {
    private Long adminId;
    private String name;
    private String gender;
    private String phone;

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        if (adminId != null && adminId <= 0) {
            throw new BusinessesException(400, "管理员ID必须大于0");
        }
        this.adminId = adminId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new BusinessesException(400, "管理员姓名不能为空");
        }
        if (name.length() > 50) {
            throw new BusinessesException(400, "管理员姓名长度不能超过50");
        }
        this.name = name.trim();
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        if (!"男".equals(gender) && !"女".equals(gender)) {
            throw new BusinessesException(400, "性别只能为男或女");
        }
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        if (phone == null || !phone.matches("^1\\d{10}$")) {
            throw new BusinessesException(400, "手机号格式错误");
        }
        this.phone = phone;
    }
}
