package com.application.tennisApplication.service;

import com.application.tennisApplication.model.User;

public interface UserService {
    void saveUser(User user);
    boolean isPasswordValid(String password);
    boolean isEmailValid(String email);
}
