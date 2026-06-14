package org.example.servicrImpl;

import org.example.auth.AuthService;
import org.example.auth.Roles;
import org.example.dao.UserDAO;
import org.example.entity.Admin;
import org.example.entity.Student;
import org.example.entity.Teacher;
import org.example.entity.User;
import org.example.exception.BusinessesException;
import org.example.service.UserService;
import org.example.utils.DBUtils;
import org.example.utils.LoginResult;
import org.example.utils.RedisUtils;
import redis.clients.jedis.Jedis;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserServiceImpl implements UserService {
        private UserDAO userDAO = new UserDAO();
        /*
         * @Override
         * public Result<Boolean> register(User user) {
         *
         * User dbUser =
         * userDAO.findByUserId(
         * user.getUserId()
         * );
         *
         * if(dbUser != null){
         *
         * return Result.fail(
         * 400,
         * "学号已存在"
         * );
         * }
         *
         * userDAO.insert(user);
         *
         * return Result.success(
         * "注册成功",
         * true
         * );
         * }
         */

        @Override
        public LoginResult<?> login(User user) {

                User dbUser = userDAO.findByUserId(
                                user.getUserId());

                if (dbUser == null) {

                        throw new BusinessesException(
                                        400,
                                        "用户不存在");
                }

                if (!dbUser.getPassword()
                                .equals(user.getPassword())) {

                        throw new BusinessesException(
                                        400,
                                        "密码错误");
                }

                String token = UUID.randomUUID()
                                .toString()
                                .replace("-", "");

                try (
                                Jedis jedis = RedisUtils.getJedis()) {

                        String oldToken = jedis.get(
                                        "online:" +
                                                        dbUser.getUserId());

                        if (oldToken != null) {

                                jedis.del(
                                                "token:" +
                                                                oldToken);
                        }

                        Map<String, String> map = new HashMap<>();

                        map.put(
                                        "userId",
                                        String.valueOf(
                                                        dbUser.getUserId()));

                        map.put(
                                        "role",
                                        dbUser.getRole());

                        jedis.hset(
                                        "token:" + token,
                                        map);

                        jedis.expire(
                                        "token:" + token,
                                        60 * 60);

                        jedis.set(
                                        "online:" +
                                                        dbUser.getUserId(),
                                        token);

                        jedis.expire(
                                        "online:" +
                                                        dbUser.getUserId(),
                                        60 * 60);
                }

                if (Roles.ADMIN.equals(
                                dbUser.getRole())) {

                        LoginResult<Admin> result = new LoginResult<>();

                        result.setToken(token);
                        result.setRole(dbUser.getRole());

                        return result;
                }

                if (Roles.TEACHER.equals(
                                dbUser.getRole())) {

                        LoginResult<Teacher> result = new LoginResult<>();

                        result.setToken(token);
                        result.setRole(dbUser.getRole());

                        return result;
                }

                if (Roles.STUDENT.equals(
                                dbUser.getRole())) {

                        LoginResult<Student> result = new LoginResult<>();

                        result.setToken(token);
                        result.setRole(dbUser.getRole());

                        return result;
                }

                throw new BusinessesException(
                                500,
                                "用户角色异常");
        }

    @Override
    public boolean deleteUser(
            Long userId,
            String token
    ) {

        AuthService.requireAdmin(token);

        if(userId == null || userId <= 0){

            throw new BusinessesException(
                    400,
                    "用户ID非法"
            );
        }

        Connection conn =
                DBUtils.getConnection();

        try {

            conn.setAutoCommit(false);

            boolean flag =
                    userDAO.deleteById(
                            conn,
                            userId
                    );

            if(!flag){

                throw new BusinessesException(
                        404,
                        "用户不存在"
                );
            }

            conn.commit();

            return true;

        } catch (BusinessesException e) {

        try {
            if (conn != null) {
                conn.rollback();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        throw e;

    } catch (Exception e) {

        try {
            if (conn != null) {
                conn.rollback();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        e.printStackTrace();

        throw new BusinessesException(
                500,
                "系统异常"
        );
    }finally {

            DBUtils.close(conn);
        }
    }

        /*
         * @Override
         * Result<Boolean> logout(Long userId){
         * 
         * }
         */

}
