package org.example.servicrImpl;

import org.example.dao.ClassInfoDAO;
import org.example.dao.StudentDAO;
import org.example.dao.TeacherDAO;
import org.example.entity.ClassInfo;
import org.example.entity.Teacher;
import org.example.exception.BusinessesException;
import org.example.service.ClassInfoService;
import org.example.utils.DBUtils;
import redis.clients.jedis.Jedis;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.example.servicrImpl.StudentServiceImpl.checkTeacher;

public class ClassInfoServiceImpl implements ClassInfoService {

    private final ClassInfoDAO classDAO;
    private final StudentDAO studentDAO;
    private final TeacherDAO teacherDAO;

    public ClassInfoServiceImpl(ClassInfoDAO classDAO, StudentDAO studentDAO, TeacherDAO teacherDAO) {
        this.classDAO = classDAO;
        this.studentDAO = studentDAO;
        this.teacherDAO = teacherDAO;
    }

    @Override
    public boolean addClass(ClassInfo classInfo, String token) {
        checkTeacher(token);
        if (classInfo == null) {
            throw new BusinessesException(400, "班级不能为空");
        }
        Connection conn = DBUtils.getConnection();
        try {
            conn.setAutoCommit(false);

            if (classInfo.getTeacherId() == null) {
                throw new BusinessesException(400, "请输入教师Id");
            }

            // ===== 修复：必须用同一个连接 =====
            Teacher teacher = teacherDAO.findById(conn, classInfo.getTeacherId());
            if (teacher == null) {
                throw new BusinessesException(400, "教师不存在");
            }

            if (teacher.getClassId() != null) {
                throw new BusinessesException(400, "该教师已绑定班级");
            }

            boolean insertSuccess = classDAO.insert(conn, classInfo);
            if (!insertSuccess) {
                throw new BusinessesException(500, "新增班级失败");
            }

            Integer classId = classInfo.getId();
            if (classId == null) {
                throw new BusinessesException(500, "班级ID获取失败，数据异常");
            }

            teacher.setClassId(classId);
            boolean updateSuccess = teacherDAO.updateClassId(conn, teacher);
            if (!updateSuccess) {
                throw new BusinessesException(500, "教师绑定班级失败");
            }

            conn.commit();
            return true;
        } catch (Exception e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException("事务回滚失败", ex);
            }
            e.printStackTrace();
            if (e instanceof BusinessesException) {
                throw (BusinessesException) e;
            }
            throw new BusinessesException(500, "服务器异常，修改失败");
        } finally {
            DBUtils.close(conn);
        }
    }

    @Override
    public boolean deleteClass(Integer id, String token) {
        checkTeacher(token);
        if (id == null || id <= 0) {
            throw new BusinessesException(400, "班级ID非法");
        }
        Connection conn = DBUtils.getConnection();
        try {
            conn.setAutoCommit(false);

            // ===== 修复：必须用同一个连接查询 =====
            ClassInfo oldClass = classDAO.findById(conn, id);
            if (oldClass == null) {
                throw new BusinessesException(404, "班级不存在");
            }

            int count = studentDAO.countByClassId(conn, id);
            if (count > 0) {
                throw new BusinessesException(400, "班级中还有学生，不能删除");
            }

            // ===== 修复：旧老师清空班级 =====
            Long oldTeacherId = oldClass.getTeacherId();
            if (oldTeacherId != null) {
                Teacher oldTeacher = new Teacher();
                oldTeacher.setTeacherId(oldTeacherId);
                oldTeacher.setClassId(null); // 必须设为null
                teacherDAO.updateClassId(conn, oldTeacher);
            }

            // 删除班级
            boolean flag = classDAO.deleteById(conn, id);
            if (!flag) {
                throw new BusinessesException(500, "删除班级失败");
            }

            conn.commit();
            return true;
        } catch (Exception e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException("事务回滚失败", ex);
            }
            e.printStackTrace();
            if (e instanceof BusinessesException) {
                throw (BusinessesException) e;
            }
            throw new BusinessesException(500, "服务器异常，修改失败");
        } finally {
            DBUtils.close(conn);
        }
    }

    @Override
    public boolean updateClass(ClassInfo classInfo, String token) {
        checkTeacher(token);
        if (classInfo == null) {
            throw new BusinessesException(400, "班级不能为空");
        }
        Connection conn = DBUtils.getConnection();
        try {
            conn.setAutoCommit(false);

            ClassInfo oldClass = classDAO.findById(conn, classInfo.getId());
            if (oldClass == null) {
                throw new BusinessesException(404, "班级不存在");
            }

            Long newTeacherId = classInfo.getTeacherId();

            Teacher newTeacher = teacherDAO.findById(conn, newTeacherId);
            if (newTeacher == null) {
                throw new BusinessesException(400, "教师不存在");
            }

            if (
                    newTeacher.getClassId() != null
                            &&
                            !newTeacher.getClassId().equals(
                                    classInfo.getId()
                            )
            ) {

                throw new BusinessesException(
                        400,
                        "该教师已绑定其它班级"
                );
            }

            // 老师切换逻辑
            Long oldTeacherId = oldClass.getTeacherId();
            if (oldTeacherId == null || !oldTeacherId.equals(newTeacherId)) {
                if (oldTeacherId != null) {
                    Teacher oldTeacher = new Teacher();
                    oldTeacher.setTeacherId(oldTeacherId);
                    oldTeacher.setClassId(null);
                    teacherDAO.updateClassId(conn, oldTeacher);
                }

                newTeacher.setClassId(classInfo.getId());
                teacherDAO.updateClassId(conn, newTeacher);
            }

            boolean updateSuccess = classDAO.update(conn, classInfo);
            if (!updateSuccess) {
                throw new BusinessesException(500, "修改班级失败");
            }

            conn.commit();
            return true;
        } catch (Exception e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException("事务回滚失败", ex);
            }
            e.printStackTrace();
            if (e instanceof BusinessesException) {
                throw (BusinessesException) e;
            }
            throw new BusinessesException(500, "服务器异常，修改失败");
        } finally {
            DBUtils.close(conn);
        }
    }

    @Override
    public ClassInfo findById(Integer id) {
        if (id == null || id <= 0) {
            throw new BusinessesException(400, "班级ID非法");
        }
        ClassInfo classInfo = classDAO.findById(id);
        if (classInfo == null) {
            throw new BusinessesException(404, "班级不存在");
        }
        return classInfo;
    }

    @Override
    public List<ClassInfo> findAll(String token) {
        checkTeacher(token);
        return classDAO.findAll();
    }
}