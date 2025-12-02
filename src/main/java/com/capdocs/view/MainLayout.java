package com.capdocs.view;

import com.capdocs.model.User;
import com.capdocs.util.Session;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import java.util.Stack;

public class MainLayout {

    private final BorderPane layout;
    private final HBox header;
    private final MenuButton configMenu;
    private final Label pageTitle;
    private final Button backButton;

    // Stack for Navigation History (Academic Requirement)
    private final Stack<Parent> viewStack = new Stack<>();
    private final Stack<String> titleStack = new Stack<>();

    public MainLayout() {
        this.layout = new BorderPane();
        this.header = new HBox(20);
        this.configMenu = new MenuButton("Configuración");
        this.pageTitle = new Label("Resumen General");
        this.backButton = new Button("← Volver");

        initLayout();
    }

    private void initLayout() {
        // --- Global Dark Theme Background ---
        layout.setStyle("-fx-background-color: #121212;");

        // --- Header Configuration ---
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(10, 30, 10, 30));
        header.setStyle(
                "-fx-background-color: #1f1f1f; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 10, 0, 0, 5);");

        // 1. Logo
        HBox logoContainer = new HBox(10);
        logoContainer.setAlignment(Pos.CENTER_LEFT);
        try {
            ImageView logo = new ImageView(new Image(getClass().getResourceAsStream("/images/logo.png")));
            logo.setFitHeight(35);
            logo.setPreserveRatio(true);
            logoContainer.getChildren().add(logo);
        } catch (Exception e) {
            Label textLogo = new Label("CAPDOCS");
            textLogo.setStyle("-fx-text-fill: white; -fx-font-weight: 900; -fx-font-size: 22px;");
            logoContainer.getChildren().add(textLogo);
        }

        // 2. Back Button & Title
        backButton.setVisible(false); // Initially hidden
        backButton.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: #3498db; -fx-font-weight: bold; -fx-cursor: hand; -fx-border-color: #3498db; -fx-border-radius: 5;");
        backButton.setOnAction(e -> goBack());

        pageTitle.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 0 0 0 10;");

        HBox titleBox = new HBox(10, backButton, pageTitle);
        titleBox.setAlignment(Pos.CENTER_LEFT);

        // 3. Navigation Buttons (Center)
        HBox navBar = new HBox(15);
        navBar.setAlignment(Pos.CENTER);

        Button btnHome = createNavButton("Inicio",
                () -> navigateTo(new DashboardView(), "Resumen General"));

        Button btnSales = createNavButton("Ventas",
                () -> navigateTo(new PosView(), "Punto de Venta"));

        Button btnProduction = createNavButton("Producción",
                () -> navigateTo(new ProductionView(), "Producción"));

        Button btnInventory = createNavButton("Inventario",
                () -> navigateTo(new InventoryView(), "Inventario"));

        Button btnSuppliers = createNavButton("Proveedores",
                () -> navigateTo(new SuppliersView(), "Proveedores"));

        Button btnClients = createNavButton("Clientes",
                () -> navigateTo(new ClientsView(), "Clientes"));

        navBar.getChildren().addAll(btnHome, btnSales, btnProduction, btnInventory, btnSuppliers, btnClients);

        // Admin Only Buttons
        if (Session.getCurrentUser() != null && Session.getCurrentUser().getRole() == User.Role.ADMIN) {
            Button btnFinance = createNavButton("Finanzas",
                    () -> navigateTo(new FinanceView(), "Finanzas"));
            navBar.getChildren().add(btnFinance);
        }

        // 4. Configuration Menu (Right)
        configMenu.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: white; -fx-font-weight: bold; -fx-border-color: #555; -fx-border-radius: 5;");

        if (Session.getCurrentUser() != null && Session.getCurrentUser().getRole() == User.Role.ADMIN) {
            MenuItem usersItem = new MenuItem("Usuarios");
            usersItem.setOnAction(e -> navigateTo(new UsersView(), "Gestión de Usuarios"));
            configMenu.getItems().add(usersItem);
            configMenu.getItems().add(new SeparatorMenuItem());
        }

        MenuItem themeItem = new MenuItem("Cambiar Tema");
        themeItem.setOnAction(e -> toggleTheme());
        configMenu.getItems().add(themeItem);

        MenuItem logoutItem = new MenuItem("Cerrar Sesión");
        configMenu.getItems().add(logoutItem);

        // Spacers
        Region spacerLeft = new Region();
        HBox.setHgrow(spacerLeft, Priority.ALWAYS);
        Region spacerRight = new Region();
        HBox.setHgrow(spacerRight, Priority.ALWAYS);

        header.getChildren().addAll(logoContainer, titleBox, spacerLeft, navBar, spacerRight, configMenu);
        layout.setTop(header);

        // Initial View
        layout.setCenter(new DashboardView());
    }

    private void toggleTheme() {
        Scene scene = layout.getScene();
        if (scene != null) {
            if (scene.getStylesheets().contains(getClass().getResource("/styles.css").toExternalForm())) {
                scene.getStylesheets().clear();
                scene.getStylesheets().add(getClass().getResource("/dark-theme.css").toExternalForm());
            } else {
                scene.getStylesheets().clear();
                scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            }
        }
    }

    private Button createNavButton(String text, Runnable action) {
        Button btn = new Button(text);
        btn.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: #aaaaaa; -fx-font-size: 14px; -fx-font-weight: 600; -fx-cursor: hand;");
        btn.setOnMouseEntered(e -> btn.setStyle(
                "-fx-background-color: rgba(255,255,255,0.1); -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: 600; -fx-background-radius: 5;"));
        btn.setOnMouseExited(e -> btn.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: #aaaaaa; -fx-font-size: 14px; -fx-font-weight: 600;"));
        btn.setOnAction(e -> action.run());
        return btn;
    }

    /**
     * Navigates to a new view, pushing the current one to the stack.
     */
    public void navigateTo(Parent view, String title) {
        // Push current view to stack if it exists
        if (layout.getCenter() != null) {
            viewStack.push((Parent) layout.getCenter());
            titleStack.push(pageTitle.getText());
        }

        // Set new view
        layout.setCenter(view);
        pageTitle.setText(title);

        // Animation
        view.setOpacity(0);
        javafx.animation.FadeTransition ft = new javafx.animation.FadeTransition(javafx.util.Duration.millis(300),
                view);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();

        updateBackButton();
    }

    /**
     * Pops the previous view from the stack.
     */
    public void goBack() {
        if (!viewStack.isEmpty()) {
            Parent previousView = viewStack.pop();
            String previousTitle = titleStack.pop();

            layout.setCenter(previousView);
            pageTitle.setText(previousTitle);

            updateBackButton();
        }
    }

    private void updateBackButton() {
        backButton.setVisible(!viewStack.isEmpty());
    }

    public void setLogoutAction(Runnable action) {
        for (MenuItem item : configMenu.getItems()) {
            if ("Cerrar Sesión".equals(item.getText())) {
                item.setOnAction(e -> action.run());
                break;
            }
        }
    }

    public Scene getScene() {
        Scene scene = new Scene(layout, 1280, 800);
        // Load CSS if available, otherwise styles are inline
        try {
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        } catch (Exception e) {
            // CSS might not exist yet, ignore
        }
        return scene;
    }
}
