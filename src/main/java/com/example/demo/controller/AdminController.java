package com.example.demo.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.bean.AdminResponse;
import com.example.demo.bean.CreateUserRequest;
import com.example.demo.bean.UserSummary;
import com.example.demo.service.KeycloakAdminService;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final KeycloakAdminService keycloakAdminService;

    public AdminController(KeycloakAdminService keycloakAdminService) {
        this.keycloakAdminService = keycloakAdminService;
    }
    
//    @GetMapping("/users")
//    @PreAuthorize("hasRole('ADMIN')")
//    public List<UserSummary> getAllUsers() {
//        return keycloakAdminService.getAllUsers();
//    }

    @PostMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public AdminResponse createUser(@RequestBody CreateUserRequest request) {
        keycloakAdminService.createUser(request);
        return new AdminResponse("User created successfully", System.currentTimeMillis());
    }

    @DeleteMapping("/users/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public AdminResponse deleteUser(@PathVariable String username) {
        keycloakAdminService.deleteUser(username);
        return new AdminResponse("User deleted successfully", System.currentTimeMillis());
    }
}

