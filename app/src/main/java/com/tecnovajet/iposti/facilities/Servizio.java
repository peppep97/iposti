package com.tecnovajet.iposti.facilities;

import java.io.Serializable;

public class Servizio implements Serializable {

    private int idStruttura;
    private int idServizio;
    private String nome;
    private String descrizione;
    private double prezzo;
    private String durata;
    private String categoria;
    private String nomeStruttura;

    public Servizio(int idStruttura, int idServizio, String nome, String descrizione, double prezzo, String durata, String categoria, String nomeStruttura) {
        this.idStruttura = idStruttura;
        this.idServizio = idServizio;
        this.nome = nome;
        this.descrizione = descrizione;
        this.prezzo = prezzo;
        this.durata = durata;
        this.categoria = categoria;
        this.nomeStruttura = nomeStruttura;
    }

    public int getIdStruttura() {
        return idStruttura;
    }

    public int getIdServizio() {
        return idServizio;
    }

    public String getNome() {
        return nome;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public double getPrezzo() {
        return prezzo;
    }

    public String getDurata() {
        return durata;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getNomeStruttura() {
        return nomeStruttura;
    }
}
