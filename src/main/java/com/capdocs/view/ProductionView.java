package com.capdocs.view;

import com.capdocs.dao.OrderDAO;
import com.capdocs.model.Order;
import com.capdocs.model.OrderItem;
import com.capdocs.util.AlertHelper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class ProductionView extends BorderPane {

    private final OrderDAO orderDAO;
    private final TableView<Order> orderTable;
    private final ObservableList<Order> orderList;

    public ProductionView() {
        this.orderDAO = new OrderDAO();
        this.orderList = FXCollections.observableArrayList();
        this.orderTable = new TableView<>();

        initUI();
        loadData();
    }

    private void initUI() {
        setPadding(new Insets(20));
        setStyle("-fx-background-color: #1e1e1e;");

        // Header
        Label titleLabel = new Label("Cola de Producción");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");

        Button refreshBtn = new Button("Actualizar");
        refreshBtn.getStyleClass().add("button-primary");
        refreshBtn.setOnAction(e -> loadData());

        HBox header = new HBox(20, titleLabel, refreshBtn);
        header.setAlignment(Pos.CENTER_LEFT);
        setTop(header);

        // Table
        orderTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        orderTable.setStyle("-fx-background-color: transparent;");
        VBox.setMargin(orderTable, new Insets(20, 0, 0, 0));

        TableColumn<Order, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Order, String> clientCol = new TableColumn<>("Cliente");
        clientCol.setCellValueFactory(new PropertyValueFactory<>("clientName"));

        TableColumn<Order, String> statusCol = new TableColumn<>("Estado");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.equals("PENDING"))
                        setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                    else if (item.equals("IN_PROCESS"))
                        setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold;");
                    else if (item.equals("FINISHED"))
                        setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold;");
                    else
                        setStyle("-fx-text-fill: #95a5a6;");
                }
            }
        });

        TableColumn<Order, String> dateCol = new TableColumn<>("Fecha Entrega");
        dateCol.setCellValueFactory(cellData -> {
            if (cellData.getValue().getDeliveryDate() != null) {
                return new SimpleStringProperty(
                        cellData.getValue().getDeliveryDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            }
            return new SimpleStringProperty("N/A");
        });

        TableColumn<Order, String> actionsCol = new TableColumn<>("Acciones");
        actionsCol.setCellFactory(param -> new TableCell<>() {
            private final Button detailsBtn = new Button("Detalles");
            private final ComboBox<String> statusCombo = new ComboBox<>();

            {
                detailsBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 10px;");
                detailsBtn.setOnAction(event -> showOrderDetails(getTableView().getItems().get(getIndex())));

                statusCombo.getItems().addAll("PENDING", "IN_PROCESS", "FINISHED", "DELIVERED", "CANCELLED");
                statusCombo.setStyle("-fx-font-size: 10px; -fx-pref-width: 100px;");
                statusCombo.setOnAction(event -> {
                    Order order = getTableView().getItems().get(getIndex());
                    String newStatus = statusCombo.getValue();
                    if (newStatus != null && !newStatus.equals(order.getStatus())) {
                        orderDAO.updateStatus(order.getId(), newStatus);
                        loadData();
                    }
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Order order = getTableView().getItems().get(getIndex());
                    statusCombo.setValue(order.getStatus());
                    HBox box = new HBox(5, statusCombo, detailsBtn);
                    box.setAlignment(Pos.CENTER);
                    setGraphic(box);
                }
            }
        });

        orderTable.getColumns().addAll(idCol, clientCol, statusCol, dateCol, actionsCol);
        orderTable.setItems(orderList);

        setCenter(orderTable);
    }

    private void loadData() {
        orderList.setAll(orderDAO.findAllOrders());
    }

    private void showOrderDetails(Order order) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Detalles de Orden #" + order.getId());

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: white;");

        // Info Header
        GridPane infoGrid = new GridPane();
        infoGrid.setHgap(10);
        infoGrid.setVgap(5);
        infoGrid.add(new Label("Cliente:"), 0, 0);
        infoGrid.add(new Label(order.getClientName()), 1, 0);
        infoGrid.add(new Label("Total:"), 0, 1);
        infoGrid.add(new Label("$" + order.getTotalPrice()), 1, 1);
        infoGrid.add(new Label("Pagado:"), 0, 2);
        infoGrid.add(new Label("$" + order.getPaidAmount()), 1, 2);
        infoGrid.add(new Label("Saldo:"), 0, 3);
        double balance = order.getTotalPrice() - order.getPaidAmount();
        Label balanceLabel = new Label("$" + balance);
        if (balance > 0)
            balanceLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        else
            balanceLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
        infoGrid.add(balanceLabel, 1, 3);

        // Items Table
        TableView<OrderItem> itemTable = new TableView<>();
        itemTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        itemTable.setPrefHeight(200);

        TableColumn<OrderItem, String> productCol = new TableColumn<>("Producto");
        productCol.setCellValueFactory(new PropertyValueFactory<>("productName"));

        TableColumn<OrderItem, String> sizeCol = new TableColumn<>("Talla");
        sizeCol.setCellValueFactory(new PropertyValueFactory<>("size"));

        TableColumn<OrderItem, Integer> qtyCol = new TableColumn<>("Cant.");
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<OrderItem, Double> priceCol = new TableColumn<>("Precio Unit.");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));

        TableColumn<OrderItem, String> techCol = new TableColumn<>("Técnica");
        techCol.setCellValueFactory(new PropertyValueFactory<>("techniqueDetails"));

        itemTable.getColumns().addAll(productCol, sizeCol, qtyCol, priceCol, techCol);

        List<OrderItem> items = orderDAO.findItemsByOrderId(order.getId());
        itemTable.setItems(FXCollections.observableArrayList(items));

        Button closeBtn = new Button("Cerrar");
        closeBtn.getStyleClass().add("button-primary");
        closeBtn.setOnAction(e -> dialog.close());

        root.getChildren().addAll(new Label("Información General"), infoGrid, new Separator(),
                new Label("Items de la Orden"), itemTable, closeBtn);

        Scene scene = new Scene(root, 600, 500);
        try {
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        } catch (Exception ex) {
            /* Ignore */ }
        dialog.setScene(scene);
        dialog.showAndWait();
    }
}
