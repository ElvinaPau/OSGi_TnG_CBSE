package com.tng.user.impl;

import com.tng.User;
import com.tng.UserService;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.osgi.service.component.annotations.Component;

@Component(service = UserService.class)
public class UserServiceImpl implements UserService {

    private final Map<String, User> users = new ConcurrentHashMap<>();

    @Override
    public User findOrCreateUser(String phoneNumber, String username) {
        // Check if user already exists by phone number
        User existing = users.get(phoneNumber);
        if (existing != null) {
            if (!existing.getUsername().equals(username)) {
                existing.setUsername(username);
            }
            return existing;
        }

        // Create new user
        User newUser = new User(username, phoneNumber);
        users.put(phoneNumber, newUser);
        return newUser;
    }

    @Override
    public User getUser(String phoneNumber) {
        return users.get(phoneNumber);
    }
}
