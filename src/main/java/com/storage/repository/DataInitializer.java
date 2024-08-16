package com.storage.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.storage.model.User;

import jakarta.annotation.PostConstruct;

/**
 * This class will initialize various components in the application 
 * according to its needs.
 */
@Component
public class DataInitializer {

    private final UserRepository userRepo;
    
    @Value("${library.security.default.user:admin}")
    private String defaultUser;
    
    @Value("${library.security.default.enabled:false}")
    private boolean securityEnabled;
    
    public DataInitializer(UserRepository userRepo) {
    	this.userRepo = userRepo;
    }

    @PostConstruct
    public void init() {
    	//Initialize basic user to use the system
        User firstUser = new User();
        firstUser.setUsername(defaultUser);
        userRepo.save(firstUser);
    }
}