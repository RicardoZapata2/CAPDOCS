package com.capdocs.view;

import com.capdocs.datastructures.ProductionQueue;
import com.capdocs.model.Order;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class DashboardView extends BorderPane {

    private final ProductionQueue productionQueue;

    public DashboardView() {
        this.productionQueue = ProductionQueue.getInstance();
        initUI();
    }

    private void initUI() {
        setPadding(new Insets(20));

        // Title
        Label titleLabel = new Label("Dashboard");
        titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        setTop(titleLabel);

        // KPIs
        HBox kpiBox = new HBox(20);
        kpiBox.setPadding(new Insets(20, 0, 20, 0));
        kpiBox.setAlignment(Pos.CENTER);

        kpiBox.getChildren().addAll(
                createCard("Ventas del Día", "$1,250.00"),
                createCard("Producciones Pendientes", String.valueOf(productionQueue.size())),
                createCard("Ganancia Neta", "$450.00"));

        // Production Queue (FIFO)
        VBox queueBox = new VBox(10);
        queueBox.setPadding(new Insets(20, 0, 0, 0));

        Label queueLabel = new Label("Cola de Producción (FIFO)");
        queueLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        ListView<String> queueList = new ListView<>();
        // Mock data if empty for visualization
        if (productionQueue.isEmpty()) {
            queueList.getItems().add("No hay órdenes pendientes.");
        } else {
            for (Order o : productionQueue.getQueue()) {
                queueList.getItems().add("Orden #" + o.getId() + " - " + o.getStatus());
            }
        }

        queueBox.getChildren().addAll(queueLabel, queueList);

        VBox centerBox = new VBox(kpiBox, queueBox);
        setCenter(centerBox);
    }

    private VBox createCard(String title, String value) {
        VBox card = new VBox(10);
        card.getStyleClass().add("card");
        card.setPrefWidth(200);
        card.setAlignment(Pos.CENTER);

        Label titleLbl = new Label(title);
        titleLbl.getStyleClass().add("card-title");

        Label valueLbl = new Label(value);
        valueLbl.getStyleClass().add("card-value");

        card.getChildren().addAll(titleLbl, valueLbl);
        return card;
    }
}
