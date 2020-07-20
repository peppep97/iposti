package com.tecnovajet.iposti.lastminute;

public class LastMinute {

    private int idStruttura;
    private String nomeStruttura;
    private String tipologia;
    private int idLastMinute;
    private String ora;
    private String giorno;
    private int idServizio;
    private double newPrezzo;
    private String descrizioneLastminute;
    private String nomeServizio;
    private String descrizioneServizio;
    private double prezzo;
    private String categoria;
    private double distanza;
    private int preferito;
    private String indirizzo;

    public LastMinute(int idStruttura, String nomeStruttura, String tipologia, int idLastMinute, String ora, String giorno, int idServizio, double newPrezzo, String descrizioneLastminute, String nomeServizio, String descrizioneServizio, double prezzo, String categoria, double distanza, int preferito) {
        this.idStruttura = idStruttura;
        this.nomeStruttura = nomeStruttura;
        this.tipologia = tipologia;
        this.idLastMinute = idLastMinute;
        this.ora = ora;
        this.giorno = giorno;
        this.idServizio = idServizio;
        this.newPrezzo = newPrezzo;
        this.descrizioneLastminute = descrizioneLastminute;
        this.nomeServizio = nomeServizio;
        this.descrizioneServizio = descrizioneServizio;
        this.prezzo = prezzo;
        this.categoria = categoria;
        this.distanza = distanza;
        this.preferito = preferito;
    }

    public LastMinute(int idStruttura, String nomeStruttura, String tipologia, String indirizzo, int idLastMinute, String ora, String giorno, int idServizio, double newPrezzo, String descrizioneLastminute, String nomeServizio, String descrizioneServizio, double prezzo, String categoria) {
        this.idStruttura = idStruttura;
        this.nomeStruttura = nomeStruttura;
        this.tipologia = tipologia;
        this.idLastMinute = idLastMinute;
        this.ora = ora;
        this.giorno = giorno;
        this.idServizio = idServizio;
        this.newPrezzo = newPrezzo;
        this.descrizioneLastminute = descrizioneLastminute;
        this.nomeServizio = nomeServizio;
        this.descrizioneServizio = descrizioneServizio;
        this.prezzo = prezzo;
        this.categoria = categoria;
        this.indirizzo = indirizzo;
    }

    public LastMinute(int idLastMinute, String ora, String giorno, int idServizio, double newPrezzo, String descrizioneLastminute, String nomeServizio, String descrizioneServizio, double prezzo, String categoria) {
        this.idLastMinute = idLastMinute;
        this.ora = ora;
        this.giorno = giorno;
        this.idServizio = idServizio;
        this.newPrezzo = newPrezzo;
        this.descrizioneLastminute = descrizioneLastminute;
        this.nomeServizio = nomeServizio;
        this.descrizioneServizio = descrizioneServizio;
        this.prezzo = prezzo;
        this.categoria = categoria;
    }


    public int getIdStruttura() {
        return idStruttura;
    }

    public String getNomeStruttura() {
        return nomeStruttura;
    }

    public String getTipologia() {
        return tipologia;
    }

    public int getIdLastMinute() {
        return idLastMinute;
    }

    public String getOra() {
        return ora;
    }

    public String getGiorno() {
        return giorno;
    }

    public int getIdServizio() {
        return idServizio;
    }

    public double getNewPrezzo() {
        return newPrezzo;
    }

    public String getDescrizioneLastminute() {
        return descrizioneLastminute;
    }

    public String getNomeServizio() {
        return nomeServizio;
    }

    public String getDescrizioneServizio() {
        return descrizioneServizio;
    }

    public double getPrezzo() {
        return prezzo;
    }

    public String getCategoria() {
        return categoria;
    }

    public double getDistanza() {
        return distanza;
    }

    public int getPreferito() {
        return preferito;
    }

    public String getIndirizzo() {
        return indirizzo;
    }
}
