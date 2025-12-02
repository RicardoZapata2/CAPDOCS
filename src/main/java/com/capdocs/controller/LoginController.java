package com.capdocs.controller;

import com.capdocs.dao.UserDAO;
import com.capdocs.model.User;
import com.capdocs.util.AlertHelper;
import com.capdocs.util.Session;
import com.capdocs.view.LoginView;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

public class LoginController {

    private final LoginView view;
    private final UserDAO userDAO;
    private final Stage stage;
    private final Runnable onLoginSuccess;

    public LoginController(Stage stage, Runnable onLoginSuccess) {
        this.stage = stage;
        this.onLoginSuccess = onLoginSuccess;
        this.view = new LoginView();
        this.userDAO = new UserDAO();

        initController();
    }

    private void initController() {
        view.getLoginButton().setOnAction(e -> handleLogin());
        view.getPasswordField().setOnAction(e -> handleLogin()); // Allow Enter key
    }

    private void handleLogin() {
        String username = view.getUsernameField().getText();
        String password = view.getPasswordField().getText();

        if (username.isEmpty() || password.isEmpty()) {
            AlertHelper.showError("Error", "Por favor ingrese usuario y contraseña.");
            return;
        }

        Optional<User> userOpt = userDAO.findByUsername(username);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            boolean passwordMatch = false;

            try {
                if (BCrypt.checkpw(password, user.getPasswordHash())) {
                    passwordMatch = true;
                }
            } catch (IllegalArgumentException e) {
                // Fallback for legacy/plain text passwords (should be removed in production)
                if (password.equals(user.getPasswordHash())) {
                    passwordMatch = true;
                }
            }

            if (passwordMatch) {
                Session.setCurrentUser(user);
                if (onLoginSuccess != null) {
                    onLoginSuccess.run();
                }
            } else {
                AlertHelper.showError("Error", "Contraseña incorrecta.");
            }
        } else {
            AlertHelper.showError("Error", "Usuario no encontrado.");
        }
    }

    public LoginView getView() {
        return view;
    }
}
