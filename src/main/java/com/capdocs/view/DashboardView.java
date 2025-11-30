package com.capdocs.view;

import com.capdocs.datastructures.ProductionQueue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class DashboardView extends VBox {

    private final Label pendingOrdersLabel;
    private final Label dailySalesLabel;
    private final VBox queueContainer;

    public DashboardView() {
        setSpacing(30);
        setPadding(new Insets(30));
        setAlignment(Pos.TOP_CENTER);

        // Title
        Label title = new Label("Resumen General");
        title.setStyle("-fx-font-size: 28px; -fx-text-fill: white; -fx-font-weight: bold;");

        // KPI Cards
        HBox kpiContainer = new HBox(20);
        kpiContainer.setAlignment(Pos.CENTER);

        VBox card1 = createKpiCard("Órdenes Pendientes", "0", "#3498db");
        VBox card2 = createKpiCard("Ventas Hoy", "$0.00", "#2ecc71");
        VBox card3 = createKpiCard("Producción Activa", "0", "#e67e22");

        pendingOrdersLabel = (Label) card1.getChildren().get(1);
        dailySalesLabel = (Label) card2.getChildren().get(1);

        kpiContainer.getChildren().addAll(card1, card2, card3);

        // Production Queue Section
        VBox queueSection = new VBox(15);
        queueSection.setAlignment(Pos.TOP_LEFT);
        queueSection
                .setStyle("-fx-background-color: rgba(255,255,255,0.05); -fx-background-radius: 10; -fx-padding: 20;");

        Label queueTitle = new Label("Cola de Producción");
        queueTitle.setStyle("-fx-font-size: 20px; -fx-text-fill: white; -fx-font-weight: bold;");

        queueContainer = new VBox(10);
        // Placeholder for queue items
        Label emptyLabel = new Label("No hay órdenes en cola.");
        emptyLabel.setStyle("-fx-text-fill: #aaaaaa; -fx-font-style: italic;");
        queueContainer.getChildren().add(emptyLabel);

        queueSection.getChildren().addAll(queueTitle, queueContainer);
        VBox.setVgrow(queueSection, Priority.ALWAYS);

        getChildren().addAll(title, kpiContainer, queueSection);
    }

    private VBox createKpiCard(String title, String value, String colorHex) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20));
        card.setPrefWidth(250);
        card.setStyle("-fx-background-color: rgba(255,255,255,0.05); -fx-background-radius: 10; -fx-border-color: "
                + colorHex + "; -fx-border-width: 0 0 0 4;");

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 14px;");

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;");

        card.getChildren().addAll(titleLabel, valueLabel);
        return card;
    }

    public Label getPendingOrdersLabel() {
        return pendingOrdersLabel;
    }

    public Label getDailySalesLabel() {
        return dailySalesLabel;
    }

    public void updateQueue(ProductionQueue queue) {
        queueContainer.getChildren().clear();
        if (queue.isEmpty()) {
            Label emptyLabel = new Label("No hay órdenes en cola.");
            emptyLabel.setStyle("-fx-text-fill: #aaaaaa; -fx-font-style: italic;");
            queueContainer.getChildren().add(emptyLabel);
        } else {
            // Visualize queue items
            // For now, just a list of IDs
            Label itemLabel = new Label("Órdenes en cola: " + queue.size()); // Placeholder
            itemLabel.setStyle("-fx-text-fill: white;");
            queueContainer.getChildren().add(itemLabel);
        }
    }
}
