package org.example.dao;

import org.example.entity.Admin;
import org.example.utils.DBUtils;
import org.example.utils.DaoUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AdminDAO {
    public Admin findById(Long adminId) {
        String sql = "select * from admin where adminId=?";
        try (
                Connection conn = DBUtils.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setLong(1, adminId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return DaoUtils.resultSetToObject(rs, Admin.class);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public Admin findById(Connection conn, Long adminId) {
        String sql = "select * from admin where adminId=?";
        try (
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setLong(1, adminId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return DaoUtils.resultSetToObject(rs, Admin.class);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public Admin findByPhone(Connection conn, String phone) {
        String sql = "select * from admin where phone=?";
        try (
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, phone);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return DaoUtils.resultSetToObject(rs, Admin.class);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public boolean update(Connection conn, Admin admin) {
        String sql = "update admin set " +
                "name=#{name}, " +
                "gender=#{gender}, " +
                "phone=#{phone} " +
                "where adminId=#{adminId}";
        try (
                PreparedStatement ps = conn.prepareStatement(DaoUtils.parseSql(sql))
        ) {
            DaoUtils.objectToPreparedStatement(
                    admin,
                    ps,
                    DaoUtils.getFieldNames(sql).toArray(new String[0])
            );
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
