package org.example.servlet;

import com.alibaba.fastjson2.JSON;
import org.example.dao.ClassInfoDAO;
import org.example.dao.TeacherDAO;
import org.example.entity.Teacher;
import org.example.exception.BusinessesException;
import org.example.service.TeacherService;
import org.example.servicrImpl.TeacherServiceImpl;
import org.example.utils.Result;
import org.example.utils.WriteJson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/teacher")
public class TeacherServlet extends HttpServlet {

    private TeacherService teacherService;

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

        TeacherDAO teacherDAO =
                new TeacherDAO();
        ClassInfoDAO classInfoDAO=new ClassInfoDAO();

        teacherService =
                new TeacherServiceImpl(
                        teacherDAO,
                        classInfoDAO
                );
    }

    @Override
    protected void doGet(
            HttpServletRequest req,
            HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setHeader("Access-Control-Allow-Origin", "*");           // 允许所有来源（开发阶段）
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
        resp.setHeader("Access-Control-Max-Age", "3600");
        String action =
                req.getParameter("action");

        Result<?> result;

        switch (action) {

            case "findById":
                result = findById(req);
                break;


            default:
                result = Result.fail("未知操作");
        }

        WriteJson.writeJson(resp, result);
    }

    @Override
    protected void doPost(
            HttpServletRequest req,
            HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setHeader("Access-Control-Allow-Origin", "*");           // 允许所有来源（开发阶段）
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
        resp.setHeader("Access-Control-Max-Age", "3600");
        req.setCharacterEncoding("UTF-8");

        String action =
                req.getParameter("action");

        Result<?> result;

        switch (action) {

/*            case "add":
                result = addTeacher(req);
                break;*/

            case "update":
                result = updateTeacher(req);
                break;

/*            case "delete":
                result = deleteTeacher(req);
                break;*/

            default:
                result = Result.fail("未知操作");
        }

        WriteJson.writeJson(resp, result);
    }


    private Result<Boolean> updateTeacher(
            HttpServletRequest req) {

        try {

            String token =
                    req.getHeader("token");

            Teacher teacher =
                    buildTeacher(req);

            teacherService.updateTeacher(
                    teacher,
                    token
            );

            return Result.success(
                    "修改教师成功",
                    true
            );

        } catch (BusinessesException e) {

            return Result.fail(
                    e.getCode(),
                    e.getMessage()
            );
        }
    }

/*    private Result<Boolean> deleteTeacher(
            HttpServletRequest req) {

        try {

            String token =
                    req.getHeader("token");

            teacherService.deleteTeacher(
                    token
            );

            return Result.success(
                    "删除教师成功",
                    true
            );

        } catch (BusinessesException e) {

            return Result.fail(
                    e.getCode(),
                    e.getMessage()
            );
        }
    }*/

    private Result<Teacher> findById(
            HttpServletRequest req) {

        try {

            String token =
                    req.getHeader("token");

            Teacher teacher =
                    teacherService.findById(
                            token
                    );

            return Result.success(
                    "查询成功",
                    teacher
            );

        } catch (BusinessesException e) {

            return Result.fail(
                    e.getCode(),
                    e.getMessage()
            );
        }
    }

    private Teacher buildTeacher(
            HttpServletRequest req) {

        Teacher teacher = new Teacher();

        teacher.setName(
                req.getParameter("name"));

        teacher.setGender(
                req.getParameter("gender"));

        teacher.setPhone(
                req.getParameter("phone"));

/*    teacher.setTeacherId(
                Long.parseLong(
                req.getParameter("teacherId")));*/

     teacher.setClassId(
                Integer.parseInt(
                        req.getParameter("classId")));

        return teacher;
    }
}