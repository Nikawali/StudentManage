package org.example.service;

import org.example.entity.Admin;

public interface AdminService {
    Admin findById(String token);

    boolean updateAdmin(Admin admin, String token);
}
