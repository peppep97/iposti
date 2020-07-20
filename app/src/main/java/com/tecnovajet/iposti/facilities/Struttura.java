package com.tecnovajet.iposti.facilities;

import java.io.Serializable;

public class Struttura implements Serializable {

    private int id;
    private String nome;
    private String descrizione;
    private String via;
    private String numero;
    private String cap;
    private String citta;
    private String tipologia;
    private double distance;
    private double lat;
    private double lon;
    private int preferito;

    public Struttura(int id, String nome, String tipologia, double distance, int preferito) {
        this.id = id;
        this.nome = nome;
        this.tipologia = tipologia;
        this.distance = distance;
        this.preferito = preferito;
    }

    public Struttura(int id, String nome, String descrizione, String via, String numero, String cap, String citta, String tipologia, double lat, double lon, int preferito) {
        this.id = id;
        this.nome = nome;
        this.descrizione = descrizione;
        this.via = via;
        this.numero = numero;
        this.cap = cap;
        this.citta = citta;
        this.tipologia = tipologia;
        this.lat = lat;
        this.lon = lon;
        this.preferito = preferito;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getTipologia() {
        return tipologia;
    }

    public double getDistance() {
        return distance;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public String getVia() {
        return via;
    }

    public String getNumero() {
        return numero;
    }

    public String getCap() {
        return cap;
    }

    public String getCitta() {
        return citta;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public int getPreferito() {
        return preferito;
    }
}
