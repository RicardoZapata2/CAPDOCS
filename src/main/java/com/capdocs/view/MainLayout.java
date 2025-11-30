package com.capdocs.view;

import com.capdocs.datastructures.NavigationStack;
import com.capdocs.util.Session;
import com.capdocs.model.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class MainLayout {

    private final BorderPane layout;
    private final NavigationStack navigationStack;
    private final HBox header;
    private final MenuButton configMenu;
    private Label pageTitle;

    public MainLayout() {
        this.navigationStack = new NavigationStack();
        this.layout = new BorderPane();
        this.header = new HBox(20);
        this.configMenu = new MenuButton("Configuración");

        initLayout();
    }

    private void initLayout() {
        // --- Header Configuration ---
        header.getStyleClass().add("header-bar"); // New CSS class needed
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(10, 30, 10, 30));
        header.setStyle(
                "-fx-background-color: #000000; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 10, 0, 0, 5);");

        // 1. Logo (White/Contrast)
        HBox logoContainer = new HBox();
        logoContainer.setAlignment(Pos.CENTER_LEFT);
        try {
            // Assuming we have a white version or using a filter. For now, using the same
            // logo.
            // In a real scenario, load "logo_white.png"
            ImageView logo = new ImageView(new Image(getClass().getResourceAsStream("/images/logo.png")));
            logo.setFitHeight(40);
            logo.setPreserveRatio(true);
            // Optional: Apply a color adjust effect if the logo isn't white, but better to
            // use correct asset.
            logoContainer.getChildren().add(logo);
        } catch (Exception e) {
            Label textLogo = new Label("CAPDOCS");
            textLogo.setStyle("-fx-text-fill: white; -fx-font-weight: 900; -fx-font-size: 24px;");
            logoContainer.getChildren().add(textLogo);
        }

        // 2. Navigation Buttons (Center)
        HBox navBar = new HBox(15);
        navBar.setAlignment(Pos.CENTER);

        Button btnHome = createNavButton("Inicio",
                () -> setCenter(new com.capdocs.view.DashboardView(), "Resumen General"));
        Button btnPos = createNavButton("POS / Producción",
                () -> setCenter(new com.capdocs.view.PosView(), "POS / Producción"));
        Button btnInventory = createNavButton("Inventario",
                () -> setCenter(new com.capdocs.view.InventoryView(), "Inventario"));

        navBar.getChildren().addAll(btnHome, btnPos, btnInventory);

        // Admin Only Buttons
        if (Session.getCurrentUser() != null && Session.getCurrentUser().getRole() == User.Role.ADMIN) {
            Button btnFinance = createNavButton("Finanzas",
                    () -> setCenter(new com.capdocs.view.FinanceView(), "Finanzas"));
            navBar.getChildren().add(btnFinance);
        }

        // 3. Configuration Menu (Right)
        configMenu.getStyleClass().add("config-menu-button");
        configMenu.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: white; -fx-font-weight: bold; -fx-border-color: white; -fx-border-radius: 5;");

        if (Session.getCurrentUser() != null && Session.getCurrentUser().getRole() == User.Role.ADMIN) {
            MenuItem usersItem = new MenuItem("Usuarios");
            usersItem.setOnAction(e -> setCenter(new com.capdocs.view.UsersView(), "Gestión de Usuarios"));

            MenuItem suppliersItem = new MenuItem("Proveedores");
            suppliersItem.setOnAction(e -> setCenter(new com.capdocs.view.SuppliersView(), "Gestión de Proveedores"));

            configMenu.getItems().addAll(usersItem, suppliersItem, new SeparatorMenuItem());
        }

        MenuItem logoutItem = new MenuItem("Cerrar Sesión");
        // We need a way to trigger the logout callback.
        // For now, we'll assign the action in MainController or pass it here.
        // But MainLayout doesn't know about MainController's callback directly easily
        // without passing it.
        // We will set a public setter for the logout action or handle it via a global
        // event/callback.
        // For simplicity in this refactor, we'll expose a method to set the logout
        // action.

        configMenu.getItems().add(logoutItem);

        // Spacers
        Region spacerLeft = new Region();
        HBox.setHgrow(spacerLeft, Priority.ALWAYS);
        Region spacerRight = new Region();
        HBox.setHgrow(spacerRight, Priority.ALWAYS);

        header.getChildren().addAll(logoContainer, spacerLeft, navBar, spacerRight, configMenu);

        layout.setTop(header);

        // Initial View
        setCenter(new com.capdocs.view.DashboardView(), "Resumen General");
    }

    private Button createNavButton(String text, Runnable action) {
        Button btn = new Button(text);
        btn.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: #cccccc; -fx-font-size: 14px; -fx-font-weight: 600; -fx-cursor: hand;");
        btn.setOnMouseEntered(e -> btn.setStyle(
                "-fx-background-color: rgba(255,255,255,0.1); -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: 600; -fx-background-radius: 5;"));
        btn.setOnMouseExited(e -> btn.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: #cccccc; -fx-font-size: 14px; -fx-font-weight: 600;"));
        btn.setOnAction(e -> action.run());
        return btn;
    }

    public void setCenter(Parent view, String title) {
        // Fade Animation
        view.setOpacity(0);
        layout.setCenter(view);

        javafx.animation.FadeTransition ft = new javafx.animation.FadeTransition(javafx.util.Duration.millis(400),
                view);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }

    public void setLogoutAction(Runnable action) {
        for (MenuItem item : configMenu.getItems()) {
            if (item.getText() != null && item.getText().equals("Cerrar Sesión")) {
                item.setOnAction(e -> action.run());
                break;
            }
        }
    }

    public Scene getScene() {
        Scene scene = new Scene(layout, 1280, 800);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        return scene;
    }

    public BorderPane getLayout() {
        return layout;
    }
}
