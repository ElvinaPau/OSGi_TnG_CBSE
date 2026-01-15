package com.tng;

public interface UserService {
    User findOrCreateUser(String phoneNumber, String username);
    User getUser(String phoneNumber);
}
