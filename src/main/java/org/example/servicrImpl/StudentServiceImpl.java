package org.example.servicrImpl;

import org.example.dao.ClassInfoDAO;
import org.example.dao.StudentDAO;
import org.example.entity.ClassInfo;
import org.example.entity.Student;
import org.example.exception.BusinessesException;
import org.example.service.StudentService;
import org.example.utils.DBUtils;
import org.example.utils.RedisUtils;
import redis.clients.jedis.Jedis;

import java.sql.Connection;
import java.util.List;

public class StudentServiceImpl implements StudentService {

    private final StudentDAO studentDAO;
    private final ClassInfoDAO classInfoDAO;

    public StudentServiceImpl(StudentDAO studentDAO, ClassInfoDAO classInfoDAO) {
        this.studentDAO = studentDAO;
        this.classInfoDAO = classInfoDAO;
    }

    @Override
    public boolean addStudent(Student student, String token) {
        checkTeacher(token);

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
            }

            Student exist = studentDAO.findByPhone(conn, student.getPhone());
            if (exist != null) {
                throw new BusinessesException(400, "手机号已存在");
            }

            Student existStudent = studentDAO.findById(conn,student.getStudentId());
            if (existStudent != null) {
                throw new BusinessesException(400, "学号已存在，无法添加！");
            }
            boolean flag = studentDAO.insert(conn, student);
            if (!flag) {
                throw new BusinessesException(500, "新增失败");
            }

            conn.commit();
            return true;
        } catch (Exception e) {

            try {
                conn.rollback();
            } catch (Exception ex) {}

            if(e instanceof BusinessesException){
                throw (BusinessesException)e;
            }

            e.printStackTrace();

            throw new BusinessesException(
                    500,
                    "服务器异常"
            );

        } finally {
            DBUtils.close(conn);
        }
    }

    @Override
    public boolean deleteStudent(Long id, String token) {
        checkTeacher(token);
        if (id == null || id <= 0) {
            throw new BusinessesException(400, "学生ID非法");
        }

        Connection conn = DBUtils.getConnection();
        try {
            Student old =
                    studentDAO.findById(conn,id);

            if(old==null){

                throw new BusinessesException(
                        404,
                        "学生不存在"
                );
            }
            boolean flag = studentDAO.deleteById(conn, id);
            if (!flag) {
                throw new BusinessesException(500, "删除失败");
            }
            return true;
        } finally {
            DBUtils.close(conn);
        }
    }

    @Override
    public boolean updateStudent(Student student, String token) {
        checkTeacher(token);
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

            Integer classId = student.getClassId();
            if (classId != null) {
                ClassInfo classInfo = classInfoDAO.findById(conn, classId);
                if (classInfo == null) {
                    throw new BusinessesException(400, "该班级不存在！");
                }
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
            } catch (Exception ex) {}

            if(e instanceof BusinessesException){
                throw (BusinessesException)e;
            }

            e.printStackTrace();

            throw new BusinessesException(
                    500,
                    "服务器异常"
            );

        } finally {
            DBUtils.close(conn);
        }
    }

    @Override
    public Student findById(String token, Long id) {
        checkLogin(token);

        try (Jedis jedis = RedisUtils.getJedis()) {
            String userId = jedis.hget("token:" + token, "userId");
            String role = jedis.hget("token:" + token, "role");

            Connection conn = DBUtils.getConnection();
            try {
                if ("student".equals(role)) {
                    return studentDAO.findById(conn, Long.parseLong(userId));
                }
                if ("teacher".equals(role)) {
                    if (id == null || id <= 0) throw new BusinessesException(400, "学生ID非法");
                    return studentDAO.findById(conn, id);
                }
                throw new BusinessesException(403, "角色异常");
            } finally {
                DBUtils.close(conn);
            }

        } catch (BusinessesException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessesException(500, "系统异常");
        }
    }

    @Override
    public List<Student> findAll(String token) {
        checkTeacher(token);
        Connection conn = DBUtils.getConnection();
        try {
            return studentDAO.findAll(conn);
        } finally {
            DBUtils.close(conn);
        }
    }

    @Override
    public List<Student> findByClassId(String token, Integer classId) {
        checkTeacher(token);

        // ====================== 修复：提到try外面 ======================
        if (classId == null || classId <= 0) throw new BusinessesException(400, "班级ID非法");
        ClassInfo classInfo = classInfoDAO.findById(classId);
        if (classInfo == null) throw new BusinessesException(404, "没有此班级");

        Connection conn = DBUtils.getConnection();
        try {
            return studentDAO.findByClassId(conn, classId);
        } finally {
            DBUtils.close(conn);
        }
    }

    @Override
    public List<Student> search(String keyword, String token) {
        checkTeacher(token);
        Connection conn = DBUtils.getConnection();
        try {
            return studentDAO.search(conn, keyword);
        } finally {
            DBUtils.close(conn);
        }
    }

    public static String checkLogin(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new BusinessesException(401, "未登录");
        }
        try (Jedis jedis = RedisUtils.getJedis()) {
            String userId = jedis.hget("token:" + token, "userId");
            if (userId == null) throw new BusinessesException(401, "登录已失效");
            return userId;
        } catch (BusinessesException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessesException(500, "Redis异常");
        }
    }

    public static String checkTeacher(String token) {
        checkLogin(token);
        try (Jedis jedis = RedisUtils.getJedis()) {
            String role = jedis.hget("token:" + token, "role");
            if (!"teacher".equals(role)) throw new BusinessesException(403, "仅教师可操作");
            return role;
        } catch (BusinessesException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessesException(500, "Redis异常");
        }
    }
}