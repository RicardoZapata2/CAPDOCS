package com.capdocs.controller;

import com.capdocs.util.Session;
import com.capdocs.view.MainLayout;

public class MainController {

    private final MainLayout layout;
    private final Runnable logoutHandler;

    public MainController(MainLayout layout, Runnable logoutHandler) {
        this.layout = layout;
        this.logoutHandler = logoutHandler;

        // Set the logout action in the layout's configuration menu
        this.layout.setLogoutAction(this::logout);
    }

    private void logout() {
        Session.setCurrentUser(null);
        if (logoutHandler != null) {
            logoutHandler.run();
        }
    }

    public MainLayout getLayout() {
        return layout;
    }
}
