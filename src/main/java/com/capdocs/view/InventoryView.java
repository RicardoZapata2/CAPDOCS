package com.capdocs.view;

import com.capdocs.dao.ProductDAO;
import com.capdocs.model.Category;
import com.capdocs.model.Product;
import com.capdocs.model.ProductVariant;
import com.capdocs.util.AlertHelper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.List;

public class InventoryView extends BorderPane {

    private final ProductDAO productDAO;
    private TableView<Product> productTable;
    private ObservableList<Product> productList;

    public InventoryView() {
        this.productDAO = new ProductDAO();
        this.productList = FXCollections.observableArrayList();
        initUI();
        loadData();
    }

    private void initUI() {
        setPadding(new Insets(20));
        setStyle("-fx-background-color: #1e1e1e;");

        // Header
        Label titleLabel = new Label("Gestión de Inventario");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");

        HBox header = new HBox(20, titleLabel);

        // Only ADMIN can add products
        if (com.capdocs.util.Session.getCurrentUser().getRole() == com.capdocs.model.User.Role.ADMIN) {
            Button addProductBtn = new Button("Nuevo Producto");
            addProductBtn.getStyleClass().add("button-primary");
            addProductBtn.setOnAction(e -> showProductDialog(null));
            header.getChildren().add(addProductBtn);
        }

        header.setAlignment(Pos.CENTER_LEFT);
        setTop(header);

        // Table
        productTable = new TableView<>();
        productTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        productTable.setStyle("-fx-background-color: transparent;");
        VBox.setMargin(productTable, new Insets(20, 0, 0, 0));

        TableColumn<Product, String> nameCol = new TableColumn<>("Producto");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Product, String> categoryCol = new TableColumn<>("Categoría");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("categoryName"));

        TableColumn<Product, Double> priceCol = new TableColumn<>("Precio Venta");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("salePrice"));

        productTable.getColumns().addAll(nameCol, categoryCol, priceCol);

        // Only ADMIN sees actions
        if (com.capdocs.util.Session.getCurrentUser().getRole() == com.capdocs.model.User.Role.ADMIN) {
            TableColumn<Product, String> actionsCol = new TableColumn<>("Acciones");
            actionsCol.setCellFactory(param -> new TableCell<>() {
                private final Button variantsBtn = new Button("Variantes");
                private final Button editBtn = new Button("Editar");

                {
                    variantsBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 10px;");
                    variantsBtn.setOnAction(event -> showVariantsDialog(getTableView().getItems().get(getIndex())));

                    editBtn.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-size: 10px;");
                    editBtn.setOnAction(event -> showProductDialog(getTableView().getItems().get(getIndex())));
                }

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        HBox buttons = new HBox(5, variantsBtn, editBtn);
                        buttons.setAlignment(Pos.CENTER);
                        setGraphic(buttons);
                    }
                }
            });
            productTable.getColumns().add(actionsCol);
        }

        productTable.setItems(productList);
        setCenter(productTable);
    }

    private void loadData() {
        productList.setAll(productDAO.findAllProducts());
    }

    private void showProductDialog(Product product) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(product == null ? "Nuevo Producto" : "Editar Producto");

        VBox root = new VBox(15);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: white; -fx-background-radius: 10;");
        root.setAlignment(Pos.TOP_CENTER);

        // Title
        Label headerLabel = new Label(product == null ? "Crear Nuevo Producto" : "Editar Producto");
        headerLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // Form Container (Grid for 2 columns)
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(15);
        grid.setAlignment(Pos.CENTER);

        // Fields
        TextField nameField = createStyledTextField(product != null ? product.getName() : "", "Nombre del Producto");

        ComboBox<Category> categoryCombo = new ComboBox<>();
        categoryCombo.setItems(FXCollections.observableArrayList(productDAO.findAllCategories()));
        categoryCombo.setPromptText("Seleccione Categoría");
        categoryCombo.setMaxWidth(Double.MAX_VALUE);
        categoryCombo.setStyle("-fx-background-color: #ecf0f1; -fx-font-size: 14px; -fx-padding: 5;");
        if (product != null) {
            for (Category c : categoryCombo.getItems()) {
                if (c.getId().equals(product.getCategoryId())) {
                    categoryCombo.getSelectionModel().select(c);
                    break;
                }
            }
        }

        TextArea descField = new TextArea(product != null ? product.getDescription() : "");
        descField.setPromptText("Descripción del producto...");
        descField.setPrefRowCount(3);
        descField.setWrapText(true);
        descField.setStyle(
                "-fx-control-inner-background: #ecf0f1; -fx-background-color: #ecf0f1; -fx-border-color: transparent;");

        TextField baseCostField = createStyledTextField(product != null ? String.valueOf(product.getBaseCost()) : "",
                "0.00");
        TextField salePriceField = createStyledTextField(product != null ? String.valueOf(product.getSalePrice()) : "",
                "0.00");
        TextField wholesalePriceField = createStyledTextField(
                product != null ? String.valueOf(product.getWholesalePrice()) : "", "0.00");

        // Image Upload Section
        Button uploadBtn = new Button("Seleccionar Imagen");
        uploadBtn.getStyleClass().add("button-primary");

        ImageView imagePreview = new ImageView();
        imagePreview.setFitHeight(100);
        imagePreview.setFitWidth(100);
        imagePreview.setPreserveRatio(true);
        imagePreview.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 2);");

        Label imagePathLabel = new Label(product != null
                ? (product.getImagePath() != null ? new File(product.getImagePath()).getName() : "Sin imagen")
                : "Sin imagen seleccionada");
        imagePathLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 12px;");

        final String[] selectedImagePath = { product != null ? product.getImagePath() : null };

        if (product != null && product.getImagePath() != null) {
            try {
                File imgFile = new File(product.getImagePath());
                if (imgFile.exists()) {
                    imagePreview.setImage(new Image(imgFile.toURI().toString()));
                }
            } catch (Exception e) {
                /* Ignore */ }
        }

        uploadBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Seleccionar Imagen");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg"));
            File file = fileChooser.showOpenDialog(dialog);
            if (file != null) {
                try {
                    File destDir = new File("images/products");
                    if (!destDir.exists())
                        destDir.mkdirs();

                    String fileName = System.currentTimeMillis() + "_" + file.getName();
                    File destFile = new File(destDir, fileName);

                    Files.copy(file.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                    selectedImagePath[0] = destFile.getPath();
                    imagePathLabel.setText(fileName);
                    imagePreview.setImage(new Image(destFile.toURI().toString()));
                } catch (Exception ex) {
                    AlertHelper.showError("Error", "No se pudo guardar la imagen: " + ex.getMessage());
                }
            }
        });

        VBox imageContainer = new VBox(10, uploadBtn, imagePathLabel, imagePreview);
        imageContainer.setAlignment(Pos.CENTER_LEFT);

        // Layout placement
        // Row 0: Name & Category
        grid.add(createLabel("Nombre"), 0, 0);
        grid.add(nameField, 0, 1);
        grid.add(createLabel("Categoría"), 1, 0);
        grid.add(categoryCombo, 1, 1);

        // Row 1: Description (Spans 2 columns)
        grid.add(createLabel("Descripción"), 0, 2);
        grid.add(descField, 0, 3, 2, 1);

        // Row 2: Prices
        grid.add(createLabel("Costo Base"), 0, 4);
        grid.add(baseCostField, 0, 5);
        grid.add(createLabel("Precio Venta"), 1, 4);
        grid.add(salePriceField, 1, 5);

        // Row 3: Wholesale & Image
        grid.add(createLabel("Precio Mayorista"), 0, 6);
        grid.add(wholesalePriceField, 0, 7);

        grid.add(createLabel("Imagen del Producto"), 1, 6);
        grid.add(imageContainer, 1, 7, 1, 2);

        // Save Button
        Button saveBtn = new Button("Guardar Producto");
        saveBtn.getStyleClass().add("button-primary");
        saveBtn.setPrefWidth(200);
        saveBtn.setOnAction(e -> {
            try {
                String name = nameField.getText();
                Category cat = categoryCombo.getValue();
                double cost = Double.parseDouble(baseCostField.getText());
                double price = Double.parseDouble(salePriceField.getText());
                double wholesale = wholesalePriceField.getText().isEmpty() ? 0
                        : Double.parseDouble(wholesalePriceField.getText());

                if (name.isEmpty() || cat == null) {
                    AlertHelper.showError("Error", "Nombre y Categoría son obligatorios.");
                    return;
                }

                Product newProduct = Product.builder()
                        .id(product != null ? product.getId() : null)
                        .name(name)
                        .categoryId(cat.getId())
                        .description(descField.getText())
                        .baseCost(cost)
                        .salePrice(price)
                        .wholesalePrice(wholesale)
                        .imagePath(selectedImagePath[0])
                        .build();

                if (product == null) {
                    productDAO.saveProduct(newProduct);
                } else {
                    AlertHelper.showInfo("Info",
                            "Actualización simulada (falta update en DAO). Se guardará como nuevo.");
                    productDAO.saveProduct(newProduct);
                }

                loadData();
                dialog.close();
            } catch (NumberFormatException ex) {
                AlertHelper.showError("Error", "Verifique los campos numéricos.");
            } catch (SQLException ex) {
                AlertHelper.showError("Error", "Error de base de datos: " + ex.getMessage());
            }
        });

        root.getChildren().addAll(headerLabel, grid, saveBtn);

        Scene scene = new Scene(root, 600, 650);
        try {
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        } catch (Exception ex) {
            /* Ignore */ }

        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private TextField createStyledTextField(String text, String prompt) {
        TextField tf = new TextField(text);
        tf.setPromptText(prompt);
        tf.setStyle("-fx-background-color: #ecf0f1; -fx-padding: 10; -fx-background-radius: 5;");
        return tf;
    }

    private Label createLabel(String text) {
        Label lbl = new Label(text);
        lbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #34495e; -fx-font-size: 12px;");
        return lbl;
    }

    private void showVariantsDialog(Product product) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Variantes: " + product.getName());

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        TableView<ProductVariant> variantTable = new TableView<>();
        variantTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<ProductVariant, String> sizeCol = new TableColumn<>("Talla");
        sizeCol.setCellValueFactory(new PropertyValueFactory<>("size"));

        TableColumn<ProductVariant, Integer> stockCol = new TableColumn<>("Stock");
        stockCol.setCellValueFactory(new PropertyValueFactory<>("stockQuantity"));

        variantTable.getColumns().addAll(sizeCol, stockCol);

        // Load variants
        List<ProductVariant> variants = productDAO.findVariantsByProductId(product.getId());
        variantTable.setItems(FXCollections.observableArrayList(variants));

        // Add Variant Form
        HBox addBox = new HBox(10);
        addBox.setAlignment(Pos.CENTER_LEFT);

        ComboBox<String> sizeCombo = new ComboBox<>();
        sizeCombo.getItems().addAll("S/M", "L/XL", "AJUSTABLE");
        sizeCombo.setPromptText("Talla");

        TextField stockField = new TextField();
        stockField.setPromptText("Cantidad");
        stockField.setPrefWidth(80);

        Button addBtn = new Button("Agregar");
        addBtn.getStyleClass().add("button-primary");
        addBtn.setOnAction(e -> {
            try {
                String size = sizeCombo.getValue();
                int stock = Integer.parseInt(stockField.getText());

                if (size == null) {
                    AlertHelper.showError("Error", "Seleccione una talla.");
                    return;
                }

                ProductVariant variant = ProductVariant.builder()
                        .productId(product.getId())
                        .size(size)
                        .stockQuantity(stock)
                        .build();

                productDAO.saveVariant(variant);
                variantTable.setItems(
                        FXCollections.observableArrayList(productDAO.findVariantsByProductId(product.getId())));
                stockField.clear();
            } catch (NumberFormatException ex) {
                AlertHelper.showError("Error", "Stock debe ser un número.");
            } catch (SQLException ex) {
                AlertHelper.showError("Error", "Error al guardar variante: " + ex.getMessage());
            }
        });

        addBox.getChildren().addAll(sizeCombo, stockField, addBtn);

        layout.getChildren().addAll(variantTable, addBox);

        Scene scene = new Scene(layout, 400, 400);
        try {
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        } catch (Exception ex) {
            /* Ignore */ }
        dialog.setScene(scene);
        dialog.showAndWait();
    }
}
