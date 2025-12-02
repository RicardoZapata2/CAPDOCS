package com.capdocs.view;

import com.capdocs.dao.ClientDAO;
import com.capdocs.dao.OrderDAO;
import com.capdocs.dao.ProductDAO;
import com.capdocs.model.*;
import com.capdocs.util.AlertHelper;
import com.capdocs.util.Session;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PosView extends BorderPane {

    private final ProductDAO productDAO;
    private final ClientDAO clientDAO;
    private final OrderDAO orderDAO;

    private ComboBox<Client> clientCombo;
    private ComboBox<Product> productCombo;
    private ComboBox<ProductVariant> variantCombo;
    private TextField quantityField;
    private TableView<OrderItem> cartTable;
    private Label totalLabel;
    private TextField paymentField;
    private ObservableList<OrderItem> cartItems;

    public PosView() {
        this.productDAO = new ProductDAO();
        this.clientDAO = new ClientDAO();
        this.orderDAO = new OrderDAO();
        this.cartItems = FXCollections.observableArrayList();

        initUI();
    }

    private void initUI() {
        setPadding(new Insets(20));

        // Top: Client Selection
        HBox topBox = new HBox(15);
        topBox.setAlignment(Pos.CENTER_LEFT);
        topBox.setPadding(new Insets(0, 0, 20, 0));

        clientCombo = new ComboBox<>();
        clientCombo.setPromptText("Seleccionar Cliente");
        clientCombo.setPrefWidth(250);
        loadClients();

        Button newClientBtn = new Button("Nuevo Cliente");
        newClientBtn.setOnAction(e -> showNewClientDialog());

        topBox.getChildren().addAll(new Label("Cliente:"), clientCombo, newClientBtn);
        setTop(topBox);

        // Center: Product Selection & Cart
        VBox centerBox = new VBox(15);

        // Product Selection Row
        HBox productBox = new HBox(15);
        productBox.setAlignment(Pos.CENTER_LEFT);

        productCombo = new ComboBox<>();
        productCombo.setPromptText("Producto");
        productCombo.setPrefWidth(200);
        productCombo.setItems(FXCollections.observableArrayList(productDAO.findAllProducts()));
        productCombo.setOnAction(e -> loadVariants());

        variantCombo = new ComboBox<>();
        variantCombo.setPromptText("Talla");
        variantCombo.setPrefWidth(100);

        quantityField = new TextField("1");
        quantityField.setPrefWidth(60);

        Button addToCartBtn = new Button("Agregar");
        addToCartBtn.getStyleClass().add("button-primary");
        addToCartBtn.setOnAction(e -> addToCart());

        productBox.getChildren().addAll(productCombo, variantCombo, new Label("Cant:"), quantityField, addToCartBtn);

        // Cart Table
        cartTable = new TableView<>();
        cartTable.setItems(cartItems);
        cartTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<OrderItem, String> prodCol = new TableColumn<>("Producto");
        prodCol.setCellValueFactory(cell -> {
            // This is a bit inefficient, fetching product name again, but simple for now
            // Ideally OrderItem should have product name or we fetch it
            // For MVP, I'll just return a placeholder or need to enhance OrderItem to hold
            // temp name
            return new SimpleStringProperty("Producto ID: " + cell.getValue().getVariantId());
            // TODO: Enhance OrderItem to store product name for UI display
        });

        TableColumn<OrderItem, Integer> qtyCol = new TableColumn<>("Cant");
        qtyCol.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getQuantity()).asObject());

        TableColumn<OrderItem, Double> priceCol = new TableColumn<>("Precio Unit");
        priceCol.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().getUnitPrice()).asObject());

        TableColumn<OrderItem, Double> subtotalCol = new TableColumn<>("Subtotal");
        subtotalCol.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().getSubtotal()).asObject());

        cartTable.getColumns().addAll(prodCol, qtyCol, priceCol, subtotalCol);

        centerBox.getChildren().addAll(productBox, cartTable);
        setCenter(centerBox);

        // Bottom: Totals & Payment
        VBox bottomBox = new VBox(15);
        bottomBox.setPadding(new Insets(20, 0, 0, 0));
        bottomBox.setAlignment(Pos.CENTER_RIGHT);

        totalLabel = new Label("Total: $0.00");
        totalLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        HBox paymentBox = new HBox(15);
        paymentBox.setAlignment(Pos.CENTER_RIGHT);

        paymentField = new TextField();
        paymentField.setPromptText("Monto Abono");

        Button finishBtn = new Button("Finalizar Venta");
        finishBtn.getStyleClass().add("button-primary");
        finishBtn.setStyle("-fx-font-size: 16px; -fx-padding: 10 30; -fx-background-color: #27ae60;");
        finishBtn.setOnAction(e -> finishSale());

        paymentBox.getChildren().addAll(new Label("Abono:"), paymentField, finishBtn);

        bottomBox.getChildren().addAll(totalLabel, paymentBox);
        setBottom(bottomBox);
    }

    private void loadClients() {
        clientCombo.setItems(FXCollections.observableArrayList(clientDAO.findAll()));
    }

    private void loadVariants() {
        Product selected = productCombo.getValue();
        if (selected != null) {
            variantCombo
                    .setItems(FXCollections.observableArrayList(productDAO.findVariantsByProductId(selected.getId())));
        }
    }

    private void addToCart() {
        Product product = productCombo.getValue();
        ProductVariant variant = variantCombo.getValue();
        String qtyStr = quantityField.getText();

        if (product == null || variant == null || qtyStr.isEmpty()) {
            AlertHelper.showError("Error", "Seleccione producto, talla y cantidad.");
            return;
        }

        try {
            int qty = Integer.parseInt(qtyStr);
            if (qty <= 0)
                throw new NumberFormatException();

            if (qty > variant.getStockQuantity()) {
                AlertHelper.showError("Stock Insuficiente",
                        "Solo hay " + variant.getStockQuantity() + " unidades disponibles.");
                return;
            }

            double unitPrice = product.getSalePrice();
            double subtotal = unitPrice * qty;

            OrderItem item = new OrderItem();
            item.setVariantId(variant.getId());
            item.setQuantity(qty);
            item.setUnitPrice(unitPrice);
            item.setSubtotal(subtotal);
            // item.setProductName(product.getName() + " - " + variant.getSize()); // Need
            // to add this field to OrderItem or wrap it

            cartItems.add(item);
            updateTotal();

        } catch (NumberFormatException e) {
            AlertHelper.showError("Error", "Cantidad inválida.");
        }
    }

    private void updateTotal() {
        double total = cartItems.stream().mapToDouble(OrderItem::getSubtotal).sum();
        totalLabel.setText(String.format("Total: $%.2f", total));
    }

    private void finishSale() {
        Client client = clientCombo.getValue();
        if (client == null) {
            AlertHelper.showError("Error", "Seleccione un cliente.");
            return;
        }

        if (cartItems.isEmpty()) {
            AlertHelper.showError("Error", "El carrito está vacío.");
            return;
        }

        try {
            double total = cartItems.stream().mapToDouble(OrderItem::getSubtotal).sum();
            double paid = 0;
            if (!paymentField.getText().isEmpty()) {
                paid = Double.parseDouble(paymentField.getText());
            }

            Order order = new Order();
            order.setClientId(client.getId());
            order.setStatus("PENDING");
            order.setTotalPrice(total);
            order.setPaidAmount(paid);
            order.setDeliveryDate(LocalDateTime.now().plusDays(3)); // Default 3 days

            Payment payment = new Payment();
            payment.setAmount(paid);
            payment.setPaymentMethod("Efectivo"); // Default
            payment.setNotes("Abono inicial");

            orderDAO.createOrder(order, new ArrayList<>(cartItems), payment);

            AlertHelper.showInfo("Éxito", "Venta registrada correctamente. ID: " + order.getId());
            cartItems.clear();
            paymentField.clear();
            updateTotal();
            loadClients(); // Refresh if needed

        } catch (NumberFormatException e) {
            AlertHelper.showError("Error", "Monto de abono inválido.");
        } catch (SQLException e) {
            AlertHelper.showError("Error", "Error al guardar la venta: " + e.getMessage());
        }
    }

    private void showNewClientDialog() {
        // Simple dialog to add client
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nuevo Cliente");
        dialog.setHeaderText("Ingrese nombre del cliente:");
        dialog.showAndWait().ifPresent(name -> {
            try {
                Client c = new Client();
                c.setName(name);
                c.setPhone("");
                c.setEmail("");
                c.setAddress("");
                clientDAO.create(c);
                loadClients();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
