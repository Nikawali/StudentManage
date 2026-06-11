package org.example.servlet;

import org.example.dao.AdminDAO;
import org.example.dao.ClassInfoDAO;
import org.example.dao.TeacherDAO;
import org.example.dao.UserDAO;
import org.example.entity.Admin;
import org.example.entity.Teacher;
import org.example.exception.BusinessesException;
import org.example.service.AdminService;
import org.example.service.TeacherService;
import org.example.servicrImpl.AdminServiceImpl;
import org.example.servicrImpl.TeacherServiceImpl;
import org.example.utils.Result;
import org.example.utils.WriteJson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin")
public class AdminServlet extends HttpServlet {
    private AdminService adminService;
    private TeacherService teacherService;

    @Override
    public void init() throws ServletException {
        adminService = new AdminServiceImpl(new AdminDAO());
        teacherService = new TeacherServiceImpl(
                new TeacherDAO(),
                new ClassInfoDAO(),
                new UserDAO());
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setCors(resp);
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setCors(resp);
        String action = req.getParameter("action");
        Result<?> result;

        if ("findById".equals(action)) {
            result = findById(req);
        } else if ("findTeachers".equals(action)) {
            result = findTeachers(req);
        } else if ("findTeacherById".equals(action)) {
            result = findTeacherById(req);
        } else {
            result = Result.fail("未知操作");
        }
        WriteJson.writeJson(resp, result);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setCors(resp);
        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");
        Result<?> result;

        if ("update".equals(action)) {
            result = updateAdmin(req);
        } else if ("addTeacher".equals(action)) {
            result = addTeacher(req);
        } else if ("updateTeacher".equals(action)) {
            result = updateTeacher(req);
        } else if ("deleteTeacher".equals(action)) {
            result = deleteTeacher(req);
        } else {
            result = Result.fail("未知操作");
        }
        WriteJson.writeJson(resp, result);
    }

    private Result<Admin> findById(HttpServletRequest req) {
        try {
            return Result.success(
                    "查询成功",
                    adminService.findById(req.getHeader("token")));
        } catch (BusinessesException e) {
            return Result.fail(e.getCode(), e.getMessage());
        }
    }

    private Result<Boolean> updateAdmin(HttpServletRequest req) {
        try {
            adminService.updateAdmin(buildAdmin(req), req.getHeader("token"));
            return Result.success("修改管理员成功", true);
        } catch (BusinessesException e) {
            return Result.fail(e.getCode(), e.getMessage());
        }
    }

    private Result<List<Teacher>> findTeachers(HttpServletRequest req) {
        try {
            return Result.success(
                    "查询成功",
                    teacherService.findAllTeachers(req.getHeader("token")));
        } catch (BusinessesException e) {
            return Result.fail(e.getCode(), e.getMessage());
        }
    }

    private Result<Teacher> findTeacherById(HttpServletRequest req) {
        try {
            return Result.success(
                    "查询成功",
                    teacherService.findTeacherById(parseTeacherId(req.getParameter("teacherId")),
                            req.getHeader("token")));
        } catch (BusinessesException e) {
            return Result.fail(e.getCode(), e.getMessage());
        }
    }

    private Result<Boolean> addTeacher(HttpServletRequest req) {
        try {
            teacherService.addTeacher(
                    buildTeacher(req),
                    parseTeacherId(req.getParameter("teacherId")),
                    req.getHeader("token"));
            return Result.success("新增教师成功", true);
        } catch (BusinessesException e) {
            return Result.fail(e.getCode(), e.getMessage());
        }
    }

    private Result<Boolean> updateTeacher(HttpServletRequest req) {
        try {
            teacherService.updateTeacherByAdmin(
                    buildTeacher(req),
                    parseTeacherId(req.getParameter("teacherId")),
                    req.getHeader("token"));
            return Result.success("修改教师成功", true);
        } catch (BusinessesException e) {
            return Result.fail(e.getCode(), e.getMessage());
        }
    }

    private Result<Boolean> deleteTeacher(HttpServletRequest req) {
        try {
            teacherService.deleteTeacher(
                    parseTeacherId(req.getParameter("teacherId")),
                    req.getHeader("token"));
            return Result.success("删除教师成功", true);
        } catch (BusinessesException e) {
            return Result.fail(e.getCode(), e.getMessage());
        }
    }

    private Admin buildAdmin(HttpServletRequest req) {
        Admin admin = new Admin();
        admin.setName(req.getParameter("name"));
        admin.setGender(req.getParameter("gender"));
        admin.setPhone(req.getParameter("phone"));
        return admin;
    }

    private Teacher buildTeacher(HttpServletRequest req) {
        Teacher teacher = new Teacher();

        // 姓名校验
        String name = req.getParameter("name");
        if (name == null || name.trim().isEmpty()) {
            throw new BusinessesException(400, "教师姓名不能为空");
        }
        teacher.setName(name.trim());

        // 性别校验
        String gender = req.getParameter("gender");
        if (gender == null || gender.trim().isEmpty()) {
            throw new BusinessesException(400, "性别不能为空");
        }
        teacher.setGender(gender.trim());

        // 手机号校验
        String phone = req.getParameter("phone");
        if (phone == null || phone.trim().isEmpty()) {
            throw new BusinessesException(400, "手机号不能为空");
        }
        teacher.setPhone(phone.trim());

        // 班级ID校验：允许为空；不为空则必须为数字且 > 0
        String classId = req.getParameter("classId");
        if (classId == null || classId.trim().isEmpty()) {
            teacher.setClassId(null);
        } else {
            try {
                teacher.setClassId(Integer.parseInt(classId.trim()));
            } catch (NumberFormatException e) {
                throw new BusinessesException(400, "班级ID必须是数字");
            }
        }

        return teacher;
    }

    private Long parseTeacherId(String teacherId) {
        if (teacherId == null || teacherId.trim().isEmpty()) {
            throw new BusinessesException(400, "教师ID不能为空");
        }
        try {
            return Long.parseLong(teacherId.trim());
        } catch (NumberFormatException e) {
            throw new BusinessesException(400, "教师ID必须是数字");
        }
    }

    private void setCors(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, token");
        resp.setHeader("Access-Control-Max-Age", "3600");
    }
}
