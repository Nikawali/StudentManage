package org.example.servicrImpl;

import org.example.auth.AuthContext;
import org.example.auth.AuthService;
import org.example.dao.ClassInfoDAO;
import org.example.dao.StudentDAO;
import org.example.dao.TeacherDAO;
import org.example.entity.ClassInfo;
import org.example.entity.Teacher;
import org.example.exception.BusinessesException;
import org.example.service.ClassInfoService;
import org.example.utils.DBUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
        AuthService.requireAdmin(token);
        if (classInfo == null) {
            throw new BusinessesException(400, "班级不能为空");
        }
        Connection conn = DBUtils.getConnection();
        try {
            conn.setAutoCommit(false);

            Teacher teacher = null;
            if (classInfo.getTeacherId() != null) {
                teacher = teacherDAO.findById(conn, classInfo.getTeacherId());
                if (teacher == null) {
                    throw new BusinessesException(400, "教师不存在");
                }

                if (teacher.getClassId() != null) {
                    throw new BusinessesException(400, "该教师已绑定班级");
                }
            }

            boolean insertSuccess = classDAO.insert(conn, classInfo);
            if (!insertSuccess) {
                throw new BusinessesException(500, "新增班级失败");
            }

            Integer classId = classInfo.getId();
            if (classId == null) {
                throw new BusinessesException(500, "班级ID获取失败，数据异常");
            }

            if (teacher != null) {
                teacher.setClassId(classId);
                boolean updateSuccess = teacherDAO.updateClassId(conn, teacher);
                if (!updateSuccess) {
                    throw new BusinessesException(500, "教师绑定班级失败");
                }
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
        AuthContext auth = AuthService.requireTeacherOrAdmin(token);
        if (id == null || id <= 0) {
            throw new BusinessesException(400, "班级ID非法");
        }
        Connection conn = DBUtils.getConnection();
        try {
            conn.setAutoCommit(false);

            ClassInfo oldClass = classDAO.findById(conn, id);
            if (oldClass == null) {
                throw new BusinessesException(404, "班级不存在");
            }
            ensureTeacherCanAccessClass(auth, oldClass);

            int count = studentDAO.countByClassId(conn, id);
            if (count > 0) {
                throw new BusinessesException(400, "班级中还有学生，不能删除");
            }

            Long oldTeacherId = oldClass.getTeacherId();
            if (oldTeacherId != null) {
                Teacher oldTeacher = new Teacher();
                oldTeacher.setTeacherId(oldTeacherId);
                oldTeacher.setClassId(null); // 必须设为null
                teacherDAO.updateClassId(conn, oldTeacher);
            }

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
        AuthContext auth = AuthService.requireTeacherOrAdmin(token);
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
            ensureTeacherCanAccessClass(auth, oldClass);

            Long newTeacherId = classInfo.getTeacherId();
            if (auth.isTeacher()) {
                newTeacherId = auth.getUserId();
                classInfo.setTeacherId(newTeacherId);
            }

            Long oldTeacherId = oldClass.getTeacherId();
            if (auth.isAdmin()) {
                if (newTeacherId == null) {
                    if (oldTeacherId != null) {
                        Teacher oldTeacher = new Teacher();
                        oldTeacher.setTeacherId(oldTeacherId);
                        oldTeacher.setClassId(null);
                        teacherDAO.updateClassId(conn, oldTeacher);
                    }
                } else if (oldTeacherId == null || !oldTeacherId.equals(newTeacherId)) {
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

                    if (oldTeacherId != null) {
                        Teacher oldTeacher = new Teacher();
                        oldTeacher.setTeacherId(oldTeacherId);
                        oldTeacher.setClassId(null);
                        teacherDAO.updateClassId(conn, oldTeacher);
                    }

                    newTeacher.setClassId(classInfo.getId());
                    teacherDAO.updateClassId(conn, newTeacher);
                }
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
        AuthContext auth = AuthService.requireTeacherOrAdmin(token);
        List<ClassInfo> list = classDAO.findAll();
        if (auth.isAdmin()) {
            return list;
        }
        Teacher teacher = teacherDAO.findById(auth.getUserId());
        if (teacher == null) {
            throw new BusinessesException(404, "教师不存在");
        }
        if (teacher.getClassId() == null) {
            throw new BusinessesException(403, "当前教师未绑定班级");
        }
        List<ClassInfo> result = new ArrayList<>();
        for (ClassInfo item : list) {
            if (auth.getUserId().equals(item.getTeacherId())) {
                result.add(item);
            }
        }
        return result;
    }

    private void ensureTeacherCanAccessClass(AuthContext auth, ClassInfo classInfo) {
        if (auth.isAdmin()) {
            return;
        }
        if (classInfo.getTeacherId() == null || !auth.getUserId().equals(classInfo.getTeacherId())) {
            throw new BusinessesException(403, "教师仅可操作所属班级");
        }
    }
}
