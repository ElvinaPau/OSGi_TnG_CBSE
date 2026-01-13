package com.tng.user.impl;

import com.tng.UserService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

// OSGi Bundle Activator for User Component
// This registers the UserService when the bundle starts

public class UserActivator implements BundleActivator {

    private ServiceRegistration<UserService> serviceRegistration;

    @Override
    public void start(BundleContext context) throws Exception {
        System.out.println("=== User Component Bundle STARTED ===");

        // Create and register the UserService
        UserService userService = new UserServiceImpl();
        serviceRegistration = context.registerService(
                UserService.class,
                userService,
                null);

        System.out.println("UserService registered successfully!");
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        System.out.println("=== User Component Bundle STOPPING ===");

        // Unregister the service
        if (serviceRegistration != null) {
            serviceRegistration.unregister();
            System.out.println("UserService unregistered!");
        }
    }
}