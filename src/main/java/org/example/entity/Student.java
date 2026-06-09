package org.example.entity;

import org.example.exception.BusinessesException;

public class Student {

    private Long studentId;
    private String name;
    private String gender;
    private Integer age;
    private String phone;
    private Integer classId;
    private String college;
    private String major;

    public Student() {
    }

    public Student(Long studentId,
                   String name,
                   String gender,
                   Integer age,
                   String phone,
                   Integer classId,
                   String college,
                   String major) {

        setStudentId(studentId);
        setName(name);
        setGender(gender);
        setAge(age);
        setPhone(phone);
        setClassId(classId);
        setCollege(college);
        setMajor(major);
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        if (studentId != null && studentId <= 0) {
            throw new BusinessesException(400, "学生ID必须大于0");
        }
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new BusinessesException(400, "姓名不能为空");
        }
        if (name.length() > 50) {
            throw new BusinessesException(400, "姓名长度不能超过50");
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

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        if (age == null) {
            throw new BusinessesException(400, "年龄不能为空");
        }
        if (age < 0 || age > 120) {
            throw new BusinessesException(400, "年龄必须在0~120之间");
        }
        this.age = age;
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

    public Integer getClassId() {
        return classId;
    }

    public void setClassId(Integer classId) {
        if (classId != null && classId <= 0) {
            throw new BusinessesException(400, "班级ID必须大于0");
        }
        this.classId = classId;
    }

    public String getCollege(){
        return college;
    }

    public void setCollege(String college){
        if(college==null||college.trim().isEmpty()){
            throw new BusinessesException(400, "学院不能为空");
        }
        this.college=college;
    }

    public String getMajor(){
        return major;
    }

    public void setMajor(String major){
        if(major==null||major.trim().isEmpty()){
            throw new BusinessesException(400, "专业不能为空");
        }
        this.major=major;
    }

    @Override
    public String toString() {
        return "Student{" +
                "studentId=" + studentId +
                ", name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", age=" + age +
                ", phone='" + phone + '\'' +
                ", classId=" + classId +
                ", college='" + college + '\'' +
                ", major='" + major + '\'' +
                '}';
    }
}