package com.capdocs.model;

public class Technique {
    private int id;
    private String name;
    private String description;
    private double baseCost;

    public Technique() {
    }

    public Technique(int id, String name, String description, double baseCost) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.baseCost = baseCost;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getBaseCost() {
        return baseCost;
    }

    public void setBaseCost(double baseCost) {
        this.baseCost = baseCost;
    }

    @Override
    public String toString() {
        return name;
    }
}
