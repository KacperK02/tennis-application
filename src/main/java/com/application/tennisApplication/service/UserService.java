package com.application.tennisApplication.service;

import com.application.tennisApplication.model.User;

public interface UserService {
    boolean isPasswordValid(String password);
    boolean isEmailValid(String email);
}
