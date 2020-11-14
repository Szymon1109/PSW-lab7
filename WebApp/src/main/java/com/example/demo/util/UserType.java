package com.example.demo.util;

public enum UserType {

    ADMIN("ADMINISTRATOR"),
    TRAINER("PROWADZĄCY"),
    STUDENT("UCZESTNIK");

    public final String roleName;

    UserType(String roleName) {
        this.roleName = roleName;
    }

}
