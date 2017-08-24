package com.example.poste.trouvemoi;

/**
 * Created by Poste on 20/12/2015.
 */
public class Joueur {
    private static Joueur joueur=null;
    private String nom;
    private String playerId;
    private Joueur(){

    }
    public static Joueur getInstance(){
        if(joueur==null)joueur=new Joueur();
        return joueur;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }
}
