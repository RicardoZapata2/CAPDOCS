package com.capdocs.view;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class FinanceView extends VBox {
    public FinanceView() {
        setSpacing(20);
        setAlignment(Pos.CENTER);
        Label title = new Label("Módulo de Finanzas");
        title.setStyle("-fx-font-size: 24px; -fx-text-fill: -fx-text-primary;");
        getChildren().add(title);
    }
}
