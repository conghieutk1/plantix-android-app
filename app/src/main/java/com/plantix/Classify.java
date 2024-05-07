package com.plantix;
import java.util.List;
public class Classify {
    private String message;
    private List<PredictionData> data;

    public Classify(String message, List<PredictionData> data) {
        this.message = message;
        this.data = data;
    }

    // Getters and setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<PredictionData> getData() {
        return data;
    }

    public void setData(List<PredictionData> data) {
        this.data = data;
    }

    // Inner class representing each prediction data
    public static class PredictionData {
        private int id;
        private String name;
        private double prob;

        // Getters and setters
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
}
