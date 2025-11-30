package com.capdocs.controller;

import com.capdocs.dao.OrderDAO;
import com.capdocs.dao.TransactionDAO;
import com.capdocs.model.Order;
import com.capdocs.model.Transaction;
import com.capdocs.util.Session;
import com.capdocs.view.DashboardView;
import com.capdocs.view.MainLayout;
import com.capdocs.model.User;

import java.util.List;

public class MainController {

    private final MainLayout layout;
    private final OrderDAO orderDAO;
    private final TransactionDAO transactionDAO;
    private final Runnable logoutHandler;

    public MainController(MainLayout layout, Runnable logoutHandler) {
        this.layout = layout;
        this.logoutHandler = logoutHandler;
        this.orderDAO = new OrderDAO();
        this.transactionDAO = new TransactionDAO();

        // Set the logout action in the layout's configuration menu
        this.layout.setLogoutAction(this::logout);

        // Initialize Dashboard (Home) View
        showDashboard();
    }

    private void showDashboard() {
        DashboardView dashboard = new DashboardView();

        // Load Data
        List<Order> pendingOrders = orderDAO.findAllPending();
        List<Transaction> todayTransactions = transactionDAO.findAllToday();

        double totalSales = todayTransactions.stream()
                .filter(t -> t.getType() == Transaction.Type.INCOME)
                .mapToDouble(Transaction::getAmount)
                .sum();

        dashboard.getPendingOrdersLabel().setText(String.valueOf(pendingOrders.size()));
        dashboard.getDailySalesLabel().setText(String.format("$%.2f", totalSales));

        layout.setCenter(dashboard, "Resumen General");
    }

    private void logout() {
        Session.setCurrentUser(null);
        if (logoutHandler != null) {
            logoutHandler.run();
        }
    }

    public MainLayout getLayout() {
        return layout;
    }
}
