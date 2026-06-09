package org.example.dao;

import org.example.entity.Student;
import org.example.utils.DBUtils;
import org.example.utils.DaoUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {


    public boolean insert(Connection conn,Student student) {

        String sql =
                "insert into student " +
                        "(studentId,name,gender,age,phone,classId) " +
                        "values " +
                        "(#{studentId},#{name},#{gender},#{age},#{phone},#{classId})";

        try (
                PreparedStatement ps =
                        conn.prepareStatement(
                                DaoUtils.parseSql(sql)
                        )
        ) {

            DaoUtils.objectToPreparedStatement(
                    student,
                    ps,
                    DaoUtils
                            .getFieldNames(sql)
                            .toArray(new String[0])
            );

            return ps.executeUpdate() > 0;

        } catch (Exception e) {

            throw new RuntimeException(e);
        }
    }

    public boolean update(Connection conn,Student student) {

        String sql =
                "update student set " +
                        "name=#{name}," +
                        "gender=#{gender}," +
                        "age=#{age}," +
                        "phone=#{phone}," +
                        "classId=#{classId} " +
                        "where studentId=#{studentId}";

        try (
                PreparedStatement ps =
                        conn.prepareStatement(
                                DaoUtils.parseSql(sql)
                        )
        ) {

            DaoUtils.objectToPreparedStatement(
                    student,
                    ps,
                    DaoUtils
                            .getFieldNames(sql)
                            .toArray(new String[0])
            );

            return ps.executeUpdate() > 0;

        } catch (Exception e) {

            throw new RuntimeException(e);
        }
    }

    public boolean deleteById(Connection conn,Long id) {

        String sql =
                "delete from student where studentId=?";

        try (
                PreparedStatement ps =
                        conn.prepareStatement(sql)
        ) {

            ps.setLong(1,id);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {

            throw new RuntimeException(e);
        }
    }


    public List<Student> findAll(Connection conn) {

        List<Student> list =
                new ArrayList<>();

        String sql = "SELECT " +
                "s.*, " +
                "c.className, " +
                "c.major, " +
                "c.college " +
                "FROM student s " +
                "LEFT JOIN class_info c ON s.classId = c.classId";

        try (

                PreparedStatement ps =
                        conn.prepareStatement(sql);

                ResultSet rs =
                        ps.executeQuery()
        ) {

            while (rs.next()) {

                list.add(
                        DaoUtils
                                .resultSetToObject(
                                        rs,
                                        Student.class
                                )
                );
            }

        } catch (Exception e) {

            throw new RuntimeException(e);
        }

        return list;
    }

    public List<Student> findByClassId(Connection conn,Integer classId) {
        List<Student> list = new ArrayList<>();

        String sql = "SELECT s.*, c.college, c.major FROM student s "
                + "LEFT JOIN class_info c ON s.classId = c.classId "
                + "WHERE s.classId=?";

        try (
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, classId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(DaoUtils.resultSetToObject(rs, Student.class));
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    public Student findByPhone(Connection conn,String phone) {

        String sql = "SELECT " +
                "s.*, " +
                "c.className, " +
                "c.major, " +
                "c.college " +
                "FROM student s " +
                "LEFT JOIN class_info c ON s.classId = c.classId " +
                "WHERE s.phone=?";

        try (
                PreparedStatement ps =
                        conn.prepareStatement(sql)
        ) {

            ps.setString(1,phone);

            ResultSet rs =
                    ps.executeQuery();

            if(rs.next()) {

                return DaoUtils.resultSetToObject(
                        rs,
                        Student.class
                );
            }

        } catch (Exception e) {

            throw new RuntimeException(e);
        }

        return null;
    }


    public Student findById(Connection conn,Long studentId) {

        String sql = "SELECT " +
                "s.*, " +
                "c.className, " +
                "c.major, " +
                "c.college " +
                "FROM student s " +
                "LEFT JOIN class_info c ON s.classId = c.classId " +
                "WHERE s.studentId=?";

        try (

                PreparedStatement ps =
                        conn.prepareStatement(sql)
        ) {

            ps.setLong(1,studentId);

            ResultSet rs =
                    ps.executeQuery();

            if(rs.next()) {

                return DaoUtils.resultSetToObject(
                        rs,
                        Student.class
                );
            }

        } catch (Exception e) {

            throw new RuntimeException(e);
        }

        return null;
    }

    public int countByClassId(Connection conn,Integer classId) {

        String sql =
                "select count(*) " +
                        "from student " +
                        "where classId=?";

        try (

                PreparedStatement ps =
                        conn.prepareStatement(sql)
        ) {

            ps.setInt(1,classId);

            ResultSet rs =
                    ps.executeQuery();

            if(rs.next()) {

                return rs.getInt(1);
            }

        } catch (Exception e) {

            throw new RuntimeException(e);
        }

        return 0;
    }


    public List<Student> search(Connection conn,String keyword) {

        List<Student> list =
                new ArrayList<>();

        String sql = "SELECT " +
                "s.*, " +
                "c.className, " +
                "c.major, " +
                "c.college " +
                "FROM student s " +
                "LEFT JOIN class_info c ON s.classId = c.classId " +
                "WHERE " +
                "CAST(s.studentId AS CHAR) LIKE ? " +
                "OR s.name LIKE ? " +
                "OR s.gender LIKE ? " +
                "OR CAST(s.age AS CHAR) LIKE ? " +
                "OR s.phone LIKE ? " +
                "OR CAST(s.classId AS CHAR) LIKE ? " +
                "OR c.college LIKE ? " +  // 学院在班级表
                "OR c.major LIKE ?";

        try (
                PreparedStatement ps =
                        conn.prepareStatement(sql)
        ) {

            String value =
                    "%" + keyword + "%";

            // 8个问号全部绑定同一个关键字
            for (int i = 1; i <= 8; i++) {

                ps.setString(i, value);
            }

            try (
                    ResultSet rs =
                            ps.executeQuery()
            ) {

                while (rs.next()) {

                    Student student =
                            DaoUtils.resultSetToObject(
                                    rs,
                                    Student.class
                            );

                    list.add(student);
                }
            }

        } catch (Exception e) {

            throw new RuntimeException(e);
        }

        return list;
    }
}
