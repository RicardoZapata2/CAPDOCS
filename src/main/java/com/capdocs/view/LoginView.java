package com.capdocs.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class LoginView {

    private final VBox view;
    private final TextField usernameField;
    private final PasswordField passwordField;
    private final Button loginButton;

    public LoginView() {
        view = new VBox(25);
        view.setAlignment(Pos.CENTER);
        view.getStyleClass().add("login-container");
        view.setMaxWidth(400);
        view.setMaxHeight(500);
        view.setStyle(
                "-fx-background-color: #1a1a1a; -fx-background-radius: 20; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 20, 0, 0, 10);");

        // Logo / Title Section
        VBox header = new VBox(10);
        header.setAlignment(Pos.CENTER);

        try {
            ImageView logo = new ImageView(new Image(getClass().getResourceAsStream("/images/logo.png")));
            logo.setFitWidth(120);
            logo.setPreserveRatio(true);
            header.getChildren().add(logo);
        } catch (Exception e) {
            // Fallback if logo missing
        }

        Label titleLabel = new Label("CAPDOCS");
        titleLabel.setStyle(
                "-fx-font-size: 36px; -fx-font-weight: 900; -fx-text-fill: white; -fx-font-family: 'Segoe UI', sans-serif; -fx-effect: dropshadow(gaussian, #3498db, 10, 0.5, 0, 0);");

        Label subtitleLabel = new Label("SISTEMA INTEGRAL DE PRODUCCIÓN");
        subtitleLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #aaaaaa; -fx-letter-spacing: 2px;");

        header.getChildren().addAll(titleLabel, subtitleLabel);

        // Form Section
        VBox form = new VBox(15);
        form.setAlignment(Pos.CENTER);
        form.setPadding(new Insets(20));

        usernameField = new TextField();
        usernameField.setPromptText("Usuario");
        usernameField.getStyleClass().add("text-field");
        usernameField.setStyle(
                "-fx-background-color: #2c2c2c; -fx-text-fill: white; -fx-prompt-text-fill: #666; -fx-background-radius: 30; -fx-padding: 12 20;");

        passwordField = new PasswordField();
        passwordField.setPromptText("Contraseña");
        passwordField.getStyleClass().add("text-field");
        passwordField.setStyle(
                "-fx-background-color: #2c2c2c; -fx-text-fill: white; -fx-prompt-text-fill: #666; -fx-background-radius: 30; -fx-padding: 12 20;");

        loginButton = new Button("INGRESAR");
        loginButton.getStyleClass().add("button-primary");
        loginButton.setStyle(
                "-fx-background-color: linear-gradient(to right, #3498db, #2980b9); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 30; -fx-padding: 12 40; -fx-cursor: hand;");
        loginButton.setMaxWidth(Double.MAX_VALUE);

        // Hover effect for button
        loginButton.setOnMouseEntered(e -> loginButton.setStyle(
                "-fx-background-color: linear-gradient(to right, #2980b9, #3498db); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 30; -fx-padding: 12 40; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(52, 152, 219, 0.4), 10, 0, 0, 5);"));
        loginButton.setOnMouseExited(e -> loginButton.setStyle(
                "-fx-background-color: linear-gradient(to right, #3498db, #2980b9); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 30; -fx-padding: 12 40; -fx-cursor: hand;"));

        form.getChildren().addAll(usernameField, passwordField, loginButton);

        view.getChildren().addAll(header, form);
    }

    public Scene getScene() {
        Scene scene = new Scene(new javafx.scene.layout.StackPane(view), 1280, 800);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        return scene;
    }

    public VBox getView() {
        return view;
    }

    public TextField getUsernameField() {
        return usernameField;
    }

    public PasswordField getPasswordField() {
        return passwordField;
    }

    public Button getLoginButton() {
        return loginButton;
    }
}
