package com.tng.user.impl;

import com.tng.User;
import com.tng.UserService;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.osgi.service.component.annotations.Component;

@Component(service = UserService.class)
public class UserServiceImpl implements UserService {

    // In-memory store simulating repository
    private final Map<String, User> users = new ConcurrentHashMap<>();

    @Override
    public User findOrCreateUser(String username) {
        // If user exists, return it
        if (users.containsKey(username)) {
            return users.get(username);
        }
        // Else create new user
        User newUser = new User(username);
        users.put(username, newUser);
        return newUser;
    }

    @Override
    public User getUser(String username) {
        return users.get(username);
    }
}
