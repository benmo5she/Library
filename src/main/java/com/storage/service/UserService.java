package com.storage.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.naming.AuthenticationException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.storage.exception.UserNotFoundException;
import com.storage.model.User;
import com.storage.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class UserService {

	private final UserRepository userRepository;
	
    @Value("${library.security.enabled:false}")
    private boolean securityEnabled;
    
    @Value("${library.security.default.user:admin}")
    private String defaultUserName;
    
    private User defaultUser = null;
    
	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	/**
	 * Authenticate a user using HTTP basic access authentication sent through the headers.
	 * Currently only checks for the user existence in the system.
	 * @param authString Basic authentication String to analyze
	 * @return Authenticated user entity
	 * @throws AuthenticationException If user was not authenticated successfully.
	 */
	public User authenticateBasic(HttpServletRequest request) throws AuthenticationException {
		String authHeader = request.getHeader("Authorization");
		return authenticateBasic(authHeader);
	}

	/**
	 * Authenticate a user using HTTP basic access authentication sent through the headers.
	 * Currently only checks for the user existence in the system.
	 * @param authString Basic authentication String to analyze
	 * @return Authenticated user entity
	 * @throws AuthenticationException If user was not authenticated successfully.
	 */
	//This method can be expanded to use spring security, 
	//and check for password and use roles for various endpoints offering increased security.
	public User authenticateBasic(String authString) throws AuthenticationException {
		if(securityEnabled) {
			if (authString != null && authString.startsWith("Basic ")) {
				String base64Credentials = authString.substring(6);
				byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
				String credentials = new String(credDecoded, StandardCharsets.UTF_8);

				// credentials = username:password
				final String[] values = credentials.split(":", 2);
				String username = values[0];
				// String password = values[1];

				return userRepository.findByUsername(username).orElseThrow(
						() -> new AuthenticationException("User: " + username + " is not autherized for this action"));
			} else {
				throw new AuthenticationException("User missing from authentication");
			}
		}
		return null;
	}

	public User findUserByUsername(String username) {
		return userRepository.findByUsername(username).orElseThrow(
					() -> new UserNotFoundException(username));
	}
	
	/**
	 * Get the initial user for the system
	 * @return Initial user entity if found
	 */
	public User getDefaultUser() {
		if(defaultUser == null) {			
			defaultUser = findUserByUsername(defaultUserName);
		}
		return defaultUser;
	}

}