package com.capdocs.view;

import com.capdocs.dao.UserDAO;
import com.capdocs.model.User;
import com.capdocs.util.AlertHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

public class UsersView extends VBox {

    private final UserDAO userDAO;
    private final TableView<User> table;
    private final ObservableList<User> users;

    public UsersView() {
        this.userDAO = new UserDAO();
        this.users = FXCollections.observableArrayList();
        this.table = new TableView<>();

        setSpacing(20);
        setPadding(new Insets(30));
        setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Gestión de Usuarios");
        title.setStyle("-fx-font-size: 28px; -fx-text-fill: white; -fx-font-weight: bold;");

        // Table Columns
        TableColumn<User, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(50);

        TableColumn<User, String> usernameCol = new TableColumn<>("Usuario");
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        usernameCol.setPrefWidth(200);

        TableColumn<User, String> roleCol = new TableColumn<>("Rol");
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));
        roleCol.setPrefWidth(150);

        table.getColumns().addAll(idCol, usernameCol, roleCol);
        table.setItems(users);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setStyle("-fx-background-color: transparent;");

        // Buttons
        Button addButton = new Button("Nuevo Usuario");
        addButton.getStyleClass().add("button-primary");
        addButton.setOnAction(e -> showUserDialog(null));

        Button editButton = new Button("Editar");
        editButton.getStyleClass().add("button-primary");
        editButton.setOnAction(e -> {
            User selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                showUserDialog(selected);
            } else {
                AlertHelper.showError("Error", "Seleccione un usuario para editar.");
            }
        });

        Button deleteButton = new Button("Eliminar");
        deleteButton.getStyleClass().add("button-primary"); // Should be danger style in real app
        deleteButton.setStyle("-fx-background-color: #e74c3c;");
        deleteButton.setOnAction(e -> deleteUser());

        HBox actions = new HBox(15, addButton, editButton, deleteButton);
        actions.setAlignment(Pos.CENTER);

        getChildren().addAll(title, table, actions);

        loadUsers();
    }

    private void loadUsers() {
        users.setAll(userDAO.findAll());
    }

    private void deleteUser() {
        User selected = table.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (selected.getUsername().equals("admin")) {
                AlertHelper.showError("Error", "No se puede eliminar el administrador principal.");
                return;
            }
            userDAO.delete(selected.getId());
            loadUsers();
        } else {
            AlertHelper.showError("Error", "Seleccione un usuario para eliminar.");
        }
    }

    private void showUserDialog(User user) {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle(user == null ? "Nuevo Usuario" : "Editar Usuario");
        dialog.setHeaderText(null);

        ButtonType saveButtonType = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        TextField usernameField = new TextField();
        usernameField.setPromptText("Nombre de usuario");
        if (user != null)
            usernameField.setText(user.getUsername());

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Contraseña");

        ComboBox<User.Role> roleComboBox = new ComboBox<>();
        roleComboBox.getItems().setAll(User.Role.values());
        roleComboBox.setValue(user != null ? user.getRole() : User.Role.OPERATOR);

        content.getChildren().addAll(new Label("Usuario:"), usernameField, new Label("Contraseña:"), passwordField,
                new Label("Rol:"), roleComboBox);
        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                String username = usernameField.getText();
                String password = passwordField.getText();
                User.Role role = roleComboBox.getValue();

                if (username.isEmpty()) {
                    return null;
                }

                if (user == null) {
                    // Create
                    if (password.isEmpty())
                        return null;
                    String hash = BCrypt.hashpw(password, BCrypt.gensalt());
                    return new User(0, username, hash, role);
                } else {
                    // Update
                    user.setUsername(username);
                    user.setRole(role);
                    // Only update password if provided
                    if (!password.isEmpty()) {
                        user.setPasswordHash(BCrypt.hashpw(password, BCrypt.gensalt()));
                    }
                    return user;
                }
            }
            return null;
        });

        Optional<User> result = dialog.showAndWait();
        result.ifPresent(u -> {
            if (user == null) {
                userDAO.create(u);
            } else {
                userDAO.update(u);
            }
            loadUsers();
        });
    }
}
