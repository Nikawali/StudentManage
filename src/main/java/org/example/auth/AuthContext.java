package org.example.auth;

public class AuthContext {
    private final Long userId;
    private final String role;

    public AuthContext(Long userId, String role) {
        this.userId = userId;
        this.role = role;
    }

    public Long getUserId() {
        return userId;
    }

    public String getRole() {
        return role;
    }

    public boolean isStudent() {
        return Roles.STUDENT.equals(role);
    }

    public boolean isTeacher() {
        return Roles.TEACHER.equals(role);
    }

    public boolean isAdmin() {
        return Roles.ADMIN.equals(role);
    }

    public boolean isTeacherOrAdmin() {
        return isTeacher() || isAdmin();
    }
}
