package org.example.dao;

import org.example.entity.User;
import org.example.utils.DBUtils;
import org.example.utils.DaoUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDAO {
        private final DaoUtils daoUtils = new DaoUtils();

        public User findByUserId(
                        Long userId) {
                String sql = "select * from user " +
                                "where userId=?";

                try (
                                Connection conn = DBUtils.getConnection();

                                PreparedStatement ps = conn.prepareStatement(sql)) {

                        ps.setLong(
                                        1,
                                        userId);

                        try (
                                        ResultSet rs = ps.executeQuery()) {

                                if (rs.next())

                                        return daoUtils
                                                        .resultSetToObject(
                                                                        rs,
                                                                        User.class);

                        }

                } catch (Exception e) {

                        throw new RuntimeException(e);
                }

                return null;
        }

        public boolean deleteById(
                        Connection conn,
                        Long userId) {

                String sql = "delete from user " +
                                "where userId=?";

                try (
                                PreparedStatement ps = conn.prepareStatement(sql)) {

                        ps.setLong(1, userId);

                        return ps.executeUpdate() > 0;

                } catch (Exception e) {

                        throw new RuntimeException(e);
                }
        }

/*        public boolean insert(User user) {
                Connection conn = DBUtils.getConnection();
                try {
                        return insert(conn, user);
                } catch (Exception e) {
                        throw new RuntimeException(e);
                } finally {
                        DBUtils.close(conn);
                }
        }*/

        public User findByUserId(Connection conn, Long userId) {
                String sql = "select * from user where userId=?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        ps.setLong(1, userId);
                        try (ResultSet rs = ps.executeQuery()) {
                                if (rs.next()) {
                                        return daoUtils.resultSetToObject(rs, User.class);
                                }
                        }
                } catch (Exception e) {
                        throw new RuntimeException(e);
                }
                return null;
        }

        public boolean insert(Connection conn, User user) {
                String sql = "insert into user " +
                                "(userId,password,role) " +
                                "values " +
                                "(#{userId},#{password},#{role})";

                try (PreparedStatement ps = conn.prepareStatement(daoUtils.parseSql(sql))) {
                        daoUtils.objectToPreparedStatement(
                                        user,
                                        ps,
                                        daoUtils.getFieldNames(sql)
                                                        .toArray(new String[0]));
                        return ps.executeUpdate() > 0;
                } catch (Exception e) {
                        throw new RuntimeException(e);
                }
        }
}
