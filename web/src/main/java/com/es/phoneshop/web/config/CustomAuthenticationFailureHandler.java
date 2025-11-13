package com.es.phoneshop.web.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import java.io.IOException;

public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationFailureHandler.class);
    private static final String USER_NAME_ATTRIBUTE = "username";

    public CustomAuthenticationFailureHandler(String failureUrl) {
        super(failureUrl);
    }

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws ServletException, IOException {
        String userName = request.getParameter(USER_NAME_ATTRIBUTE);

        logger.error("Authentication failed for username = {}", userName);

        super.onAuthenticationFailure(request, response, exception);
    }

}
