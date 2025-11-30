package com.capdocs.datastructures;

import com.capdocs.model.Order;
import java.util.LinkedList;
import java.util.Queue;

/**
 * ACADEMIC REQUIREMENT: QUEUE (Colas)
 * Manages Pending Orders for production (FIFO).
 */
public class ProductionQueue {

    private final Queue<Order> queue = new LinkedList<>();

    public void enqueue(Order order) {
        queue.offer(order);
    }

    public Order dequeue() {
        return queue.poll();
    }

    public Order peek() {
        return queue.peek();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public int size() {
        return queue.size();
    }

    public void clear() {
        queue.clear();
    }

    public Queue<Order> getAll() {
        return new LinkedList<>(queue);
    }
}
