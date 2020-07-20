package com.tecnovajet.iposti.booking;

public class BookingModel {

    private String oraInizio, giorno, nome, categoria, nomeStruttura;
    private int stato, id, cancellazione;
    private double prezzo;

    public BookingModel(int id, String oraInizio, String giorno, String nome, String categoria, String nomeStruttura, int stato, double prezzo, int cancellazione) {
        this.oraInizio = oraInizio;
        this.giorno = giorno;
        this.nome = nome;
        this.categoria = categoria;
        this.nomeStruttura = nomeStruttura;
        this.stato = stato;
        this.prezzo = prezzo;
        this.id = id;
        this.cancellazione = cancellazione;
    }

    public String getNomeStruttura() {
        return nomeStruttura;
    }

    public String getNome() {
        return nome;
    }

    public double getPrezzo() {
        return prezzo;
    }

    public int getStato() {
        return stato;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getGiorno() {
        return giorno;
    }

    public String getOraInizio() {
        return oraInizio;
    }

    public int getId() {
        return id;
    }

    public void setStato(int stato) {
        this.stato = stato;
    }

    public int getCancellazione() {
        return cancellazione;
    }

    public void setCancellazione(int cancellazione) {
        this.cancellazione = cancellazione;
    }
}
