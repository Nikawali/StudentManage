package org.example.service;

import org.example.entity.Teacher;

import java.util.List;

public interface TeacherService {
    boolean updateTeacher(Teacher teacher, String token);

    Teacher findById(String token);

    List<Teacher> findAllTeachers(String token);

    Teacher findTeacherById(Long teacherId, String token);

    boolean addTeacher(Teacher teacher, Long teacherId, String token);

    boolean updateTeacherByAdmin(Teacher teacher, Long teacherId, String token);

    boolean deleteTeacher(Long teacherId, String token);
}
