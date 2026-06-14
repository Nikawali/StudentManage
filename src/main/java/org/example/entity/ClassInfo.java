package org.example.entity;


import org.example.exception.BusinessesException;

public class ClassInfo {

    private Integer classId;
    private String className;
    private Long teacherId;
    private Long studentCount;
    private String teacherName;
    private String college;
    private String major;

    public ClassInfo() {
    }

    public ClassInfo(Integer classInfoId,
                     String className,
                     Long teacherId,
                     String college,
                     String major) {

        setId(classInfoId);
        setClassName(className);
        setTeacherId(teacherId);
        setCollege(college);
        setMajor(major);
    }

    public Integer getId() {
        return classId;
    }

    public void setId(Integer classInfoId) {

        if (classInfoId != null && classInfoId <= 0) {
            throw new BusinessesException(400,"班级ID必须大于0");
        }

        this.classId = classInfoId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {

        if (className == null || className.trim().isEmpty()) {
            throw new BusinessesException(400,"班级名称不能为空");
        }

        if (className.length() > 50) {
            throw new BusinessesException(400,"班级名称长度不能超过50");
        }

        this.className = className.trim();
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
    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public Long getStudentCount() {
        return studentCount;
    }

    public void setStudentCount(Long studentCount) {
        this.studentCount = studentCount;
    }

    public String getCollege(){
        return college;
    }

    public void setCollege(String college){
        if(college==null||college.trim().isEmpty()){
            throw new BusinessesException(400,"学院不能为空");
        }
        this.college=college.trim();
    }

    public String getMajor(){
        return major;
    }

    public void setMajor(String major){
        if(major==null||major.trim().isEmpty()){
            throw new BusinessesException(400,"专业不能为空");
        }
        this.major=major.trim();
    }

    @Override
    public String toString() {
        return "ClassInfo{" +
                "classId=" + classId +
                ", className='" + className + '\'' +
                ", teacherId=" + teacherId +
                '}';
    }
}
