package com.tng;

public interface UserService {
    User findOrCreateUser(String username);
    User getUser(String username);
}
