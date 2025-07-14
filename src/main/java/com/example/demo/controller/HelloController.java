package com.example.demo.controller;

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

import com.example.demo.bean.HelloResponse;
import com.example.demo.bean.UserInfo;

@RestController
@RequestMapping("/api")
public class HelloController {

//	@GetMapping("/hello")
//	public HelloResponse hello() {
//		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//		String username = "Anonymous";
//		String provider = "Unknown";
//
//		if (authentication instanceof JwtAuthenticationToken) {
//			// JWT token from Keycloak
//			Jwt jwt = ((JwtAuthenticationToken) authentication).getToken();
//			username = jwt.getClaimAsString("preferred_username");
//			if (username == null) {
//				username = jwt.getClaimAsString("email");
//			}
//			provider = "Keycloak";
//		} else if (authentication != null && authentication.getPrincipal() instanceof OAuth2User) {
//			// OAuth2 user from Google
//			OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
//			username = oauth2User.getAttribute("name");
//			if (username == null) {
//				username = oauth2User.getAttribute("email");
//			}
//			provider = "Google";
//		}
//
//		return new HelloResponse("Hello, " + username + "! (via " + provider + ")", System.currentTimeMillis());
//	}
	
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
	        provider = oauth2Token.getAuthorizedClientRegistrationId(); // e.g., "google" or "keycloak"
	    }

	    return new HelloResponse("Hello, " + username + "! (via " + provider + ")", System.currentTimeMillis());
	}


//	@GetMapping("/user-info")
//	public UserInfo getUserInfo() {
//		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//		if (authentication instanceof JwtAuthenticationToken) {
//			// JWT token from Keycloak
//			Jwt jwt = ((JwtAuthenticationToken) authentication).getToken();
//			return new UserInfo(jwt.getClaimAsString("preferred_username"), jwt.getClaimAsString("email"),
//					jwt.getClaimAsString("name"), null, // Keycloak doesn't provide picture in standard claims
//					"Keycloak");
//		} else if (authentication != null && authentication.getPrincipal() instanceof OAuth2User) {
//			// OAuth2 user from Google
//			OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
//			return new UserInfo(oauth2User.getAttribute("email"), oauth2User.getAttribute("email"),
//					oauth2User.getAttribute("name"), oauth2User.getAttribute("picture"), "Google");
//		}
//
//		return new UserInfo("Anonymous", null, null, null, "Unknown");
//	}
	
	@GetMapping("/user-info")
	public UserInfo getUserInfo() {
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

	    if (authentication instanceof JwtAuthenticationToken jwtAuth) {
	        // JWT token (from Keycloak)
	        Jwt jwt = jwtAuth.getToken();
	        return new UserInfo(
	            jwt.getClaimAsString("preferred_username"),
	            jwt.getClaimAsString("email"),
	            jwt.getClaimAsString("name"),
	            null, // Keycloak usually doesn't include picture URL
	            "Keycloak"
	        );
	    } else if (authentication instanceof OAuth2AuthenticationToken oauth2Token) {
	        // OAuth2 login (Google or Keycloak)
	        OAuth2User oauth2User = oauth2Token.getPrincipal();
	        String provider = oauth2Token.getAuthorizedClientRegistrationId(); // "google" or "keycloak"
	        return new UserInfo(
	            oauth2User.getAttribute("email"),
	            oauth2User.getAttribute("email"),
	            oauth2User.getAttribute("name"),
	            oauth2User.getAttribute("picture"), // Google provides it; Keycloak may not
	            capitalize(provider) // Optional: to display "Google" instead of "google"
	        );
	    }

	    return new UserInfo("Anonymous", null, null, null, "Unknown");
	}

	// Helper method
	private String capitalize(String str) {
	    return (str == null || str.isEmpty()) ? str : str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
	}


	@GetMapping("/public/hello")
	public HelloResponse getMethodName(@RequestParam(defaultValue = "default") String param) {
//		public HelloResponse getMethodName(@RequestParam(required = false) String param) {
		return new HelloResponse("Hello Public world", System.currentTimeMillis());
	}

}
