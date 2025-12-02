package com.capdocs.view;

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
        view = new VBox(20);
        view.setAlignment(Pos.CENTER);
        view.getStyleClass().add("login-container");
        view.setMaxWidth(400);
        view.setMaxHeight(500);

        // Logo / Title Section
        VBox header = new VBox(10);
        header.setAlignment(Pos.CENTER);

        try {
            ImageView logo = new ImageView(new Image(getClass().getResourceAsStream("/images/logo.png")));
            logo.setFitWidth(100);
            logo.setPreserveRatio(true);
            header.getChildren().add(logo);
        } catch (Exception e) {
            // Fallback if logo missing
        }

        Label titleLabel = new Label("CAPDOCS");
        titleLabel.getStyleClass().add("login-title");

        Label subtitleLabel = new Label("SISTEMA INTEGRAL DE REGISTRO");
        subtitleLabel.getStyleClass().add("login-subtitle");

        header.getChildren().addAll(titleLabel, subtitleLabel);

        // Form Section
        VBox form = new VBox(15);
        form.setAlignment(Pos.CENTER);

        usernameField = new TextField();
        usernameField.setPromptText("Usuario");
        usernameField.getStyleClass().add("text-field");

        passwordField = new PasswordField();
        passwordField.setPromptText("Contraseña");
        passwordField.getStyleClass().add("text-field");

        loginButton = new Button("INGRESAR");
        loginButton.getStyleClass().add("button-primary");
        loginButton.setMaxWidth(Double.MAX_VALUE);

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
