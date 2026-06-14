package org.example.auth;

import org.example.exception.BusinessesException;
import org.example.utils.RedisUtils;
import redis.clients.jedis.Jedis;

public final class AuthService {
    private AuthService() {
    }

    public static AuthContext requireLogin(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new BusinessesException(401, "未登录");
        }
        try (Jedis jedis = RedisUtils.getJedis()) {
            String userId = jedis.hget("token:" + token, "userId");
            String role = jedis.hget("token:" + token, "role");
            if (userId == null || role == null) {
                throw new BusinessesException(401, "登录已失效");
            }
            return new AuthContext(Long.parseLong(userId), role);
        } catch (BusinessesException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessesException(500, "Redis异常");
        }
    }

    public static AuthContext requireTeacherOrAdmin(String token) {
        AuthContext auth = requireLogin(token);
        if (!auth.isTeacherOrAdmin()) {
            throw new BusinessesException(403, "仅教师或管理员可操作");
        }
        return auth;
    }

    public static AuthContext requireTeacher(String token) {
        AuthContext auth = requireLogin(token);
        if (!auth.isTeacher()) {
            throw new BusinessesException(403, "仅教师可操作");
        }
        return auth;
    }

    public static AuthContext requireAdmin(String token) {
        AuthContext auth = requireLogin(token);
        if (!auth.isAdmin()) {
            throw new BusinessesException(403, "仅管理员可操作");
        }
        return auth;
    }
}
