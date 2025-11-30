package com.capdocs.datastructures;

import javafx.scene.Parent;
import java.util.Stack;

/**
 * ACADEMIC REQUIREMENT: STACK (Pilas)
 * Manages UI navigation history.
 */
public class NavigationStack {

    private final Stack<Parent> history = new Stack<>();

    public void push(Parent view) {
        history.push(view);
    }

    public Parent pop() {
        if (history.isEmpty()) {
            return null;
        }
        return history.pop();
    }

    public Parent peek() {
        if (history.isEmpty()) {
            return null;
        }
        return history.peek();
    }

    public boolean isEmpty() {
        return history.isEmpty();
    }

    public void clear() {
        history.clear();
    }
}
