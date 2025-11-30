package com.capdocs.view;

import com.capdocs.dao.SupplierDAO;
import com.capdocs.model.Supplier;
import com.capdocs.util.AlertHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Optional;

public class SuppliersView extends VBox {

    private final SupplierDAO supplierDAO;
    private final TableView<Supplier> table;
    private final ObservableList<Supplier> suppliers;

    public SuppliersView() {
        this.supplierDAO = new SupplierDAO();
        this.suppliers = FXCollections.observableArrayList();
        this.table = new TableView<>();

        setSpacing(20);
        setPadding(new Insets(30));
        setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Gestión de Proveedores");
        title.setStyle("-fx-font-size: 28px; -fx-text-fill: white; -fx-font-weight: bold;");

        // Table Columns
        TableColumn<Supplier, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(50);

        TableColumn<Supplier, String> nameCol = new TableColumn<>("Nombre");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(200);

        TableColumn<Supplier, String> contactCol = new TableColumn<>("Contacto");
        contactCol.setCellValueFactory(new PropertyValueFactory<>("contactPerson"));
        contactCol.setPrefWidth(150);

        TableColumn<Supplier, String> phoneCol = new TableColumn<>("Teléfono");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        phoneCol.setPrefWidth(120);

        table.getColumns().addAll(idCol, nameCol, contactCol, phoneCol);
        table.setItems(suppliers);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setStyle("-fx-background-color: transparent;");

        // Buttons
        Button addButton = new Button("Nuevo Proveedor");
        addButton.getStyleClass().add("button-primary");
        addButton.setOnAction(e -> showSupplierDialog(null));

        Button editButton = new Button("Editar");
        editButton.getStyleClass().add("button-primary");
        editButton.setOnAction(e -> {
            Supplier selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                showSupplierDialog(selected);
            } else {
                AlertHelper.showError("Error", "Seleccione un proveedor para editar.");
            }
        });

        Button deleteButton = new Button("Eliminar");
        deleteButton.getStyleClass().add("button-primary");
        deleteButton.setStyle("-fx-background-color: #e74c3c;");
        deleteButton.setOnAction(e -> deleteSupplier());

        HBox actions = new HBox(15, addButton, editButton, deleteButton);
        actions.setAlignment(Pos.CENTER);

        getChildren().addAll(title, table, actions);

        loadSuppliers();
    }

    private void loadSuppliers() {
        suppliers.setAll(supplierDAO.findAll());
    }

    private void deleteSupplier() {
        Supplier selected = table.getSelectionModel().getSelectedItem();
        if (selected != null) {
            supplierDAO.delete(selected.getId());
            loadSuppliers();
        } else {
            AlertHelper.showError("Error", "Seleccione un proveedor para eliminar.");
        }
    }

    private void showSupplierDialog(Supplier supplier) {
        Dialog<Supplier> dialog = new Dialog<>();
        dialog.setTitle(supplier == null ? "Nuevo Proveedor" : "Editar Proveedor");
        dialog.setHeaderText(null);

        ButtonType saveButtonType = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        TextField nameField = new TextField();
        nameField.setPromptText("Nombre de la empresa");
        if (supplier != null)
            nameField.setText(supplier.getName());

        TextField contactField = new TextField();
        contactField.setPromptText("Persona de contacto");
        if (supplier != null)
            contactField.setText(supplier.getContactPerson());

        TextField phoneField = new TextField();
        phoneField.setPromptText("Teléfono");
        if (supplier != null)
            phoneField.setText(supplier.getPhone());

        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        if (supplier != null)
            emailField.setText(supplier.getEmail());

        TextField addressField = new TextField();
        addressField.setPromptText("Dirección");
        if (supplier != null)
            addressField.setText(supplier.getAddress());

        TextArea notesArea = new TextArea();
        notesArea.setPromptText("Notas adicionales");
        notesArea.setPrefRowCount(3);
        if (supplier != null)
            notesArea.setText(supplier.getNotes());

        content.getChildren().addAll(
                new Label("Nombre:"), nameField,
                new Label("Contacto:"), contactField,
                new Label("Teléfono:"), phoneField,
                new Label("Email:"), emailField,
                new Label("Dirección:"), addressField,
                new Label("Notas:"), notesArea);
        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                if (nameField.getText().isEmpty())
                    return null;

                return new Supplier(
                        supplier == null ? 0 : supplier.getId(),
                        nameField.getText(),
                        contactField.getText(),
                        phoneField.getText(),
                        emailField.getText(),
                        addressField.getText(),
                        notesArea.getText());
            }
            return null;
        });

        Optional<Supplier> result = dialog.showAndWait();
        result.ifPresent(s -> {
            if (supplier == null) {
                supplierDAO.create(s);
            } else {
                supplierDAO.update(s);
            }
            loadSuppliers();
        });
    }
}
