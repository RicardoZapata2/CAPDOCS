package com.capdocs.view;

import com.capdocs.dao.SupplierDAO;
import com.capdocs.model.Supplier;
import com.capdocs.util.AlertHelper;
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

public class SuppliersView extends BorderPane {

    private final SupplierDAO supplierDAO;
    private final TableView<Supplier> table;
    private final ObservableList<Supplier> data;

    public SuppliersView() {
        this.supplierDAO = new SupplierDAO();
        this.data = FXCollections.observableArrayList();
        this.table = new TableView<>();

        initUI();
        loadData();
    }

    private void initUI() {
        setPadding(new Insets(20));
        setStyle("-fx-background-color: #1e1e1e;");

        // Header
        Label titleLabel = new Label("Gestión de Proveedores");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");

        Button addBtn = new Button("Nuevo Proveedor");
        addBtn.getStyleClass().add("button-primary");
        addBtn.setOnAction(e -> showDialog(null));

        HBox header = new HBox(20, titleLabel, addBtn);
        header.setAlignment(Pos.CENTER_LEFT);
        setTop(header);

        // Table
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setStyle("-fx-background-color: transparent;");
        VBox.setMargin(table, new Insets(20, 0, 0, 0));

        TableColumn<Supplier, String> nameCol = new TableColumn<>("Empresa");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Supplier, String> contactCol = new TableColumn<>("Contacto");
        contactCol.setCellValueFactory(new PropertyValueFactory<>("contactPerson"));

        TableColumn<Supplier, String> phoneCol = new TableColumn<>("Teléfono");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));

        TableColumn<Supplier, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<Supplier, String> actionsCol = new TableColumn<>("Acciones");
        actionsCol.setCellFactory(param -> new TableCell<>() {
            private final Button editBtn = new Button("Editar");
            private final Button deleteBtn = new Button("Eliminar");

            {
                editBtn.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-size: 10px;");
                editBtn.setOnAction(event -> showDialog(getTableView().getItems().get(getIndex())));

                deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 10px;");
                deleteBtn.setOnAction(event -> deleteSupplier(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5, editBtn, deleteBtn);
                    buttons.setAlignment(Pos.CENTER);
                    setGraphic(buttons);
                }
            }
        });

        table.getColumns().addAll(nameCol, contactCol, phoneCol, emailCol, actionsCol);
        table.setItems(data);

        setCenter(table);
    }

    private void loadData() {
        data.setAll(supplierDAO.findAll());
    }

    private void showDialog(Supplier supplier) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(supplier == null ? "Nuevo Proveedor" : "Editar Proveedor");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        TextField nameField = new TextField(supplier != null ? supplier.getName() : "");
        nameField.setPromptText("Empresa");

        TextField contactField = new TextField(supplier != null ? supplier.getContactPerson() : "");
        contactField.setPromptText("Persona de Contacto");

        TextField phoneField = new TextField(supplier != null ? supplier.getPhone() : "");
        phoneField.setPromptText("Teléfono");

        TextField emailField = new TextField(supplier != null ? supplier.getEmail() : "");
        emailField.setPromptText("Email");

        TextField addressField = new TextField(supplier != null ? supplier.getAddress() : "");
        addressField.setPromptText("Dirección");

        TextArea notesField = new TextArea(supplier != null ? supplier.getNotes() : "");
        notesField.setPromptText("Notas");
        notesField.setPrefRowCount(3);

        Button saveBtn = new Button("Guardar");
        saveBtn.getStyleClass().add("button-primary");
        saveBtn.setOnAction(e -> {
            if (nameField.getText().isEmpty()) {
                AlertHelper.showError("Error", "El nombre de la empresa es obligatorio.");
                return;
            }

            Supplier newSupplier = new Supplier(
                    supplier != null ? supplier.getId() : 0,
                    nameField.getText(),
                    contactField.getText(),
                    phoneField.getText(),
                    emailField.getText(),
                    addressField.getText(),
                    notesField.getText());

            if (supplier == null) {
                supplierDAO.create(newSupplier);
            } else {
                supplierDAO.update(newSupplier);
            }
            loadData();
            dialog.close();
        });

        layout.getChildren().addAll(
                new Label("Empresa:"), nameField,
                new Label("Contacto:"), contactField,
                new Label("Teléfono:"), phoneField,
                new Label("Email:"), emailField,
                new Label("Dirección:"), addressField,
                new Label("Notas:"), notesField,
                saveBtn);

        Scene scene = new Scene(layout, 400, 550);
        try {
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        } catch (Exception ex) {
            /* Ignore */ }
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private void deleteSupplier(Supplier supplier) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Eliminación");
        alert.setHeaderText("¿Eliminar proveedor " + supplier.getName() + "?");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            supplierDAO.delete(supplier.getId());
            loadData();
        }
    }
}
