package org.example.servlet;


import com.alibaba.fastjson2.JSON;
import org.example.dao.ClassInfoDAO;
import org.example.dao.StudentDAO;
import org.example.dao.TeacherDAO;
import org.example.entity.ClassInfo;
import org.example.exception.BusinessesException;
import org.example.service.ClassInfoService;
import org.example.servicrImpl.ClassInfoServiceImpl;
import org.example.utils.Result;
import org.example.utils.WriteJson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/class")
public class ClassInfoServlet extends HttpServlet {

    private ClassInfoService classInfoService;
    @Override
    protected void doOptions(
            HttpServletRequest req,
            HttpServletResponse resp)
            throws IOException {

        resp.setHeader(
                "Access-Control-Allow-Origin",
                "*");

        resp.setHeader(
                "Access-Control-Allow-Headers",
                "Content-Type, token");

        resp.setHeader(
                "Access-Control-Allow-Methods",
                "GET,POST,PUT,DELETE,OPTIONS");

        resp.setStatus(
                HttpServletResponse.SC_OK);
    }
    @Override
    public void init() throws ServletException {

        ClassInfoDAO classInfoDAO =
                new ClassInfoDAO();

        StudentDAO studentDAO =
                new StudentDAO();

        TeacherDAO teacherDAO=new TeacherDAO();

        classInfoService =
                new ClassInfoServiceImpl(
                        classInfoDAO,
                        studentDAO,
                        teacherDAO
                );
    }

    @Override
    protected void doGet(
            HttpServletRequest req,
            HttpServletResponse resp)
            throws ServletException, IOException {
        setCors(resp);
        String action =
                req.getParameter("action");

        Result<?> result;

       if ("findAll".equals(action)) {

            try {

                result = Result.success(
                        "查询成功",
                        classInfoService.findAll(req.getHeader("token"))
                );

            } catch (BusinessesException e) {

                result = Result.fail(
                        e.getCode(),
                        e.getMessage()
                );
            }

        } else {

            result = Result.fail("未知操作");
        }

        WriteJson.writeJson(resp, result);
    }

    @Override
    protected void doPost(
            HttpServletRequest req,
            HttpServletResponse resp)
            throws ServletException, IOException {
        setCors(resp);
        req.setCharacterEncoding("UTF-8");

        String action =
                req.getParameter("action");

        Result<?> result;

        if ("add".equals(action)) {

            result = addClass(req);

        } else if ("update".equals(action)) {

            result = updateClass(req);

        } else if ("delete".equals(action)) {

            result = deleteClass(req);

        } else {

            result = Result.fail("未知操作");
        }

        WriteJson.writeJson(resp, result);
    }

    private Result<Boolean> addClass(
            HttpServletRequest req) {

        try {

            ClassInfo classInfo =
                    buildClassInfo(req);
                String token=req.getHeader("token");
            classInfoService.addClass(
                    classInfo,token
            );

            return Result.success(
                    "新增班级成功",
                    true
            );

        } catch (BusinessesException e) {

            return Result.fail(
                    e.getCode(),
                    e.getMessage()
            );
        }
    }

    private Result<Boolean> updateClass(
            HttpServletRequest req) {

        try {

            ClassInfo classInfo =
                    buildClassInfo(req);
            Integer id=Integer.parseInt(req.getParameter("id"));

                String token=req.getHeader("token");

            classInfo.setId(id);

            classInfoService.updateClass(
                    classInfo,token
            );

            return Result.success(
                    "修改班级成功",
                    true
            );

        } catch (BusinessesException e) {

            return Result.fail(
                    e.getCode(),
                    e.getMessage()
            );
        }
    }

    private Result<Boolean> deleteClass(
            HttpServletRequest req) {

        try {
            String token=req.getHeader("token");
            Integer id =
                    Integer.parseInt(
                            req.getParameter("id"));

            classInfoService.deleteClass(
                    id,token
            );

            return Result.success(
                    "删除班级成功",
                    true
            );

        } catch (BusinessesException e) {

            return Result.fail(
                    e.getCode(),
                    e.getMessage()
            );
        }
    }


    private ClassInfo buildClassInfo(HttpServletRequest req) {
        ClassInfo classInfo = new ClassInfo();

        // 班级名称 校验：不能为空
        String className = req.getParameter("className");
        if (className == null || className.trim().isEmpty()) {
            throw new BusinessesException(400, "班级名称不能为空");
        }
        classInfo.setClassName(className.trim());

        String college = req.getParameter("college");
        if (college == null || college.trim().isEmpty()) {
            throw new BusinessesException(400, "学院不能为空");
        }
        classInfo.setCollege(college.trim());

        String major = req.getParameter("major");
        if (major == null || major.trim().isEmpty()) {
            throw new BusinessesException(400, "专业不能为空");
        }
        classInfo.setMajor(major.trim());

        // 教师ID 校验：允许为空；不为空则必须为数字
        String teacherId = req.getParameter("teacherId");
        if (teacherId == null || teacherId.trim().isEmpty()) {
            classInfo.setTeacherId(null);
        } else {
            try {
                classInfo.setTeacherId(Long.parseLong(teacherId.trim()));
            } catch (NumberFormatException e) {
                throw new BusinessesException(400, "教师ID必须是数字");
            }
        }

        return classInfo;
    }

    private void setCors(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, token");
        resp.setHeader("Access-Control-Max-Age", "3600");
    }
    
}
