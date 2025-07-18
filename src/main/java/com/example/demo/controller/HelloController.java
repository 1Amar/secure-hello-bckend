package com.example.demo.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.bean.AdminDashboard;
import com.example.demo.bean.HelloResponse;
import com.example.demo.bean.UserInfo;
import com.example.demo.bean.UserSummary;
import com.example.demo.service.KeycloakAdminService;

@RestController
@RequestMapping("/api")
public class HelloController {
	
	private final KeycloakAdminService keycloakAdminService;

    public HelloController(KeycloakAdminService keycloakAdminService) {
        this.keycloakAdminService = keycloakAdminService;
    }

    @GetMapping("/hello")
    public HelloResponse hello() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = "Anonymous";
        String provider = "Unknown";

        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();
            username = jwt.getClaimAsString("preferred_username");
            if (username == null) {
                username = jwt.getClaimAsString("email");
            }
            provider = "Keycloak";
        } else if (authentication instanceof OAuth2AuthenticationToken oauth2Token) {
            OAuth2User oauth2User = oauth2Token.getPrincipal();
            username = oauth2User.getAttribute("name");
            if (username == null) {
                username = oauth2User.getAttribute("email");
            }
            provider = oauth2Token.getAuthorizedClientRegistrationId();
        }

        return new HelloResponse("Hello, " + username + "! (via " + provider + ")", System.currentTimeMillis());
    }
    
    @GetMapping("/user-info")
    public UserInfo getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();
            List<String> roles = authentication.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .collect(Collectors.toList());
            
            return new UserInfo(
                jwt.getClaimAsString("preferred_username"),
                jwt.getClaimAsString("email"),
                jwt.getClaimAsString("name"),
                null,
                "Keycloak",
                roles
            );
        } else if (authentication instanceof OAuth2AuthenticationToken oauth2Token) {
            OAuth2User oauth2User = oauth2Token.getPrincipal();
            String provider = oauth2Token.getAuthorizedClientRegistrationId();
            List<String> roles = authentication.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .collect(Collectors.toList());
            
            return new UserInfo(
                oauth2User.getAttribute("email"),
                oauth2User.getAttribute("email"),
                oauth2User.getAttribute("name"),
                oauth2User.getAttribute("picture"),
                capitalize(provider),
                roles
            );
        }

        return new UserInfo("Anonymous", null, null, null, "Unknown", List.of());
    }

    @GetMapping("/public/hello")
    public HelloResponse getPublicHello(@RequestParam(defaultValue = "default") String param) {
        return new HelloResponse("Hello Public world", System.currentTimeMillis());
    }

    // Admin-only endpoints
    @GetMapping("/admin/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public AdminDashboard getAdminDashboard() {
        return new AdminDashboard(
            "Admin Dashboard",
            "Welcome to the admin panel",
            System.currentTimeMillis(),
            getAllUsers()
        );
    }
    
    @GetMapping("admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserSummary> getAllUsers() {
        return keycloakAdminService.getAllUsers();
    }

//    @GetMapping("/admin/users")
//    @PreAuthorize("hasRole('ADMIN')")
//    public List<UserSummary> getAllUsers() {
//        // This would typically come from a database
//        return List.of(
//            new UserSummary("admin", "admin@example.com", "Administrator", List.of("ROLE_ADMIN", "ROLE_USER")),
//            new UserSummary("user1", "user1@example.com", "Regular User", List.of("ROLE_USER")),
//            new UserSummary("user2", "user2@example.com", "Regular User", List.of("ROLE_USER"))
//        );
//    }
//
//    @PostMapping("/admin/users")
//    @PreAuthorize("hasRole('ADMIN')")
//    public AdminResponse createUser(@RequestBody CreateUserRequest request) {
//        // This would typically create a user in the database
//        return new AdminResponse(
//            "User '" + request.getUsername() + "' created successfully",
//            System.currentTimeMillis()
//        );
//    }
//
//    @DeleteMapping("/admin/users/{username}")
//    @PreAuthorize("hasRole('ADMIN')")
//    public AdminResponse deleteUser(@PathVariable String username) {
//        // This would typically delete a user from the database
//        return new AdminResponse(
//            "User '" + username + "' deleted successfully",
//            System.currentTimeMillis()
//        );
//    }

    // Helper method
    private String capitalize(String str) {
        return (str == null || str.isEmpty()) ? str : str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

}