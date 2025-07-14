package com.example.demo.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @GetMapping("/login-options")
    public Map<String, Object> getLoginOptions() {
        Map<String, Object> options = new HashMap<>();
        
        // Google OAuth2 login URL
//        options.put("googleLoginUrl", "/oauth2/authorization/google");
        
        // Keycloak OAuth2 login URL
        options.put("keycloakLoginUrl", "/oauth2/authorization/keycloak");
        
        // Keycloak direct URLs for frontend integration
        options.put("keycloakAuthUrl", "http://localhost:8081/realms/secure-hello-realm/protocol/openid-connect/auth");
        options.put("keycloakTokenUrl", "http://localhost:8081/realms/secure-hello-realm/protocol/openid-connect/token");
        options.put("keycloakUserInfoUrl", "http://localhost:8081/realms/secure-hello-realm/protocol/openid-connect/userinfo");
        
        // Client configuration for frontend
        options.put("keycloakClientId", "secure-hello-client");
        options.put("keycloakRealm", "secure-hello-realm");
        options.put("keycloakServerUrl", "http://localhost:8081");
        
        return options;
    }
    
    @GetMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Invalidate the session (optional, depends on your security config)
        if (request.getSession(false) != null) {
            request.getSession(false).invalidate();
        }
        
        // Redirect to Angular logout page
        response.sendRedirect("http://localhost:4200/logout");
    }
}
