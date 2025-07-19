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

/**
 * A custom implementation of the {@link Converter} interface to extract roles from a Keycloak-issued JWT.
 *
 * <p>This converter reads both realm-level and client-level (resource access) roles from the JWT
 * and maps them to Spring Security's {@link GrantedAuthority} instances.
 *
 * <p>Useful when integrating Spring Security with Keycloak to enable role-based access control (RBAC).
 *
 * <p>Example roles extracted:
 * <ul>
 *     <li>Realm roles from "realm_access.roles"</li>
 *     <li>Client roles from "resource_access.{client}.roles"</li>
 * </ul>
 *
 * @author Amar Pattanshetti
 */


public class KeycloakJwtGrantedAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

	/**
	 * Converts the given {@link Jwt} into a collection of {@link GrantedAuthority}.
	 *
	 * <p>This method extracts both realm roles and client-specific roles from the JWT claims and
	 * converts them into Spring Security authorities. Each role is prefixed with "ROLE_" and converted to uppercase
	 * to align with standard Spring Security conventions.
	 *
	 * @param jwt the JWT containing the role claims issued by Keycloak
	 * @return a collection of {@link GrantedAuthority} derived from the JWT's realm and client roles
	 */

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
