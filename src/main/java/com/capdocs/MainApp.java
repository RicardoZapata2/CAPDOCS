package com.capdocs;

import com.capdocs.controller.LoginController;
import com.capdocs.controller.MainController;
import com.capdocs.util.DatabaseConnection;
import com.capdocs.util.Session;
import com.capdocs.util.TestDataGenerator;
import com.capdocs.view.MainLayout;
import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class MainApp extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        try {
            // 1. Initialize Database & Safety Check
            try {
                DatabaseConnection.initializeDatabase();
            } catch (Exception e) {
                showErrorDialog("Error de Base de Datos", "No se pudo conectar a la base de datos.\n" + e.getMessage());
                return; // Stop execution
            }

            // 2. Data Injection
            try {
                // TestDataGenerator.generate();
            } catch (Exception e) {
                System.err.println("Warning: Test Data Generation failed: " + e.getMessage());
                // Continue anyway, not critical
            }

            // 3. Launch Application
            showLogin();

        } catch (Exception e) {
            // Global Exception Handling
            e.printStackTrace();
            showErrorDialog("Error Crítico",
                    "Ocurrió un error inesperado al iniciar la aplicación.\n" + e.getMessage());
        }
    }

    public void showLogin() {
        try {
            LoginController loginController = new LoginController(primaryStage, this::showMainApp);
            primaryStage.setScene(loginController.getView().getScene());
            primaryStage.setTitle("CapDocs - Iniciar Sesión");
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showErrorDialog("Error de UI", "No se pudo cargar la vista de Login.");
        }
    }

    public void showMainApp() {
        try {
            MainLayout mainLayout = new MainLayout();
            // Connect Controller (assuming MainController exists and handles logic if
            // needed)
            MainController mainController = new MainController(mainLayout, this::showLogin);

            primaryStage.setScene(mainLayout.getScene());
            if (Session.getCurrentUser() != null) {
                primaryStage.setTitle("CapDocs - " + Session.getCurrentUser().getUsername());
            } else {
                primaryStage.setTitle("CapDocs - Sistema de Gestión");
            }
            primaryStage.centerOnScreen();
            primaryStage.setMaximized(true); // Professional touch
        } catch (Exception e) {
            e.printStackTrace();
            showErrorDialog("Error de UI", "No se pudo cargar la vista principal.");
        }
    }

    private void showErrorDialog(String title, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
