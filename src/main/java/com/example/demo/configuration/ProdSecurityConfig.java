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
 * Production-grade Spring Security configuration tailored for stateless, token-based authentication 
 * in a microservices or SPA (Single Page Application) architecture.
 *
 * <p>This configuration is activated only under the {@code prod} Spring profile and is optimized 
 * for deployments where the frontend (e.g., Angular) communicates with the backend over REST APIs 
 * using JWT tokens.
 *
 * <p>Main features:
 * <ul>
 *     <li><strong>Stateless Security:</strong> Uses JWT for authentication; sessions are disabled.</li>
 *     <li><strong>CSRF Disabled:</strong> CSRF protection is not required since the app doesn't use cookies.</li>
 *     <li><strong>CORS:</strong> Configured to accept requests from trusted production frontend domains only.</li>
 *     <li><strong>HTTPS Enforcement:</strong> Enforced via HTTP Strict Transport Security (HSTS) headers, not via deprecated API.</li>
 *     <li><strong>OAuth2 Integration:</strong> Supports login via OAuth2 providers (e.g., Google, Keycloak).</li>
 *     <li><strong>JWT Resource Server:</strong> Validates bearer tokens on secured endpoints.</li>
 *     <li><strong>Role-Based Authorization:</strong> Secures endpoints based on roles extracted from JWT.</li>
 * </ul>
 *
 * <p><strong>Important:</strong> HTTPS redirection should be handled by the infrastructure (e.g., NGINX, AWS ALB),
 * and HSTS is used to enforce secure access on the client side. If using behind a proxy, ensure that
 * {@code server.forward-headers-strategy=native} is set in {@code application.properties}.
 *
 * @author Amar Pattanshetti
 */


@Configuration
@EnableWebSecurity
@Profile("prod")
public class ProdSecurityConfig {

	/**
	 * Configures the HTTP security filter chain for production.
	 *
	 * <p>Includes strict security settings such as:
	 * <ul>
	 *     <li>Stateless session policy for REST APIs</li>
	 *     <li>Role-based access control using JWT tokens</li>
	 *     <li>HSTS headers to enforce HTTPS on supported browsers</li>
	 *     <li>OAuth2 login support</li>
	 * </ul>
	 *
	 * @param http the {@link HttpSecurity} object provided by Spring Security
	 * @return a fully configured {@link SecurityFilterChain}
	 * @throws Exception in case of any configuration failure
	 */

	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	    http
	        // Enable CORS with custom configuration
	        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
	        
	        // Disable CSRF for stateless APIs (safe for Angular + JWT)
	        .csrf(csrf -> csrf.disable())
	        
	        // Stateless session management (no JSESSIONID)
	        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
	        
	        // Authorization rules
	        .authorizeHttpRequests(authz -> authz
	            .requestMatchers("/api/public/**", "/actuator/health").permitAll()
	            .requestMatchers("/api/admin/**").hasRole("ADMIN")
	            .anyRequest().authenticated()
	        )
	        
	        // Enforce HTTPS via HSTS headers (recommended over deprecated requiresChannel())
	        .headers(headers -> headers
	            .httpStrictTransportSecurity(hsts -> hsts
	                .includeSubDomains(true)
	                .maxAgeInSeconds(31536000)
	            )
	        )
	        
	        // OAuth2 login (for browser-based auth with Keycloak or Google)
	        .oauth2Login(oauth2 -> oauth2
	            .defaultSuccessUrl("/api/hello", true)
	            .failureUrl("/login?error=true")
	        )
	        
	        // OAuth2 resource server for token-based API auth (JWT)
	        .oauth2ResourceServer(oauth2 -> oauth2
	            .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
	        )
	        
	        // Logout behavior
	        .logout(logout -> logout
	            .logoutSuccessUrl("/api/public/hello")
	            .invalidateHttpSession(true)
	            .clearAuthentication(true)
	        );

	    return http.build();
	}


	/**
	 * Defines CORS policy to allow requests only from trusted frontend domains.
	 *
	 * <p>Restricts allowed origins, methods, and headers to enhance security. Ensures credentials 
	 * (like Authorization headers) are allowed to pass through.
	 *
	 * @return a configured {@link CorsConfigurationSource}
	 */

	
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("https://yourdomain.com")); // Restrict to production origin
        configuration.setAllowedMethods(Arrays.asList("GET", "POST"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Provides a custom {@link JwtAuthenticationConverter} to extract roles from Keycloak-issued JWTs.
     *
     * <p>Integrates with {@link KeycloakJwtGrantedAuthoritiesConverter} to map both realm and client roles
     * to Spring Security's {@link org.springframework.security.core.GrantedAuthority} format.
     *
     * @return a configured {@link JwtAuthenticationConverter}
     */

    
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new KeycloakJwtGrantedAuthoritiesConverter());
        return converter;
    }
}

