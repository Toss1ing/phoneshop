package com.es.phoneshop.web.controller.pages;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoginControllerTest {

    @InjectMocks
    private LoginController loginController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoginReturnsLoginPageName() {
        String viewName = loginController.login();

        assertEquals("login", viewName);
    }
}
