package com.application.tennisApplication.service;

public interface UserService {
    boolean isPasswordValid(String password);
    boolean isEmailValid(String email);
}
