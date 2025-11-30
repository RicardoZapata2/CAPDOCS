package com.capdocs.view;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class InventoryView extends VBox {
    public InventoryView() {
        setSpacing(20);
        setAlignment(Pos.CENTER);
        Label title = new Label("Control de Inventario");
        title.setStyle("-fx-font-size: 24px; -fx-text-fill: -fx-text-primary;");
        getChildren().add(title);
    }
}
