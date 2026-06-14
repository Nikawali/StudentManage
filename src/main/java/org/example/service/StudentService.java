package org.example.service;

import org.example.entity.Student;

import java.util.List;

public interface StudentService {

    boolean addStudent(Student student,String token);

    boolean deleteStudent(Long studentId,String token);

    boolean updateStudent(Student student,String token);

    Student findById(String token,Long studentId);

    List<Student> findAll(String token);

    List<Student> findByClassId(String token,Integer classId);

    List<Student> search(String keyword,String token);

}