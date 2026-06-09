package org.example.servicrImpl;

import org.example.dao.UserDAO;
import org.example.entity.Student;
import org.example.entity.Teacher;
import org.example.entity.User;
import org.example.exception.BusinessesException;
import org.example.service.UserService;
import org.example.utils.LoginResult;
import org.example.utils.RedisUtils;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserServiceImpl implements UserService {
    private UserDAO userDAO=new UserDAO();
    private RedisUtils redisUtils;
/*    @Override
    public Result<Boolean> register(User user) {

        User dbUser =
                userDAO.findByUserId(
                        user.getUserId()
                );

        if(dbUser != null){

            return Result.fail(
                    400,
                    "学号已存在"
            );
        }

        userDAO.insert(user);

        return Result.success(
                "注册成功",
                true
        );
    }*/

    @Override
    public LoginResult<?> login(User user) {

        User dbUser =
                userDAO.findByUserId(
                        user.getUserId()
                );

        if (dbUser == null) {

            throw new BusinessesException(
                    400,
                    "用户不存在"
            );
        }

        if (!dbUser.getPassword()
                .equals(user.getPassword())) {

            throw new BusinessesException(
                    400,
                    "密码错误"
            );
        }

        String token =
                UUID.randomUUID()
                        .toString()
                        .replace("-", "");

        try (
                Jedis jedis =
                        RedisUtils.getJedis()
        ) {

            String oldToken =
                    jedis.get(
                            "online:" +
                                    dbUser.getUserId()
                    );

            if (oldToken != null) {

                jedis.del(
                        "token:" +
                                oldToken
                );
            }

            Map<String, String> map =
                    new HashMap<>();

            map.put(
                    "userId",
                    String.valueOf(
                            dbUser.getUserId()
                    )
            );

            map.put(
                    "role",
                    dbUser.getRole()
            );

            jedis.hset(
                    "token:" + token,
                    map
            );

            jedis.expire(
                    "token:" + token,
                    60 * 60
            );

            jedis.set(
                    "online:" +
                            dbUser.getUserId(),
                    token
            );

            jedis.expire(
                    "online:" +
                            dbUser.getUserId(),
                    60 * 60
            );
        }

        if ("teacher".equals(
                dbUser.getRole()
        )) {

            LoginResult<Teacher> result =
                    new LoginResult<>();

            result.setToken(token);
            result.setRole(dbUser.getRole());

            return result;
        }

        if ("student".equals(
                dbUser.getRole()
        )) {

            LoginResult<Student> result =
                    new LoginResult<>();

            result.setToken(token);
            result.setRole(dbUser.getRole());

            return result;
        }

        throw new BusinessesException(
                500,
                "用户角色异常"
        );
    }

/*
    @Override
    Result<Boolean> logout(Long userId){

    }
*/

}
