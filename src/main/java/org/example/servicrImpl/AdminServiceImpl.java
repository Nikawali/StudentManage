package org.example.servicrImpl;

import org.example.auth.AuthContext;
import org.example.auth.AuthService;
import org.example.dao.AdminDAO;
import org.example.entity.Admin;
import org.example.exception.BusinessesException;
import org.example.service.AdminService;
import org.example.utils.DBUtils;

import java.sql.Connection;

public class AdminServiceImpl implements AdminService {
    private final AdminDAO adminDAO;

    public AdminServiceImpl(AdminDAO adminDAO) {
        this.adminDAO = adminDAO;
    }

    @Override
    public Admin findById(String token) {
        AuthContext auth = AuthService.requireAdmin(token);
        Admin admin = adminDAO.findById(auth.getUserId());
        if (admin == null) {
            throw new BusinessesException(404, "管理员不存在");
        }
        return admin;
    }

    @Override
    public boolean updateAdmin(Admin admin, String token) {
        if (admin == null) {
            throw new BusinessesException(400, "管理员信息不能为空");
        }
        AuthContext auth = AuthService.requireAdmin(token);
        Connection conn = DBUtils.getConnection();
        try {
            Admin oldAdmin = adminDAO.findById(conn, auth.getUserId());
            if (oldAdmin == null) {
                throw new BusinessesException(404, "管理员不存在");
            }
            Admin exist = adminDAO.findByPhone(conn, admin.getPhone());
            if (exist != null && !exist.getAdminId().equals(auth.getUserId())) {
                throw new BusinessesException(400, "手机号已被其他管理员使用");
            }
            admin.setAdminId(auth.getUserId());
            return adminDAO.update(conn, admin);
        } finally {
            DBUtils.close(conn);
        }
    }
}
