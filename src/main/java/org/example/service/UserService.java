package org.example.service;

import org.example.entity.User;
import org.example.utils.LoginResult;
import org.example.utils.Result;

public interface UserService {
    /*Result<Boolean> register(User user);*/

    LoginResult<?> login(User user);

    boolean deleteUser(Long userId,String token);

    /*Result<Boolean> logout(Long userId);*/
}
