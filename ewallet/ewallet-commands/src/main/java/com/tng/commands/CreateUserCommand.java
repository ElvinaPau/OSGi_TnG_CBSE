package com.tng.commands;

import com.tng.User;
import com.tng.UserService;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;

@Command(scope = "ewallet", name = "create-user", description = "Create a new user by phone number")
@Service
public class CreateUserCommand implements Action {

    @Reference
    private UserService userService;

    @Argument(index = 0, name = "phoneNumber", description = "User's phone number", required = true)
    private String phoneNumber;

    @Argument(index = 1, name = "username", description = "Username", required = true)
    private String username;

    @Override
    public Object execute() throws Exception {
        if (userService == null) {
            System.err.println("ERROR: UserService not available!");
            return null;
        }

        User existingUser = userService.getUser(phoneNumber);
        if (existingUser != null) {
            System.out.println("User with phone number '" + phoneNumber + "' already exists!");
            System.out.println("  Username: " + existingUser.getUsername());
            System.out.println("  User ID: " + existingUser.getId());
            return null;
        }

        User user = userService.findOrCreateUser(phoneNumber, username);

        System.out.println("User created successfully!");
        System.out.println("  Phone Number: " + user.getPhoneNumber());
        System.out.println("  Username: " + user.getUsername());
        System.out.println("  User ID: " + user.getId());
        System.out.println("Create wallet using: ewallet:create-wallet " + phoneNumber + " <username> <initial-balance>");

        return null;
    }
}
