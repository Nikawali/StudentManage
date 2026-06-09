package org.example.dao;

import org.example.entity.ClassInfo;
import org.example.utils.DBUtils;
import org.example.utils.DaoUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ClassInfoDAO {


    public boolean insert(Connection conn, ClassInfo classInfo) {
        String sql = "insert into class_info " + "(className,teacherId) " + "values " + "(#{className},#{teacherId})";
        try (PreparedStatement ps = conn.prepareStatement(DaoUtils.parseSql(sql), Statement.RETURN_GENERATED_KEYS)) {
            DaoUtils.objectToPreparedStatement(classInfo, ps, DaoUtils.getFieldNames(sql).toArray(new String[0]));
            int rows = ps.executeUpdate();
            if (rows <= 0) {
                return false;
            } /* * 获取自增主键 */
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                classInfo.setId(rs.getInt(1));//返回主键
            }
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

        public boolean update (Connection conn,ClassInfo classInfo){

            String sql =
                    "update class_info set " +
                            "className=#{className}," +
                            "teacherId=#{teacherId} " +
                            "WHERE classId=#{classId}";

            try (
                 PreparedStatement ps = conn.prepareStatement(DaoUtils.parseSql(sql))) {

                DaoUtils.objectToPreparedStatement(classInfo, ps, DaoUtils.getFieldNames(sql).toArray(new String[0]));

                return ps.executeUpdate() > 0;

            } catch (Exception e) {

                throw new RuntimeException(e);
            }
        }

        public int countByTeacherId (Connection conn,Long teacherId){

            String sql = "select count(*) " + "from class_info " + "where teacherId=?";

            try (
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setLong(1, teacherId);

                ResultSet rs = ps.executeQuery();

                if (rs.next()) {

                    return rs.getInt(1);
                }

            } catch (Exception e) {

                throw new RuntimeException(e);
            }
            return 0;
        }


        public ClassInfo findById (Integer id){

            String sql = "select * from class_info where classId=?";

            try (Connection conn = DBUtils.getConnection();

                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, id);

                ResultSet rs = ps.executeQuery();

                if (rs.next()) {

                    return DaoUtils.resultSetToObject(rs, ClassInfo.class);
                }

            } catch (Exception e) {

                throw new RuntimeException(e);
            }

            return null;
        }

        public boolean deleteById (Connection conn,Integer id){

            String sql =
                    "delete from class_info where classId=?";

            try (

                    PreparedStatement ps =
                            conn.prepareStatement(sql)
            ) {

                ps.setInt(1, id);

                return ps.executeUpdate() > 0;

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }


        public List<ClassInfo> findAll () {

            List<ClassInfo> list =
                    new ArrayList<>();

            String sql =
                    "SELECT\n" +
                            "    c.classId,\n" +
                            "    c.className,\n" +
                            "    c.teacherId,\n" +
                            "    t.name teacherName,\n" +
                            "    COUNT(s.studentId) studentCount\n" +
                            "FROM class_info c\n" +
                            "LEFT JOIN teacher t\n" +
                            "    ON c.teacherId=t.teacherId\n" +
                            "LEFT JOIN student s\n" +
                            "    ON c.classId=s.classId\n" +
                            "GROUP BY\n" +
                            "    c.classId,\n" +
                            "    c.className,\n" +
                            "    c.teacherId,\n" +
                            "    t.name";

            try (
                    Connection conn =
                            DBUtils.getConnection();

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
                                            ClassInfo.class
                                    )
                    );
                }

            } catch (Exception e) {

                throw new RuntimeException(e);
            }
            return list;
        }

    public ClassInfo findById (Connection conn,Integer id){

        String sql = "select * from class_info where classId=?";

        try (
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                return DaoUtils.resultSetToObject(rs, ClassInfo.class);
            }

        } catch (Exception e) {

            throw new RuntimeException(e);
        }

        return null;
    }
    }
