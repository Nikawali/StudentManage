package org.example.service;

import org.example.entity.Teacher;

public interface TeacherService {

    /* boolean addTeacher(Teacher teacher); */

    boolean deleteTeacher(String token);

    boolean updateTeacher(
            Teacher teacher,
            String token
    );

    Teacher findById(String token);
}