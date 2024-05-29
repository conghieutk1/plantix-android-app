package com.plantix;

public class Prediction {
    private int id;
    private String name;
    private double prob;
    public Prediction(int id, String name, double prob) {
        this.id = id;
        this.name = name;
        this.prob = prob;
    }

    // Các phương thức getter và setter
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

    public double getProb() {
        return prob;
    }

    public void setProb(double prob) {
        this.prob = prob;
    }
}
