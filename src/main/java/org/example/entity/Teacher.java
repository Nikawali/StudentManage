package org.example.entity;


import org.example.exception.BusinessesException;

public class Teacher {

    private Long teacherId;
    private String name;
    private String gender;
    private String phone;
    private Integer classId;

    public Teacher() {
    }

    public Teacher(Long teacherId,
                   String name,
                   String phone,
                   String gender,
                   Integer classId) {

        setTeacherId(teacherId);
        setName(name);
        setPhone(phone);
        setGender(gender);
    }

    public Integer getClassId() {
        return classId;
    }

    public void setClassId(Integer classId) {

        if (classId != null && classId <= 0) {
            throw new BusinessesException(400,"班级ID必须大于0");
        }

        this.classId = classId;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {

        if (teacherId != null && teacherId <= 0) {
            throw new BusinessesException(400,"教师ID必须大于0");
        }

        this.teacherId = teacherId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {

        if (name == null || name.trim().isEmpty()) {
            throw new BusinessesException(400,"教师姓名不能为空");
        }

        if (name.length() > 50) {
            throw new BusinessesException(400,"教师姓名长度不能超过50");
        }

        this.name = name.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {

        if (phone == null || !phone.matches("^1\\d{10}$")) {
            throw new BusinessesException(400,"手机号格式错误");
        }

        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {

        if (!"男".equals(gender) && !"女".equals(gender)) {
            throw new BusinessesException(400,"性别只能为男或女");
        }

        this.gender = gender;
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "teacherId=" + teacherId +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", classId=" + classId +'\''+
                ", gender="+gender+
                '}';
    }
}