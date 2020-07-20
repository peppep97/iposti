package com.tecnovajet.iposti.booking;

public class HourModel {

    private String hour;
    private double price;
    private boolean selected;

    public HourModel(String hour, double price) {
        this.hour = hour;
        this.price = price;
    }

    public HourModel(String hour, double price, boolean selected) {
        this.hour = hour;
        this.price = price;
        this.selected = selected;
    }

    public String getHour() {
        return hour;
    }

    public double getPrice() {
        return price;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
