package com.tecnovajet.iposti.booking;

public class DipendenteModel {

    private int id;
    private String name;
    private boolean selected;

    public DipendenteModel(int id, String name, boolean selected) {
        this.id = id;
        this.name = name;
        this.selected = selected;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
