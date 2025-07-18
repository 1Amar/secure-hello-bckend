package com.example.demo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.demo.bean.CreateUserRequest;
import com.example.demo.bean.UserSummary;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response; // ✅ correct for Spring Boot 3


@Service
public class KeycloakAdminService {
    private final RealmResource realmResource;

    public KeycloakAdminService(
            @Value("${keycloak.server-url}") String serverUrl,
            @Value("${keycloak.realm}") String realm,
            @Value("${keycloak.admin.username}") String username,
            @Value("${keycloak.admin.password}") String password
    ) {
        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm("master")
                .clientId("admin-cli")
                .username(username)
                .password(password)
                .build();

        this.realmResource = keycloak.realm(realm);
    }

    public List<UserSummary> getAllUsers() {
        return realmResource.users().list().stream()
                .map(user -> new UserSummary(
                        user.getUsername(),
                        user.getEmail(),
                        ((user.getFirstName() != null ? user.getFirstName() : "") + 
                         " " + 
                         (user.getLastName() != null ? user.getLastName() : "")).trim(),
                        getUserRoles(user.getId())
                ))
                .collect(Collectors.toList());
    }

    public void createUser(CreateUserRequest request) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getName());
        user.setEnabled(true);

        // Set password
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(request.getPassword());
        credential.setTemporary(false);
        user.setCredentials(List.of(credential));

        // Create user
        Response response = realmResource.users().create(user);
        if (response.getStatus() != 201) {
            String body = response.readEntity(String.class);
            throw new RuntimeException("User creation failed: " + body);
        }

        // Get userId safely
        String userId = CreatedResponseUtil.getCreatedId(response);

        // Assign roles
        List<RoleRepresentation> roles = request.getRoles().stream()
        	    .map(role -> {
        	        try {
        	            return realmResource.roles().get(role).toRepresentation(); // Not "ROLE_" prefixed
        	        } catch (NotFoundException e) {
        	            throw new RuntimeException("Role not found in Keycloak: " + role);
        	        }
        	    })
        	    .collect(Collectors.toList());


        realmResource.users().get(userId).roles().realmLevel().add(roles);
    }


    public void deleteUser(String username) {
        UserRepresentation user = realmResource.users().search(username).stream().findFirst().orElse(null);
        if (user != null) {
            realmResource.users().get(user.getId()).remove();
        }
    }

    private List<String> getUserRoles(String userId) {
        return realmResource.users().get(userId).roles().realmLevel().listAll().stream()
                .map(RoleRepresentation::getName)
                .collect(Collectors.toList());
    }
}
