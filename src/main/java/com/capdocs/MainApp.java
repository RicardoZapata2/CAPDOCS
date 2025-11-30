package com.capdocs;

import com.capdocs.controller.LoginController;
import com.capdocs.controller.MainController;
import com.capdocs.util.DatabaseConnection;
import com.capdocs.util.Session;
import com.capdocs.view.MainLayout;
import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        // Initialize Database
        DatabaseConnection.initializeDatabase();

        showLogin();
    }

    public void showLogin() {
        LoginController loginController = new LoginController(primaryStage, this::showMainApp);
        primaryStage.setScene(loginController.getView().getScene());
        primaryStage.setTitle("CapDocs - Login");
        primaryStage.show();
    }

    public void showMainApp() {
        MainLayout mainLayout = new MainLayout();
        MainController mainController = new MainController(mainLayout, this::showLogin);

        primaryStage.setScene(mainLayout.getScene());
        if (Session.getCurrentUser() != null) {
            primaryStage.setTitle("CapDocs - " + Session.getCurrentUser().getUsername());
        } else {
            primaryStage.setTitle("CapDocs");
        }
        primaryStage.centerOnScreen();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
