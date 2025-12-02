package com.capdocs.view;

import com.capdocs.dao.ClientDAO;
import com.capdocs.dao.OrderDAO;
import com.capdocs.model.Client;
import com.capdocs.model.Order;
import com.capdocs.util.AlertHelper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
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

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class ClientsView extends BorderPane {

    private final ClientDAO clientDAO;
    private final OrderDAO orderDAO;
    private final TableView<Client> table;
    private final ObservableList<Client> data;

    public ClientsView() {
        this.clientDAO = new ClientDAO();
        this.orderDAO = new OrderDAO();
        this.data = FXCollections.observableArrayList();
        this.table = new TableView<>();

        initUI();
        loadData();
    }

    private void initUI() {
        setPadding(new Insets(20));
        setStyle("-fx-background-color: #1e1e1e;");

        // Header
        Label titleLabel = new Label("Gestión de Clientes");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");

        Button addBtn = new Button("Nuevo Cliente");
        addBtn.getStyleClass().add("button-primary");
        addBtn.setOnAction(e -> showDialog(null));

        HBox header = new HBox(20, titleLabel, addBtn);
        header.setAlignment(Pos.CENTER_LEFT);
        setTop(header);

        // Table
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setStyle("-fx-background-color: transparent;");
        VBox.setMargin(table, new Insets(20, 0, 0, 0));

        TableColumn<Client, String> nameCol = new TableColumn<>("Nombre");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Client, String> phoneCol = new TableColumn<>("Teléfono");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));

        TableColumn<Client, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<Client, Double> balanceCol = new TableColumn<>("Saldo");
        balanceCol.setCellValueFactory(new PropertyValueFactory<>("balance"));

        TableColumn<Client, String> actionsCol = new TableColumn<>("Acciones");
        actionsCol.setCellFactory(param -> new TableCell<>() {
            private final Button historyBtn = new Button("Historial");
            private final Button editBtn = new Button("Editar");
            private final Button deleteBtn = new Button("Eliminar");

            {
                historyBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 10px;");
                historyBtn.setOnAction(event -> showHistoryDialog(getTableView().getItems().get(getIndex())));

                editBtn.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-size: 10px;");
                editBtn.setOnAction(event -> showDialog(getTableView().getItems().get(getIndex())));

                deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 10px;");
                deleteBtn.setOnAction(event -> deleteClient(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5, historyBtn, editBtn, deleteBtn);
                    buttons.setAlignment(Pos.CENTER);
                    setGraphic(buttons);
                }
            }
        });

        table.getColumns().addAll(nameCol, phoneCol, emailCol, balanceCol, actionsCol);
        table.setItems(data);

        setCenter(table);
    }

    private void loadData() {
        data.setAll(clientDAO.findAll());
    }

    private void showHistoryDialog(Client client) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Historial: " + client.getName());

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: white;");

        // Summary
        double totalDebt = 0;
        List<Order> orders = orderDAO.findOrdersByClientId(client.getId());
        for (Order o : orders) {
            if (!"CANCELLED".equals(o.getStatus())) {
                totalDebt += (o.getTotalPrice() - o.getPaidAmount());
            }
        }

        boolean isAdmin = com.capdocs.util.Session.getCurrentUser().getRole() == com.capdocs.model.User.Role.ADMIN;

        if (isAdmin) {
            Label debtLabel = new Label("Deuda Total: $" + String.format("%.2f", totalDebt));
            debtLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: "
                    + (totalDebt > 0 ? "#e74c3c" : "#2ecc71") + ";");
            layout.getChildren().add(debtLabel);
        }

        // Orders Table
        TableView<Order> orderTable = new TableView<>();
        orderTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Order, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cell -> new SimpleStringProperty(String.valueOf(cell.getValue().getId())));

        TableColumn<Order, String> dateCol = new TableColumn<>("Fecha");
        dateCol.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));

        TableColumn<Order, String> statusCol = new TableColumn<>("Estado");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        orderTable.getColumns().addAll(idCol, dateCol, statusCol);

        if (isAdmin) {
            TableColumn<Order, Double> totalCol = new TableColumn<>("Total");
            totalCol.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));

            TableColumn<Order, Double> paidCol = new TableColumn<>("Pagado");
            paidCol.setCellValueFactory(new PropertyValueFactory<>("paidAmount"));

            TableColumn<Order, Double> balanceCol = new TableColumn<>("Saldo");
            balanceCol.setCellValueFactory(cell -> new SimpleDoubleProperty(
                    cell.getValue().getTotalPrice() - cell.getValue().getPaidAmount()).asObject());

            TableColumn<Order, Void> actionCol = new TableColumn<>("Acción");
            actionCol.setCellFactory(param -> new TableCell<>() {
                private final Button payBtn = new Button("Abonar");

                {
                    payBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 10px;");
                    payBtn.setOnAction(event -> showPaymentDialog(getTableView().getItems().get(getIndex()), dialog));
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    Order order = empty ? null : getTableView().getItems().get(getIndex());
                    if (order != null && (order.getTotalPrice() - order.getPaidAmount()) > 0
                            && !"CANCELLED".equals(order.getStatus())) {
                        setGraphic(payBtn);
                    } else {
                        setGraphic(null);
                    }
                }
            });
            orderTable.getColumns().addAll(totalCol, paidCol, balanceCol, actionCol);
        }

        orderTable.setItems(FXCollections.observableArrayList(orders));

        layout.getChildren().add(orderTable);

        Scene scene = new Scene(layout, 800, 500);
        try {
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        } catch (Exception ex) {
            /* Ignore */ }
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private void showPaymentDialog(Order order, Stage parentDialog) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Realizar Abono");
        dialog.setHeaderText(
                "Orden #" + order.getId() + "\nSaldo Pendiente: $" + (order.getTotalPrice() - order.getPaidAmount()));
        dialog.setContentText("Monto a abonar:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(amountStr -> {
            try {
                double amount = Double.parseDouble(amountStr);
                if (amount <= 0) {
                    AlertHelper.showError("Error", "El monto debe ser mayor a 0.");
                    return;
                }
                if (amount > (order.getTotalPrice() - order.getPaidAmount())) {
                    AlertHelper.showError("Error", "El monto excede el saldo pendiente.");
                    return;
                }

                orderDAO.addPayment(order.getId(), amount, "Efectivo", "Abono Historial");
                AlertHelper.showInfo("Éxito", "Abono registrado correctamente.");
                parentDialog.close(); // Close to refresh
                // Ideally refresh just the table, but closing is simple for now
            } catch (NumberFormatException e) {
                AlertHelper.showError("Error", "Monto inválido.");
            } catch (SQLException e) {
                AlertHelper.showError("Error", "Error al registrar pago: " + e.getMessage());
            }
        });
    }

    private void showDialog(Client client) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(client == null ? "Nuevo Cliente" : "Editar Cliente");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        TextField nameField = new TextField(client != null ? client.getName() : "");
        nameField.setPromptText("Nombre Completo");

        TextField phoneField = new TextField(client != null ? client.getPhone() : "");
        phoneField.setPromptText("Teléfono");

        TextField emailField = new TextField(client != null ? client.getEmail() : "");
        emailField.setPromptText("Email");

        TextField addressField = new TextField(client != null ? client.getAddress() : "");
        addressField.setPromptText("Dirección");

        Button saveBtn = new Button("Guardar");
        saveBtn.getStyleClass().add("button-primary");
        saveBtn.setOnAction(e -> {
            if (nameField.getText().isEmpty()) {
                AlertHelper.showError("Error", "El nombre es obligatorio.");
                return;
            }

            Client newClient = new Client(
                    client != null ? client.getId() : 0,
                    nameField.getText(),
                    phoneField.getText(),
                    emailField.getText(),
                    addressField.getText(),
                    client != null ? client.getBalance() : 0.0);

            try {
                if (client == null) {
                    clientDAO.create(newClient);
                } else {
                    clientDAO.update(newClient);
                }
                loadData();
                dialog.close();
            } catch (SQLException ex) {
                AlertHelper.showError("Error", "Error al guardar cliente: " + ex.getMessage());
            }
        });

        layout.getChildren().addAll(
                new Label("Nombre:"), nameField,
                new Label("Teléfono:"), phoneField,
                new Label("Email:"), emailField,
                new Label("Dirección:"), addressField,
                saveBtn);

        Scene scene = new Scene(layout, 350, 400);
        try {
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        } catch (Exception ex) {
            /* Ignore */ }
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private void deleteClient(Client client) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Eliminación");
        alert.setHeaderText("¿Eliminar cliente " + client.getName() + "?");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                clientDAO.delete(client.getId());
                loadData();
            } catch (SQLException e) {
                AlertHelper.showError("Error", "No se puede eliminar: " + e.getMessage());
            }
        }
    }
}
