package com.capdocs.util;

import com.capdocs.model.User;

public class Session {
    private static User currentUser;

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static void clear() {
        currentUser = null;
    }

    public static boolean isAdmin() {
        return currentUser != null && currentUser.getRole() == User.Role.ADMIN;
    }
}
