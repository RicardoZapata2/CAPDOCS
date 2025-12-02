package com.capdocs.view;

import com.capdocs.dao.OrderDAO;
import com.capdocs.dao.TransactionDAO;
import com.capdocs.model.Transaction;
import com.capdocs.util.AlertHelper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FinanceView extends BorderPane {

    private final TransactionDAO transactionDAO;
    private final OrderDAO orderDAO;
    private final TableView<Transaction> transactionTable;
    private final ObservableList<Transaction> transactionList;

    // KPI Labels
    private Label totalIncomeLabel;
    private Label totalItemsLabel;
    private DatePicker datePicker;

    public FinanceView() {
        this.transactionDAO = new TransactionDAO();
        this.orderDAO = new OrderDAO();
        this.transactionList = FXCollections.observableArrayList();
        this.transactionTable = new TableView<>();

        initUI();
        loadData();
    }

    private void initUI() {
        setPadding(new Insets(20));
        setStyle("-fx-background-color: #1e1e1e;");

        // Header
        Label titleLabel = new Label("Finanzas y Reportes");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");

        datePicker = new DatePicker(LocalDate.now());
        datePicker.setOnAction(e -> loadData());

        Button closeDayBtn = new Button("Cierre Diario");
        closeDayBtn.getStyleClass().add("button-primary");
        closeDayBtn.setOnAction(e -> showDailyReport());

        HBox header = new HBox(20, titleLabel, new Region(), new Label("Fecha:"), datePicker, closeDayBtn);
        header.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(header.getChildren().get(2), Priority.ALWAYS);
        header.getChildren().get(3).setStyle("-fx-text-fill: white;");
        setTop(header);

        // KPI Cards
        HBox kpiBox = new HBox(20);
        kpiBox.setPadding(new Insets(20, 0, 20, 0));
        kpiBox.setAlignment(Pos.CENTER_LEFT);

        totalIncomeLabel = createKPICard(kpiBox, "Ingresos del Día", "$0.00", "#2ecc71");
        totalItemsLabel = createKPICard(kpiBox, "Prendas Vendidas", "0", "#3498db");

        // Table
        transactionTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        transactionTable.setStyle("-fx-background-color: transparent;");

        TableColumn<Transaction, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Transaction, String> typeCol = new TableColumn<>("Tipo");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));

        TableColumn<Transaction, Double> amountCol = new TableColumn<>("Monto");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));

        TableColumn<Transaction, String> dateCol = new TableColumn<>("Fecha/Hora");
        dateCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getDate().format(DateTimeFormatter.ofPattern("HH:mm:ss"))));

        transactionTable.getColumns().addAll(idCol, typeCol, amountCol, dateCol);
        transactionTable.setItems(transactionList);

        VBox centerBox = new VBox(10, kpiBox, transactionTable);
        setCenter(centerBox);
    }

    private Label createKPICard(HBox container, String title, String initialValue, String colorHex) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle(
                "-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");
        card.setPrefWidth(200);

        Label titleLbl = new Label(title);
        titleLbl.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 14px;");

        Label valueLbl = new Label(initialValue);
        valueLbl.setStyle("-fx-text-fill: " + colorHex + "; -fx-font-size: 24px; -fx-font-weight: bold;");

        card.getChildren().addAll(titleLbl, valueLbl);
        container.getChildren().add(card);
        return valueLbl;
    }

    private void loadData() {
        LocalDate selectedDate = datePicker.getValue();
        if (selectedDate == null)
            return;

        // Load Transactions
        List<Transaction> transactions = transactionDAO.findByDateRange(selectedDate, selectedDate);
        transactionList.setAll(transactions);

        // Calculate Totals
        double totalIncome = transactions.stream()
                .filter(t -> t.getType() == Transaction.Type.INCOME)
                .mapToDouble(Transaction::getAmount)
                .sum();

        totalIncomeLabel.setText("$" + String.format("%.2f", totalIncome));

        // Load Items Sold
        int itemsSold = orderDAO.countItemsSoldByDate(selectedDate);
        totalItemsLabel.setText(String.valueOf(itemsSold));
    }

    private void showDailyReport() {
        LocalDate date = datePicker.getValue();
        double income = Double.parseDouble(totalIncomeLabel.getText().replace("$", "").replace(",", "."));
        int items = Integer.parseInt(totalItemsLabel.getText());

        StringBuilder report = new StringBuilder();
        report.append("=== REPORTE DIARIO ===\n");
        report.append("Fecha: ").append(date).append("\n\n");
        report.append("Total Ingresos: $").append(income).append("\n");
        report.append("Prendas Vendidas: ").append(items).append("\n");
        report.append("Transacciones: ").append(transactionList.size()).append("\n");
        report.append("\nEstado: Cierre Exitoso");

        AlertHelper.showInfo("Cierre Diario", report.toString());
    }
}
