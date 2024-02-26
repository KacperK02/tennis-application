package com.application.tennisApplication.validation;

import com.application.tennisApplication.service.UserService;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Tag("EmailValidation")
public class EmailValidationTests {

    @Autowired
    UserService userService;
    @Test
    public void testValidEmail() {
        assertTrue(userService.isEmailValid("test@example.com"));
    }

    @Test
    public void testInvalidEmailMissingAtSymbol() {
        assertFalse(userService.isEmailValid("testexample.com"));
    }

    @Test
    public void testInvalidEmailMissingDotAfterAtSymbol() {
        assertFalse(userService.isEmailValid("test@examplecom"));
    }

    @Test
    public void testInvalidEmailInvalidCharacters() {
        assertFalse(userService.isEmailValid("test!@example.com"));
    }

    @Test
    public void testInvalidEmailInvalidTLD() {
        assertFalse(userService.isEmailValid("test@example.123"));
    }
}
