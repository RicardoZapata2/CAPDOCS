package com.capdocs.datastructures;

import com.capdocs.model.Order;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Cola de Producción (FIFO).
 * Requisito académico: Uso de Colas.
 */
public class ProductionQueue {

    private static ProductionQueue instance;
    private final Queue<Order> queue;

    private ProductionQueue() {
        this.queue = new LinkedList<>();
    }

    public static synchronized ProductionQueue getInstance() {
        if (instance == null) {
            instance = new ProductionQueue();
        }
        return instance;
    }

    public void addOrder(Order order) {
        queue.offer(order);
    }

    public Order getNextOrder() {
        return queue.poll();
    }

    public Order peekNextOrder() {
        return queue.peek();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public int size() {
        return queue.size();
    }

    public Queue<Order> getQueue() {
        return queue;
    }
}
