package org.example.servicrImpl;

import org.example.auth.AuthContext;
import org.example.auth.AuthService;
import org.example.dao.ClassInfoDAO;
import org.example.dao.TeacherDAO;
import org.example.dao.UserDAO;
import org.example.entity.ClassInfo;
import org.example.entity.Teacher;
import org.example.entity.User;
import org.example.exception.BusinessesException;
import org.example.service.TeacherService;
import org.example.utils.DBUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class TeacherServiceImpl implements TeacherService {

    private final TeacherDAO teacherDAO;
    private final ClassInfoDAO classDAO;
    private final UserDAO userDAO;

    public TeacherServiceImpl(TeacherDAO teacherDAO, ClassInfoDAO classDAO, UserDAO userDAO) {
        this.teacherDAO = teacherDAO;
        this.classDAO = classDAO;
        this.userDAO = userDAO;
    }

    @Override
    public boolean updateTeacher(Teacher teacher, String token) {
        if (teacher == null) {
            throw new BusinessesException(400, "教师更新信息不能为空");
        }
        AuthContext auth = AuthService.requireTeacher(token);

        Connection conn = DBUtils.getConnection();
        try {
            conn.setAutoCommit(false);

            Long teacherId = auth.getUserId();
            Teacher oldTeacher = teacherDAO.findById(conn, teacherId);
            if (oldTeacher == null) {
                throw new BusinessesException(404, "教师不存在");
            }

            Teacher exist = teacherDAO.findByPhone(conn, teacher.getPhone());
            if (exist != null && !exist.getTeacherId().equals(teacherId)) {
                throw new BusinessesException(400, "手机号已被其他教师使用");
            }

            teacher.setTeacherId(teacherId);
            teacher.setClassId(oldTeacher.getClassId());

            boolean updateTeacherInfo = teacherDAO.update(conn, teacher);
            if (!updateTeacherInfo) {
                throw new BusinessesException(500, "修改教师信息失败");
            }

            conn.commit();
            return true;

        } catch (BusinessesException e) {
            try {
                if (conn != null)
                    conn.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException("回滚失败", ex);
            }
            throw e;
        } catch (Exception e) {
            try {
                if (conn != null)
                    conn.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException("回滚失败", ex);
            }
            e.printStackTrace();
            throw new BusinessesException(500, "系统异常");
        } finally {
            DBUtils.close(conn);
        }
    }

    @Override
    public Teacher findById(String token) {
        AuthContext auth = AuthService.requireTeacher(token);

        Connection conn = DBUtils.getConnection();
        try {
            Teacher teacher = teacherDAO.findById(conn, auth.getUserId());

            if (teacher == null) {
                throw new BusinessesException(404, "教师不存在");
            }
            return teacher;
        } catch (NumberFormatException e) {
            throw new BusinessesException(500, "教师ID格式错误");
        } catch (BusinessesException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessesException(500, "系统异常");
        } finally {
            DBUtils.close(conn);
        }
    }

    @Override
    public List<Teacher> findAllTeachers(String token) {
        AuthService.requireAdmin(token);
        return teacherDAO.findAll();
    }

    @Override
    public Teacher findTeacherById(Long teacherId, String token) {
        AuthService.requireAdmin(token);
        if (teacherId == null || teacherId <= 0) {
            throw new BusinessesException(400, "教师ID非法");
        }
        Teacher teacher = teacherDAO.findById(teacherId);
        if (teacher == null) {
            throw new BusinessesException(404, "教师不存在");
        }
        return teacher;
    }

    @Override
    public boolean addTeacher(Teacher teacher, Long teacherId, String token) {
        if (teacher == null) {
            throw new BusinessesException(400, "教师信息不能为空");
        }
        AuthService.requireAdmin(token);
        if (teacherId == null || teacherId <= 0) {
            throw new BusinessesException(400, "教师ID非法");
        }

        Connection conn = DBUtils.getConnection();
        try {
            conn.setAutoCommit(false);

            Teacher oldTeacher = teacherDAO.findById(conn, teacherId);
            if (oldTeacher != null) {
                throw new BusinessesException(400, "教师ID已存在");
            }

            User oldUser = userDAO.findByUserId(conn, teacherId);
            if (oldUser != null) {
                throw new BusinessesException(400, "用户ID已存在");
            }

            Teacher exist = teacherDAO.findByPhone(conn, teacher.getPhone());
            if (exist != null) {
                throw new BusinessesException(400, "手机号已被其他教师使用");
            }

            Integer classId = teacher.getClassId();
            if (classId != null) {
                ClassInfo classInfo = classDAO.findById(conn, classId);
                if (classInfo == null) {
                    throw new BusinessesException(404, "班级不存在");
                }
                if (classInfo.getTeacherId() != null && !classInfo.getTeacherId().equals(teacherId)) {
                    throw new BusinessesException(400, "班级已有老师，无法选择");
                }
            }

            teacher.setTeacherId(teacherId);
            boolean insertedTeacher = teacherDAO.insert(conn, teacher);
            if (!insertedTeacher) {
                throw new BusinessesException(500, "新增教师失败");
            }

            User user = new User();
            user.setUserId(teacherId);
            user.setPassword("88888888");
            user.setRole("teacher");

            boolean insertedUser = userDAO.insert(conn, user);
            if (!insertedUser) {
                throw new BusinessesException(500, "新增用户失败");
            }

            if (classId != null) {
                ClassInfo classInfo = classDAO.findById(conn, classId);
                if (classInfo != null) {
                    classInfo.setTeacherId(teacherId);
                    boolean updated = classDAO.update(conn, classInfo);
                    if (!updated) {
                        throw new BusinessesException(500, "班级绑定教师失败");
                    }
                }
            }

            conn.commit();
            return true;
        } catch (BusinessesException e) {
            rollback(conn);
            throw e;
        } catch (Exception e) {
            rollback(conn);
            throw new BusinessesException(500, "系统异常");
        } finally {
            DBUtils.close(conn);
        }
    }

    @Override
    public boolean updateTeacherByAdmin(Teacher teacher, Long teacherId, String token) {
        if (teacher == null) {
            throw new BusinessesException(400, "教师信息不能为空");
        }
        AuthService.requireAdmin(token);
        if (teacherId == null || teacherId <= 0) {
            throw new BusinessesException(400, "教师ID非法");
        }

        Connection conn = DBUtils.getConnection();
        try {
            conn.setAutoCommit(false);

            Teacher oldTeacher = teacherDAO.findById(conn, teacherId);
            if (oldTeacher == null) {
                throw new BusinessesException(404, "教师不存在");
            }

            Teacher exist = teacherDAO.findByPhone(conn, teacher.getPhone());
            if (exist != null && !exist.getTeacherId().equals(teacherId)) {
                throw new BusinessesException(400, "手机号已被其他教师使用");
            }

            Integer newClassId = teacher.getClassId();
            if (newClassId != null) {
                ClassInfo newClass = classDAO.findById(conn, newClassId);
                if (newClass == null) {
                    throw new BusinessesException(404, "班级不存在");
                }
                if (newClass.getTeacherId() != null && !newClass.getTeacherId().equals(teacherId)) {
                    throw new BusinessesException(400, "班级已有老师，无法选择");
                }
            }

            teacher.setTeacherId(teacherId);
            boolean updateTeacherInfo = teacherDAO.update(conn, teacher);
            if (!updateTeacherInfo) {
                throw new BusinessesException(500, "修改教师信息失败");
            }

            Integer oldClassId = oldTeacher.getClassId();
            if (oldClassId != null && !oldClassId.equals(newClassId)) {
                ClassInfo oldClass = classDAO.findById(conn, oldClassId);
                if (oldClass != null) {
                    oldClass.setTeacherId(null);
                    boolean updatedOld = classDAO.update(conn, oldClass);
                    if (!updatedOld) {
                        throw new BusinessesException(500, "解绑旧班级失败");
                    }
                }
            }

            if (newClassId != null && !newClassId.equals(oldClassId)) {
                ClassInfo newClass = classDAO.findById(conn, newClassId);
                newClass.setTeacherId(teacherId);
                boolean updatedNew = classDAO.update(conn, newClass);
                if (!updatedNew) {
                    throw new BusinessesException(500, "绑定新班级失败");
                }
            }

            conn.commit();
            return true;
        } catch (BusinessesException e) {
            rollback(conn);
            throw e;
        } catch (Exception e) {
            rollback(conn);
            throw new BusinessesException(500, "系统异常");
        } finally {
            DBUtils.close(conn);
        }
    }

    @Override
    public boolean deleteTeacher(Long teacherId, String token) {
        AuthService.requireAdmin(token);
        if (teacherId == null || teacherId <= 0) {
            throw new BusinessesException(400, "教师ID非法");
        }

        Connection conn = DBUtils.getConnection();
        try {
            conn.setAutoCommit(false);

            Teacher teacher = teacherDAO.findById(conn, teacherId);
            if (teacher == null) {
                throw new BusinessesException(404, "教师不存在");
            }

            Integer classId = teacher.getClassId();
            if (classId != null) {
                ClassInfo classInfo = classDAO.findById(conn, classId);
                if (classInfo != null) {
                    classInfo.setTeacherId(null);
                    boolean updated = classDAO.update(conn, classInfo);
                    if (!updated) {
                        throw new BusinessesException(500, "解绑班级失败");
                    }
                }
            }

            boolean deletedTeacher = teacherDAO.deleteById(conn, teacherId);
            if (!deletedTeacher) {
                throw new BusinessesException(500, "删除教师失败");
            }

            boolean deletedUser = userDAO.deleteById(conn, teacherId);
            if (!deletedUser) {
                throw new BusinessesException(500, "删除用户失败");
            }

            conn.commit();
            return true;
        } catch (BusinessesException e) {
            rollback(conn);
            throw e;
        } catch (Exception e) {
            rollback(conn);
            throw new BusinessesException(500, "系统异常");
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
