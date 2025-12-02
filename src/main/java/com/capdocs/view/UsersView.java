package com.capdocs.view;

import com.capdocs.dao.UserDAO;
import com.capdocs.model.User;
import com.capdocs.util.AlertHelper;
import org.mindrot.jbcrypt.BCrypt;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Optional;

public class UsersView extends BorderPane {

    private final UserDAO userDAO;
    private final TableView<User> userTable;
    private final ObservableList<User> userList;

    public UsersView() {
        this.userDAO = new UserDAO();
        this.userList = FXCollections.observableArrayList();
        this.userTable = new TableView<>();

        initUI();
        loadData();
    }

    private void initUI() {
        setPadding(new Insets(20));
        setStyle("-fx-background-color: #1e1e1e;");

        // Header
        Label titleLabel = new Label("Gestión de Usuarios");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");

        Button addUserBtn = new Button("Nuevo Usuario");
        addUserBtn.getStyleClass().add("button-primary");
        addUserBtn.setOnAction(e -> showUserDialog(null));

        HBox header = new HBox(20, titleLabel, addUserBtn);
        header.setAlignment(Pos.CENTER_LEFT);
        setTop(header);

        // Table
        userTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        userTable.setStyle("-fx-background-color: transparent;");
        VBox.setMargin(userTable, new Insets(20, 0, 0, 0));

        TableColumn<User, String> userCol = new TableColumn<>("Usuario");
        userCol.setCellValueFactory(new PropertyValueFactory<>("username"));

        TableColumn<User, String> roleCol = new TableColumn<>("Rol");
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));

        TableColumn<User, String> actionsCol = new TableColumn<>("Acciones");
        actionsCol.setCellFactory(param -> new TableCell<>() {
            private final Button passBtn = new Button("Cambiar Clave");
            private final Button deleteBtn = new Button("Eliminar");

            {
                passBtn.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-size: 10px;");
                passBtn.setOnAction(event -> showPasswordDialog(getTableView().getItems().get(getIndex())));

                deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 10px;");
                deleteBtn.setOnAction(event -> deleteUser(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5, passBtn, deleteBtn);
                    buttons.setAlignment(Pos.CENTER);
                    setGraphic(buttons);
                }
            }
        });

        userTable.getColumns().addAll(userCol, roleCol, actionsCol);
        userTable.setItems(userList);

        setCenter(userTable);
    }

    private void loadData() {
        userList.setAll(userDAO.findAll());
    }

    private void showUserDialog(User user) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(user == null ? "Nuevo Usuario" : "Editar Usuario");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        TextField usernameField = new TextField(user != null ? user.getUsername() : "");
        usernameField.setPromptText("Nombre de usuario");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Contraseña");

        ComboBox<User.Role> roleCombo = new ComboBox<>();
        roleCombo.setItems(FXCollections.observableArrayList(User.Role.values()));
        roleCombo.setValue(user != null ? user.getRole() : User.Role.OPERATOR);

        Button saveBtn = new Button("Guardar");
        saveBtn.getStyleClass().add("button-primary");
        saveBtn.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            User.Role role = roleCombo.getValue();

            if (username.isEmpty() || (user == null && password.isEmpty())) {
                AlertHelper.showError("Error", "Complete todos los campos.");
                return;
            }

            if (user == null) {
                // Create
                String hash = BCrypt.hashpw(password, BCrypt.gensalt());
                User newUser = new User(0, username, hash, role);
                userDAO.create(newUser);
            } else {
                // Update (only username/role here, password via separate dialog)
                user.setUsername(username);
                user.setRole(role);
                userDAO.update(user);
            }
            loadData();
            dialog.close();
        });

        layout.getChildren().addAll(new Label("Usuario:"), usernameField);
        if (user == null) {
            layout.getChildren().addAll(new Label("Contraseña:"), passwordField);
        }
        layout.getChildren().addAll(new Label("Rol:"), roleCombo, saveBtn);

        Scene scene = new Scene(layout, 300, 300);
        try {
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        } catch (Exception ex) {
            /* Ignore */ }
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private void showPasswordDialog(User user) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Cambiar Contraseña");
        dialog.setHeaderText("Nueva contraseña para: " + user.getUsername());
        dialog.setContentText("Contraseña:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newPass -> {
            if (newPass.isEmpty()) {
                AlertHelper.showError("Error", "La contraseña no puede estar vacía.");
                return;
            }
            String hash = BCrypt.hashpw(newPass, BCrypt.gensalt());
            userDAO.updatePassword(user.getId(), hash);
            AlertHelper.showInfo("Éxito", "Contraseña actualizada.");
        });
    }

    private void deleteUser(User user) {
        if (user.getUsername().equals("admin")) {
            AlertHelper.showError("Error", "No se puede eliminar al administrador principal.");
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Eliminación");
        alert.setHeaderText("¿Eliminar usuario " + user.getUsername() + "?");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            userDAO.delete(user.getId());
            loadData();
        }
    }
}
