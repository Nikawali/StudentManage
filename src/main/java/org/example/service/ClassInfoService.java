package org.example.service;

import org.example.entity.ClassInfo;
import org.example.utils.Result;

import java.util.List;

public interface ClassInfoService {

    boolean addClass(ClassInfo classInfo,String token);

    boolean deleteClass(Integer id,String token);

    boolean updateClass(ClassInfo classInfo,String token);

    ClassInfo findById(Integer id);

    List<ClassInfo> findAll(String token);
}