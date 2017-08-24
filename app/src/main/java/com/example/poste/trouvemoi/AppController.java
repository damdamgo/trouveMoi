package com.example.poste.trouvemoi;

import android.app.Activity;
import android.content.Context;

/**
 * Created by Poste on 21/12/2015.
 */
public class AppController {
    private static AppController appController=null;
    private AppView appView;
    private Joueur joueur;
    private Client client;
    private AppController(){
        appView = AppView.getInstance();
        joueur = Joueur.getInstance();
        client = Client.getInstance();
    }

    public static AppController getInstance(){
        if(appController==null)appController=new AppController();
        return appController;
    }
    ////////////////////////////////////////////////////////////vue
    public void diplayMessage(String message){
        appView.diplayMessage(message);
    }

    public void displayName() {
        appView.displayName();
    }


    /////////////////////////////////////////////////////////Joueur
    public void setIdPLayer(String playerId) {
        joueur.setPlayerId(playerId);
    }

    public String getIdPlayer(){
        return joueur.getPlayerId();
    }

    public void sendPseudo(String pseudo) {
       client.sendReady(pseudo);
    }


    /////////////////////////////////////////////////////gestion choix jeu
    public void displayGameChoices(String jeu) {
        appView.diplayGames(jeu);
    }

    public void choixJeuMessage(String jeu) {
        client.sendChoixJeu(jeu);
    }

    ///////////////////////////////////////gestion choix jeu libre cela veut dire pas de proposition
    public void commencerJeuLibre() {
        appView.displayViewJeuLibre(10);
    }

    public void sendAnswerJeuLibre(String reponse) {
        client.sendAnswerJeuLibre(reponse);
    }

    /////////////////////////////////////////////////////gestion verification
    public void verificationProposition(String proposition, String premiereLettre) {
        appView.afficherProposition(proposition,premiereLettre);
    }

    public void sendReponseVerification(String reponse){
        client.sendReponseVerification(reponse);
    }
}
