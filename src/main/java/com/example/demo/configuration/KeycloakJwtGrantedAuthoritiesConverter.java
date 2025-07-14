package com.example.demo.configuration;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class KeycloakJwtGrantedAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        
        // Extract realm roles
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        if (realmAccess != null && realmAccess.containsKey("roles")) {
            @SuppressWarnings("unchecked")
            List<String> realmRoles = (List<String>) realmAccess.get("roles");
            authorities.addAll(realmRoles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                .collect(Collectors.toList()));
        }
        
        // Extract resource access roles
        Map<String, Object> resourceAccess = jwt.getClaimAsMap("resource_access");
        if (resourceAccess != null) {
            resourceAccess.forEach((clientId, clientAccess) -> {
                if (clientAccess instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> clientAccessMap = (Map<String, Object>) clientAccess;
                    if (clientAccessMap.containsKey("roles")) {
                        @SuppressWarnings("unchecked")
                        List<String> clientRoles = (List<String>) clientAccessMap.get("roles");
                        authorities.addAll(clientRoles.stream()
                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                            .collect(Collectors.toList()));
                    }
                }
            });
        }
        
        return authorities;
    }
}
