package com.tng.insurance.impl;

import java.lang.reflect.Field;

public class TestUtil {

    public static void initMocks(Object testInstance) {
        // no-op for this project
    }

    public static void setup() {
        // no-op for this project
    }

    public static void inject(Object target, String fieldName, Object dependency) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, dependency);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
