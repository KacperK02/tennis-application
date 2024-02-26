package com.application.tennisApplication.validation;

import com.application.tennisApplication.service.UserService;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Tag("PasswordValidation")
public class PasswordValidationTests {
    @Autowired
    UserService userService;

    @Test
    public void testValidPassword(){
        assertTrue(userService.isPasswordValid("Password123!"));
    }

    @Test
    public void testInvalidShortPassword() {
        assertFalse(userService.isPasswordValid("Abc123!"));
    }

    @Test
    public void testInvalidNoLowercaseLetter() {
        assertFalse(userService.isPasswordValid("UPPERCASE123!"));
    }

    @Test
    public void testInvalidNoUppercaseLetter() {
        assertFalse(userService.isPasswordValid("lowercase123!"));
    }

    @Test
    public void testInvalidNoSpecialCharacter() {
        assertFalse(userService.isPasswordValid("MixedCase123"));
    }
}
