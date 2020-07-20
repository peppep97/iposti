package com.tecnovajet.iposti.facilities;

import java.util.ArrayList;
import java.util.List;

public class GroupItem{
    String title;
    List<Servizio> childServizi = new ArrayList<>();

    public GroupItem(String title){
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void addServizio(Servizio s){
        childServizi.add(s);
    }

    public List<Servizio> getServizi(){
        return childServizi;
    }
}