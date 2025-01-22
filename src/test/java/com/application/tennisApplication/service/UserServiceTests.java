package com.application.tennisApplication.service;

import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.IncludeTags;

@Suite
@SelectPackages("com.application.tennisApplication.validation")
@IncludeTags({"EmailValidation", "PasswordValidation"})
public class UserServiceTests {

}
