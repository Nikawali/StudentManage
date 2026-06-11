package org.example.dao;

import org.example.entity.Teacher;
import org.example.utils.DBUtils;
import org.example.utils.DaoUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class TeacherDAO {

    public boolean insert(Connection conn, Teacher teacher) {
        String sql =
                "insert into teacher " +
                        "(teacherId,name,gender,phone,classId) " +
                        "values " +
                        "(#{teacherId},#{name},#{gender},#{phone},#{classId})";
        try (PreparedStatement ps = conn.prepareStatement(DaoUtils.parseSql(sql))) {
            DaoUtils.objectToPreparedStatement(
                    teacher,
                    ps,
                    DaoUtils.getFieldNames(sql)
                            .toArray(new String[0])
            );
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean update(Connection conn,Teacher teacher) {

        String sql =
                "update teacher set " +
                        "name=#{name}, " +
                        "gender=#{gender}, " +
                        "phone=#{phone}, " +
                        "classId=#{classId} "+
                        "where teacherId=#{teacherId}";

        try (
                PreparedStatement ps =
                        conn.prepareStatement(
                                DaoUtils.parseSql(sql)
                        )
        ) {

            DaoUtils.objectToPreparedStatement(
                    teacher,
                    ps,
                    DaoUtils.getFieldNames(sql)
                            .toArray(new String[0])
            );

            return ps.executeUpdate() > 0;

        } catch (Exception e) {

            throw new RuntimeException(e);
        }
    }

    public List<Teacher> findAll() {

        List<Teacher> list =
                new ArrayList<>();

        String sql =
                "select * from teacher";

        try (
                Connection conn =
                        DBUtils.getConnection();

                PreparedStatement ps =
                        conn.prepareStatement(sql);

                ResultSet rs =
                        ps.executeQuery()
        ) {

            while(rs.next()) {

                list.add(
                        DaoUtils.resultSetToObject(
                                rs,
                                Teacher.class
                        )
                );
            }

        } catch (Exception e) {

            throw new RuntimeException(e);
        }

        return list;
    }

    public Teacher findByPhone(Connection conn,String phone) {

        String sql =
                "select * from teacher " +
                        "where phone=?";

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
                        Teacher.class
                );
            }

        } catch (Exception e) {

            throw new RuntimeException(e);
        }

        return null;
    }

    public Teacher findById(Long id) {

        String sql =
                "select * from teacher where teacherId=?";

        try (
                Connection conn =
                        DBUtils.getConnection();

                PreparedStatement ps =
                        conn.prepareStatement(sql)
        ) {

            ps.setLong(1,id);

            ResultSet rs =
                    ps.executeQuery();

            if(rs.next()) {

                return DaoUtils.resultSetToObject(
                        rs,
                        Teacher.class
                );
            }

        } catch (Exception e) {

            throw new RuntimeException(e);
        }

        return null;
    }

    public boolean deleteById(Connection conn,Long id) {

        String sql =
                "delete from teacher where teacherId=?";

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


    public boolean updateClassId(Connection conn, Teacher teacher) {
        String sql = "UPDATE teacher SET classId=#{classId} WHERE teacherId=#{teacherId}";
        try (PreparedStatement ps = conn.prepareStatement(DaoUtils.parseSql(sql))) {
            DaoUtils.objectToPreparedStatement( teacher,
                    ps,
                    DaoUtils.getFieldNames(sql)
                            .toArray(new String[0]));
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public Teacher findById(Connection conn,Long id) {

        String sql =
                "select * from teacher where teacherId=?";

        try (

                PreparedStatement ps =
                        conn.prepareStatement(sql)
        ) {

            ps.setLong(1,id);

            ResultSet rs =
                    ps.executeQuery();

            if(rs.next()) {

                return DaoUtils.resultSetToObject(
                        rs,
                        Teacher.class
                );
            }

        } catch (Exception e) {

            throw new RuntimeException(e);
        }

        return null;
    }
}
