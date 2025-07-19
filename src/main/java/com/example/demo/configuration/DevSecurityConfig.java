package com.example.demo.configuration;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Security configuration class for defining authentication and authorization rules using Spring Security.
 *
 * <p>Enables web security and configures CORS, session management, JWT-based resource server,
 * OAuth2 login, and URL-based access controls for public and secured endpoints.
 *
 * <p>Integrates with Keycloak via a custom {@link KeycloakJwtGrantedAuthoritiesConverter} to handle role mapping.
 *
 * <p>Supports both browser-based OAuth2 login and token-based authentication for REST APIs.
 * 
 * <p>Typical use cases:
 * <ul>
 *     <li>Allow public access to endpoints like health checks and login</li>
 *     <li>Restrict sensitive endpoints based on user roles</li>
 *     <li>Enable secure frontend-backend communication via CORS</li>
 * </ul>
 *
 *  <p>This configuration is activated under the {@code dev} Spring profile and is designed 
 * to provide a flexible and less restrictive security setup to facilitate developer productivity.
 * 
 * <p>Main features:
 * <ul>
 *     <li><strong>CSRF Disabled:</strong> Disabled for convenience in development when using Angular or other SPA frameworks.</li>
 *     <li><strong>CORS:</strong> Configured to allow requests from common local development origins (e.g., localhost:4200, 3000).</li>
 *     <li><strong>Session Management:</strong> Typically stateless or may allow sessions as required for local debugging.</li>
 *     <li><strong>Authorization:</strong> Some endpoints (like public APIs and health checks) are open, while others require authentication.</li>
 *     <li><strong>OAuth2 Login and Resource Server:</strong> Supports OAuth2 login and JWT authentication as in production, enabling realistic end-to-end testing.</li>
 * </ul>
 * 
 * <p><strong>Note:</strong> This configuration is intentionally more permissive than production to avoid developer friction. 
 * It should not be used in production environments.
 * 
 * @author Amar Pattanshetti
 */


@Configuration
@EnableWebSecurity
@Profile("dev")
public class DevSecurityConfig {
	
	private final CorsProperties corsProperties;

    public DevSecurityConfig(CorsProperties corsProperties) {
        this.corsProperties = corsProperties;
    }

	/**
	 * Configures the main security filter chain for HTTP requests.
	 *
	 * <p>Includes settings for:
	 * <ul>
	 *     <li>CORS with a predefined configuration</li>
	 *     <li>Disabling CSRF (for stateless REST APIs)</li>
	 *     <li>Session management (uses sessions only when required)</li>
	 *     <li>Authorization rules for various endpoint patterns</li>
	 *     <li>OAuth2 login with default success/failure URLs</li>
	 *     <li>JWT resource server configuration for secured APIs</li>
	 *     <li>Logout behavior and redirection</li>
	 * </ul>
	 *
	 * @param http the {@link HttpSecurity} object to configure
	 * @return the configured {@link SecurityFilterChain}
	 * @throws Exception in case of configuration errors
	 */
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/login/**").permitAll()
                .requestMatchers("/oauth2/**").permitAll()
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/api/hello").authenticated()
                .requestMatchers("/api/user-info").authenticated()
                .requestMatchers("/api/admin/**").hasRole("ADMIN") // Admin only endpoints
                .anyRequest().authenticated()
            )
            // OAuth2 Login (for web-based login with Google/Keycloak)
            .oauth2Login(oauth2 -> oauth2
                .defaultSuccessUrl("/api/hello", true)
                .failureUrl("/login?error=true")
            )
            // OAuth2 Resource Server (for JWT tokens from frontend)
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .jwtAuthenticationConverter(jwtAuthenticationConverter())
                )
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/api/public/hello")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
            );
        
        return http.build();
    }
    
    /**
     * Defines CORS configuration to allow requests from specific frontend origins.
     *
     * <p>Allows common HTTP methods and all headers. Supports credentials for secure cookies.
     *
     * @return the configured {@link CorsConfigurationSource}
     */

//    @Bean
//    CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:4200", "http://localhost:3000"));
//        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//        configuration.setAllowedHeaders(Arrays.asList("*"));
//        configuration.setAllowCredentials(true);
//        
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }
    
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(corsProperties.getAllowedOrigins());
        configuration.setAllowedMethods(corsProperties.getAllowedMethods());
        configuration.setAllowedHeaders(corsProperties.getAllowedHeaders());
        configuration.setAllowCredentials(corsProperties.isAllowCredentials());

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    
    
    /**
     * Creates and configures a {@link JwtAuthenticationConverter} to convert JWT tokens into Spring Security
     * authorities using a custom Keycloak role converter.
     *
     * <p>This ensures that roles from Keycloak are mapped correctly into {@link org.springframework.security.core.GrantedAuthority}.
     *
     * @return the configured {@link JwtAuthenticationConverter}
     */

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter converter = 
            new org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new KeycloakJwtGrantedAuthoritiesConverter());
        return converter;
    }
}