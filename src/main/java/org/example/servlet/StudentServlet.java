package org.example.servlet;


import org.example.dao.ClassInfoDAO;
import org.example.dao.StudentDAO;
import org.example.dao.TeacherDAO;
import org.example.dao.UserDAO;
import org.example.entity.Student;
import org.example.exception.BusinessesException;
import org.example.service.StudentService;
import org.example.servicrImpl.StudentServiceImpl;
import org.example.utils.Result;
import org.example.utils.WriteJson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/student")
public class StudentServlet extends HttpServlet {

    private final StudentService studentService =
            new StudentServiceImpl(
                    new StudentDAO(),
                    new ClassInfoDAO(),
                    new TeacherDAO(),
                    new UserDAO()
            );

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
    protected void doGet(
            HttpServletRequest req,
            HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        setCors(resp);

        String action =
                req.getParameter("action");

        if (action == null) {
            WriteJson.writeJson(
                    resp,
                    Result.fail(
                            400,
                            "action不能为空"
                    )
            );
            return;
        }

        switch (action) {

            case "findById":
                findById(req, resp);
                break;

            case "findAll":
                findAll(resp,req);
                break;

            case "findByClassId":
                findByClassId(req,resp);
                break;

            case "search":
                search(req, resp);
                break;

            default:
                WriteJson.writeJson(
                        resp,
                        Result.fail(
                                400,
                                "未知操作"
                        )
                );
        }
    }

    @Override
    protected void doPost(
            HttpServletRequest req,
            HttpServletResponse resp)
             throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        setCors(resp);
        String action =
                req.getParameter("action");

        if (action == null) {

            WriteJson.writeJson(
                    resp,
                    Result.fail(
                            400,
                            "action不能为空"
                    )
            );
            return;
        }

        switch (action) {

            case "add":
                add(req, resp);
                break;

            case "update":
                update(req, resp);
                break;

            case "deleteStudent":
                deleteStudent(req, resp);
                break;

            default:
                WriteJson.writeJson(
                        resp,
                        Result.fail(
                                400,
                                "未知操作"
                        )
                );
        }
    }

    /**
     * 新增学生
     */
    private void add(
            HttpServletRequest req,
            HttpServletResponse resp)
            throws IOException {

        try {

            String token =
                    req.getHeader("token");

            Student student =
                    buildStudent(req);

            studentService.addStudent(
                    student,
                    token
            );

            WriteJson.writeJson(
                    resp,
                    Result.success(
                            "新增成功",
                            true
                    )
            );

        } catch (BusinessesException e) {

            WriteJson.writeJson(
                    resp,
                    Result.fail(
                            e.getCode(),
                            e.getMessage()
                    )
            );
        }
    }

    /**
     * 修改学生
     */
    private void update(
            HttpServletRequest req,
            HttpServletResponse resp)
            throws IOException {

        try {

            String token =
                    req.getHeader("token");

            Student student =
                    buildStudent(req);

            studentService.updateStudent(
                    student,
                    token
            );

            WriteJson.writeJson(
                    resp,
                    Result.success(
                            "修改成功",
                            true
                    )
            );

        } catch (BusinessesException e) {

            WriteJson.writeJson(
                    resp,
                    Result.fail(
                            e.getCode(),
                            e.getMessage()
                    )
            );
        }
    }

    /**
     * 删除学生
     */
    private void deleteStudent(
            HttpServletRequest req,
            HttpServletResponse resp)
            throws IOException {

        try {

            String token =
                    req.getHeader("token");

            Long studentId =
                    Long.parseLong(
                            req.getParameter(
                                    "studentId"
                            )
                    );

            studentService.deleteStudent(
                    studentId,
                    token
            );

            WriteJson.writeJson(
                    resp,
                    Result.success(
                            "删除成功",
                            true
                    )
            );

        } catch (BusinessesException e) {

            WriteJson.writeJson(
                    resp,
                    Result.fail(
                            e.getCode(),
                            e.getMessage()
                    )
            );
        }
    }

    /**
     * 根据ID查询
     */
    private void findById(
            HttpServletRequest req,
            HttpServletResponse resp)
            throws IOException {

        try {

            String token =
                    req.getHeader("token");

            String strStudentId =
                    req.getParameter(
                            "studentId"
                    );

            Long studentId = null;

            if (strStudentId != null &&
                    !strStudentId.trim().isEmpty()) {

                studentId =
                        Long.parseLong(
                                strStudentId
                        );
            }

            Student student =
                    studentService.findById(
                            token,
                            studentId
                    );

            WriteJson.writeJson(
                    resp,
                    Result.success(
                            "查询成功",
                            student
                    )
            );

        } catch (BusinessesException e) {

            WriteJson.writeJson(
                    resp,
                    Result.fail(
                            e.getCode(),
                            e.getMessage()
                    )
            );
        }
    }

    private void findByClassId(
            HttpServletRequest req,
            HttpServletResponse resp)
            throws IOException {

        try {

            String token =
                    req.getHeader("token");

            String strClassId =
                    req.getParameter(
                            "classId"
                    );
            Integer classId=-1;


            if (strClassId != null &&
                    !strClassId.trim().isEmpty()) {

                 classId =
                        Integer.parseInt(
                                strClassId
                        );
            }

            List<Student> list =
                    studentService.findByClassId(
                            token,
                            classId
                    );

            WriteJson.writeJson(
                    resp,
                    Result.success(
                            "查询成功",
                            list
                    )
            );

        } catch (BusinessesException e) {

            WriteJson.writeJson(
                    resp,
                    Result.fail(
                            e.getCode(),
                            e.getMessage()
                    )
            );
        }
    }
    /**
     * 查询全部
     */
    private void findAll(
            HttpServletResponse resp,
            HttpServletRequest req)
            throws IOException {

        try {

            String token =
                    req.getHeader("token");

            List<Student> list =
                    studentService.findAll(
                            token
                    );

            WriteJson.writeJson(
                    resp,
                    Result.success(
                            "查询成功",
                            list
                    )
            );

        } catch (BusinessesException e) {

            WriteJson.writeJson(
                    resp,
                    Result.fail(
                            e.getCode(),
                            e.getMessage()
                    )
            );
        }
    }

    private void search(
            HttpServletRequest req,
            HttpServletResponse resp)
            throws IOException {

        try {

            String token =
                    req.getHeader("token");

            String keyword =
                    req.getParameter(
                            "keyword"
                    );

            List<Student> list =
                    studentService.search(
                            keyword,
                            token
                    );

            WriteJson.writeJson(
                    resp,
                    Result.success(
                            "查询成功",
                            list
                    )
            );

        } catch (BusinessesException e) {

            WriteJson.writeJson(
                    resp,
                    Result.fail(
                            e.getCode(),
                            e.getMessage()
                    )
            );
        }
    }

    /**
     * 请求参数转Student
     */
    private Student buildStudent(HttpServletRequest req) {
        Student student = new Student();

        // 姓名校验
        String name = req.getParameter("name");
        if (name == null || name.trim().isEmpty()) {
            throw new BusinessesException(400, "姓名不能为空");
        }
        student.setName(name.trim());

        // 性别校验
        String gender = req.getParameter("gender");
        if (gender == null || gender.trim().isEmpty()) {
            throw new BusinessesException(400, "性别不能为空");
        }
        student.setGender(gender.trim());

        // 年龄校验
        String ageStr = req.getParameter("age");
        if (ageStr == null || ageStr.trim().isEmpty()) {
            throw new BusinessesException(400, "年龄不能为空");
        }
        try {
            student.setAge(Integer.parseInt(ageStr.trim()));
        } catch (NumberFormatException e) {
            throw new BusinessesException(400, "年龄必须是数字");
        }

        // 手机号校验
        String phone = req.getParameter("phone");
        if (phone == null || phone.trim().isEmpty()) {
            throw new BusinessesException(400, "手机号不能为空");
        }
        student.setPhone(phone.trim());

        String classIdStr = req.getParameter("classId");
        if (classIdStr == null || classIdStr.trim().isEmpty()) {
            student.setClassId(null); // 空 → 设为 null，存入数据库 NULL
        } else {
            try {
                student.setClassId(Integer.parseInt(classIdStr.trim()));
            } catch (NumberFormatException e) {
                throw new BusinessesException(400, "班级ID必须是数字");
            }
        }

        // 学号校验
        String studentIdStr = req.getParameter("studentId");
        if (studentIdStr == null || studentIdStr.trim().isEmpty()) {
            throw new BusinessesException(400, "学号不能为空");
        }
        try {
            student.setStudentId(Long.parseLong(studentIdStr.trim()));
        } catch (NumberFormatException e) {
            throw new BusinessesException(400, "学号必须是数字");
        }

        return student;
    }

    private void setCors(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, token");
        resp.setHeader("Access-Control-Max-Age", "3600");
    }
    
}
