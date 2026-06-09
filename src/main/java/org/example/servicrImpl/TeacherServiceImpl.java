package org.example.servicrImpl;

import org.example.dao.ClassInfoDAO;
import org.example.dao.TeacherDAO;
import org.example.entity.ClassInfo;
import org.example.entity.Teacher;
import org.example.exception.BusinessesException;
import org.example.service.TeacherService;
import org.example.utils.DBUtils;
import org.example.utils.RedisUtils;
import redis.clients.jedis.Jedis;

import java.sql.Connection;
import java.sql.SQLException;

import static org.example.servicrImpl.StudentServiceImpl.checkTeacher;

public class TeacherServiceImpl implements TeacherService {

    private final TeacherDAO teacherDAO;
    private final ClassInfoDAO classDAO;

    public TeacherServiceImpl(TeacherDAO teacherDAO, ClassInfoDAO classDAO) {
        this.teacherDAO = teacherDAO;
        this.classDAO = classDAO;
    }

    @Override
    public boolean deleteTeacher(String token) {
        checkTeacher(token);

        Connection conn = DBUtils.getConnection();
        try (Jedis jedis = RedisUtils.getJedis()) {
            Long id = Long.parseLong(jedis.hget("token:" + token, "userId"));

            if (id <= 0) {
                throw new BusinessesException(400, "教师ID非法");
            }

            int count = classDAO.countByTeacherId(conn, id);
            if (count > 0) {
                throw new BusinessesException(400, "教师仍然管理班级，无法删除");
            }

            boolean flag = teacherDAO.deleteById(conn, id);
            if (!flag) {
                throw new BusinessesException(500, "删除教师失败");
            }

            return true;
        } finally {
            DBUtils.close(conn);
        }
    }

    @Override
    public boolean updateTeacher(Teacher teacher, String token) {
        if (teacher == null) {
            throw new BusinessesException(400, "教师更新信息不能为空");
        }
        checkTeacher(token);

        Connection conn = DBUtils.getConnection();
        try (Jedis jedis = RedisUtils.getJedis()) {
            conn.setAutoCommit(false);

            String userId = jedis.hget("token:" + token, "userId");
            if (userId == null) {
                throw new BusinessesException(401, "登录已失效");
            }

            Long teacherId = Long.parseLong(userId);
            // ============== 修复：全部使用同一个连接 ==============
            Teacher oldTeacher = teacherDAO.findById(conn, teacherId);
            if (oldTeacher == null) {
                throw new BusinessesException(404, "教师不存在");
            }

            // ============== 修复：手机号重复判断 ==============
            Teacher exist = teacherDAO.findByPhone(conn, teacher.getPhone());
            if (exist != null && !exist.getTeacherId().equals(teacherId)) {
                throw new BusinessesException(400, "手机号已被其他教师使用");
            }

            teacher.setTeacherId(teacherId);

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


            boolean updateTeacherInfo = teacherDAO.update(conn, teacher);
            if (!updateTeacherInfo) {
                throw new BusinessesException(500, "修改教师信息失败");
            }

            // 旧班级清空老师
            Integer oldClassId = oldTeacher.getClassId();
            if (oldClassId != null) {
                ClassInfo oldClass = classDAO.findById(conn, oldClassId);
                if (oldClass != null) {
                    oldClass.setTeacherId(null);
                    classDAO.update(conn, oldClass);
                }
            }

            // 新班级绑定老师
            if (newClassId != null) {
                ClassInfo newClass = classDAO.findById(conn, newClassId);
                newClass.setTeacherId(teacherId);
                classDAO.update(conn, newClass);
            }

            conn.commit();
            return true;

        } catch (BusinessesException e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) {
                throw new RuntimeException("回滚失败", ex);
            }
            throw e;
        } catch (Exception e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) {
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
        checkTeacher(token);

        Connection conn = DBUtils.getConnection();
        try (Jedis jedis = RedisUtils.getJedis()) {
            Long teacherId = Long.parseLong(jedis.hget("token:" + token, "userId"));
            Teacher teacher = teacherDAO.findById(conn, teacherId);

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
}