package org.example.dao;

import org.example.entity.User;
import org.example.utils.DBUtils;
import org.example.utils.DaoUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDAO {
    private final DaoUtils daoUtils =
            new DaoUtils();
    public User findByUserId(
            Long userId
    ){
        String sql =
                "select * from user " +
                        "where userId=?";

        try(
                Connection conn =
                        DBUtils.getConnection();

                PreparedStatement ps =
                        conn.prepareStatement(sql)
        ){

            ps.setLong(
                    1,
                    userId
            );

            try(
                    ResultSet rs =
                            ps.executeQuery()
            ){

                if(rs.next())

                    return daoUtils
                            .resultSetToObject(
                                    rs,
                                    User.class
                            );

            }

        }catch(Exception e){

            throw new RuntimeException(e);
        }

        return null;
    }

    public boolean insert(User user) {

        String sql =
                "insert into user " +
                        "(username,password,role) " +
                        "values " +
                        "(#{username},#{password},#{role})";

        try (
                Connection conn =
                        DBUtils.getConnection();

                PreparedStatement ps =
                        conn.prepareStatement(
                                daoUtils.parseSql(sql)
                        )
        ) {

            daoUtils.objectToPreparedStatement(
                    user,
                    ps,
                    daoUtils.getFieldNames(sql)
                            .toArray(new String[0])
            );

            return ps.executeUpdate() > 0;

        } catch (Exception e) {

            throw new RuntimeException(e);
        }
    }
}
