package com.example.casestudy.Service;

import com.example.casestudy.model.Admin;

public interface AdminService {
    Admin findByUsername(String username);
    boolean checkPassword(Admin admin, String rawPassword); // Sẽ dùng để so sánh mật khẩu đã hash
}
