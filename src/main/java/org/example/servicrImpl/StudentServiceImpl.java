package org.example.servicrImpl;

import org.example.auth.AuthContext;
import org.example.auth.AuthService;
import org.example.dao.ClassInfoDAO;
import org.example.dao.StudentDAO;
import org.example.dao.TeacherDAO;
import org.example.dao.UserDAO;
import org.example.entity.ClassInfo;
import org.example.entity.Student;
import org.example.entity.Teacher;
import org.example.entity.User;
import org.example.exception.BusinessesException;
import org.example.service.StudentService;
import org.example.utils.DBUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class StudentServiceImpl implements StudentService {

    private final StudentDAO studentDAO;
    private final ClassInfoDAO classInfoDAO;
    private final TeacherDAO teacherDAO;
    private final UserDAO userDAO;

    public StudentServiceImpl(StudentDAO studentDAO, ClassInfoDAO classInfoDAO, TeacherDAO teacherDAO,
            UserDAO userDAO) {
        this.studentDAO = studentDAO;
        this.classInfoDAO = classInfoDAO;
        this.teacherDAO = teacherDAO;
        this.userDAO = userDAO;
    }

    @Override
    public boolean addStudent(Student student, String token) {
        AuthContext auth = AuthService.requireAdmin(token);

        if (student == null) {
            throw new BusinessesException(400, "学生不能为空");
        }

        Connection conn = DBUtils.getConnection();
        try {
            conn.setAutoCommit(false);

            Integer classId = student.getClassId();
            if (classId != null) {
                ClassInfo classInfo = classInfoDAO.findById(conn, classId);
                if (classInfo == null) {
                    throw new BusinessesException(400, "该班级不存在，无法添加学生！");
                }
                ensureTeacherCanAccessClass(auth, classId);
            }

            Student exist = studentDAO.findByPhone(conn, student.getPhone());
            if (exist != null) {
                throw new BusinessesException(400, "手机号已存在");
            }

            Student existStudent = studentDAO.findById(conn, student.getStudentId());
            if (existStudent != null) {
                throw new BusinessesException(400, "学号已存在，无法添加！");
            }

            User existUser = userDAO.findByUserId(conn, student.getStudentId());
            if (existUser != null) {
                throw new BusinessesException(400, "用户ID已存在，无法添加！");
            }
            boolean flag = studentDAO.insert(conn, student);
            if (!flag) {
                throw new BusinessesException(500, "新增失败");
            }
            User user = new User();
            user.setUserId(student.getStudentId());
            user.setPassword("88888888");
            user.setRole("student");
            boolean insertedUser = userDAO.insert(conn, user);
            if (!insertedUser) {
                throw new BusinessesException(500, "新增用户失败");
            }

            conn.commit();
            return true;
        } catch (Exception e) {

            try {
                conn.rollback();
            } catch (Exception ex) {
            }

            if (e instanceof BusinessesException) {
                throw (BusinessesException) e;
            }

            e.printStackTrace();

            throw new BusinessesException(
                    500,
                    "服务器异常");

        } finally {
            DBUtils.close(conn);
        }
    }

    @Override
    public boolean deleteStudent(
            Long id,
            String token) {

        AuthContext auth = AuthService.requireTeacherOrAdmin(
                token);

        if (id == null || id <= 0) {

            throw new BusinessesException(
                    400,
                    "学生ID非法");
        }

        Connection conn = DBUtils.getConnection();

        try {

            conn.setAutoCommit(false);

            Student old = studentDAO.findById(
                    conn,
                    id);

            if (old == null) {

                throw new BusinessesException(
                        404,
                        "学生不存在");
            }

            ensureTeacherCanAccessStudent(
                    auth,
                    old);

            boolean deleteStudent = studentDAO.deleteById(
                    conn,
                    id);

            if (!deleteStudent) {

                throw new BusinessesException(
                        500,
                        "删除学生失败");
            }

            boolean deleteUser = userDAO.deleteById(
                    conn,
                    id);

            if (!deleteUser) {
                User existUser = userDAO.findByUserId(conn, id);
                if (existUser != null) {
                    throw new BusinessesException(
                            500,
                            "删除用户失败");
                }
            }

            conn.commit();

            return true;

        } catch (BusinessesException e) {

            rollback(conn);

            throw e;

        } catch (Exception e) {

            rollback(conn);

            e.printStackTrace();

            throw new BusinessesException(
                    500,
                    "系统异常");

        } finally {

            DBUtils.close(conn);
        }
    }

    @Override
    public boolean updateStudent(Student student, String token) {
        AuthContext auth = AuthService.requireTeacherOrAdmin(token);
        if (student == null) {
            throw new BusinessesException(400, "学生不能为空");
        }

        Connection conn = DBUtils.getConnection();
        try {
            conn.setAutoCommit(false);

            Student old = studentDAO.findById(conn, student.getStudentId());
            if (old == null) {
                throw new BusinessesException(404, "学生不存在");
            }
            ensureTeacherCanAccessStudent(auth, old);

            Integer classId = student.getClassId();
            if (classId != null) {
                ClassInfo classInfo = classInfoDAO.findById(conn, classId);
                if (classInfo == null) {
                    throw new BusinessesException(400, "该班级不存在！");
                }
                ensureTeacherCanAccessClass(auth, classId);
            }

            Student exist = studentDAO.findByPhone(conn, student.getPhone());
            if (exist != null && !exist.getStudentId().equals(student.getStudentId())) {
                throw new BusinessesException(400, "手机号已被其他学生使用");
            }

            boolean flag = studentDAO.update(conn, student);
            if (!flag) {
                throw new BusinessesException(500, "修改失败");
            }

            conn.commit();
            return true;
        } catch (Exception e) {

            try {
                conn.rollback();
            } catch (Exception ex) {
            }

            if (e instanceof BusinessesException) {
                throw (BusinessesException) e;
            }

            e.printStackTrace();

            throw new BusinessesException(
                    500,
                    "服务器异常");

        } finally {
            DBUtils.close(conn);
        }
    }

    @Override
    public Student findById(String token, Long id) {
        AuthContext auth = AuthService.requireLogin(token);
        Connection conn = DBUtils.getConnection();
        try {
            if (auth.isStudent()) {
                return studentDAO.findById(conn, auth.getUserId());
            }
            if (auth.isTeacherOrAdmin()) {
                if (id == null || id <= 0) {
                    throw new BusinessesException(400, "学生ID非法");
                }
                Student student = studentDAO.findById(conn, id);
                if (student == null) {
                    throw new BusinessesException(404, "学生不存在");
                }
                ensureTeacherCanAccessStudent(auth, student);
                return student;
            }
            throw new BusinessesException(403, "角色异常");
        } finally {
            DBUtils.close(conn);
        }
    }

    @Override
    public List<Student> findAll(String token) {
        AuthContext auth = AuthService.requireTeacherOrAdmin(token);
        Connection conn = DBUtils.getConnection();
        try {
            if (auth.isAdmin()) {
                return studentDAO.findAll(conn);
            }
            Integer classId = getTeacherClassId(conn, auth.getUserId());
            return studentDAO.findByClassId(conn, classId);
        } finally {
            DBUtils.close(conn);
        }
    }

    @Override
    public List<Student> findByClassId(String token, Integer classId) {
        AuthContext auth = AuthService.requireTeacherOrAdmin(token);
        if (classId == null || classId <= 0)
            throw new BusinessesException(400, "班级ID非法");
        Connection conn = DBUtils.getConnection();
        try {
            ClassInfo classInfo = classInfoDAO.findById(conn, classId);
            if (classInfo == null)
                throw new BusinessesException(404, "没有此班级");
            ensureTeacherCanAccessClass(auth, classId);
            return studentDAO.findByClassId(conn, classId);
        } finally {
            DBUtils.close(conn);
        }
    }

    @Override
    public List<Student> search(String keyword, String token) {
        AuthContext auth = AuthService.requireTeacherOrAdmin(token);
        Connection conn = DBUtils.getConnection();
        try {
            if (auth.isAdmin()) {
                return studentDAO.search(conn, keyword);
            }
            Integer classId = getTeacherClassId(conn, auth.getUserId());
            return studentDAO.searchByClassId(conn, keyword, classId);
        } finally {
            DBUtils.close(conn);
        }
    }

    private Integer getTeacherClassId(Connection conn, Long teacherId) {
        Teacher teacher = teacherDAO.findById(conn, teacherId);
        if (teacher == null) {
            throw new BusinessesException(404, "教师不存在");
        }
        if (teacher.getClassId() == null) {
            throw new BusinessesException(403, "当前教师未绑定班级");
        }
        return teacher.getClassId();
    }

    private void ensureTeacherCanAccessClass(AuthContext auth, Integer classId) {
        if (auth.isAdmin()) {
            return;
        }
        Connection conn = DBUtils.getConnection();
        try {
            Integer teacherClassId = getTeacherClassId(conn, auth.getUserId());
            if (!teacherClassId.equals(classId)) {
                throw new BusinessesException(403, "教师仅可操作所属班级");
            }
        } finally {
            DBUtils.close(conn);
        }
    }

    private void ensureTeacherCanAccessStudent(AuthContext auth, Student student) {
        if (student == null) {
            throw new BusinessesException(404, "学生不存在");
        }
        if (auth.isAdmin()) {
            return;
        }
        Connection conn = DBUtils.getConnection();
        try {
            Integer teacherClassId = getTeacherClassId(conn, auth.getUserId());
            if (student.getClassId() == null || !teacherClassId.equals(student.getClassId())) {
                throw new BusinessesException(403, "教师仅可操作所属班级学生");
            }
        } finally {
            DBUtils.close(conn);
        }
    }

    private void rollback(Connection conn) {
        try {
            if (conn != null) {
                conn.rollback();
            }
        } catch (SQLException e) {
            throw new RuntimeException("回滚失败", e);
        }
    }
}
