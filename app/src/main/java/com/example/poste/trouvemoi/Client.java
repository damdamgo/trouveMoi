package com.example.poste.trouvemoi;

import android.util.Log;

import com.google.android.gms.cast.games.GameManagerClient;
import com.google.android.gms.cast.games.GameManagerState;
import com.google.android.gms.common.api.ResultCallback;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Poste on 20/12/2015.
 */
public class Client implements ResultCallback<GameManagerClient.GameManagerInstanceResult> {
    private static Client client=null;
    private GameManagerClient mGameManagerClient;
    private AppController appController;
    private Client(){

    }

    public static Client getInstance(){
        if(client==null)client=new Client();
        return client;
    }

    @Override
    public void onResult(GameManagerClient.GameManagerInstanceResult gameManagerInstanceResult) {
        //permet de recuprer l'instance de game manager client
        mGameManagerClient = gameManagerInstanceResult.getGameManagerClient();
        //permet de lier a l'instance un listener
        gameManagerInstanceResult.getGameManagerClient().setListener(new GameManagerClient.Listener() {
            @Override
            public void onStateChanged(GameManagerState gameManagerState, GameManagerState gameManagerState1) {

            }

            @Override
            public void onGameMessageReceived(String s, JSONObject jsonObject) {
                if(jsonObject.has("jeuxPossibles")){
                    JSONObject object = null ;
                    try {
                        object = jsonObject.getJSONObject("jeuxPossibles");
                        gestionJeuxPossibles(object);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                else if(jsonObject.has("jeu")){
                    try {
                        JSONObject object = jsonObject.getJSONObject("jeu");
                        gestionJeu(object);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else if(jsonObject.has("verification")){
                    try {
                        JSONObject object = jsonObject.getJSONObject("verification");
                        gestionVerification(object);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        appController=AppController.getInstance();
        this.sendAvailable();
        appController.displayName();
    }

    public void sendAvailable(){
        JSONObject extraMessageData = new JSONObject();
        mGameManagerClient.sendPlayerAvailableRequest(extraMessageData).setResultCallback(
                new ResultCallback<GameManagerClient.GameManagerResult>() {
                    @Override
                    public void onResult(GameManagerClient.GameManagerResult gameManagerResult) {
                        appController.setIdPLayer(gameManagerResult.getPlayerId());
                    }
                });
    }

    public void sendReady(String pseudo) {
        JSONObject extraMessageData = new JSONObject();
        try {
            extraMessageData.put("pseudo", pseudo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mGameManagerClient.sendPlayerReadyRequest(extraMessageData).setResultCallback(
                new ResultCallback<GameManagerClient.GameManagerResult>() {
                    @Override
                    public void onResult(GameManagerClient.GameManagerResult gameManagerResult) {
                        appController.setIdPLayer(gameManagerResult.getPlayerId());
                    }
                });
        mGameManagerClient.sendPlayerPlayingRequest(extraMessageData).setResultCallback(
                new ResultCallback<GameManagerClient.GameManagerResult>() {
                    @Override
                    public void onResult(GameManagerClient.GameManagerResult gameManagerResult) {
                    }
        });
    }








    public void gestionJeuxPossibles(JSONObject jsonObject){
        try {
            String jeu = jsonObject.getString("jeu");
            Log.w("ee-----------------",jeu);
            appController.displayGameChoices(jeu);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void gestionJeu(JSONObject jsonObject){
        try {
            if(jsonObject.getString("type").equals("libre")){
                appController.commencerJeuLibre();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void gestionVerification(JSONObject jsonObject){
        try {
                appController.verificationProposition(jsonObject.getString("proposition"),jsonObject.getString("premiereLettre"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }




    public void sendChoixJeu(String jeu) {
        JSONObject extraMessageData = new JSONObject();
        try {
            extraMessageData.put("jeu", jeu);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mGameManagerClient.sendGameMessage(appController.getIdPlayer(),extraMessageData);
    }

    public void sendAnswerJeuLibre(String reponse) {
        JSONObject extraMessageData = new JSONObject();
        try {
            extraMessageData.put("reponseJeuLibre",reponse);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mGameManagerClient.sendGameMessage(appController.getIdPlayer(),extraMessageData);
    }

    public void sendReponseVerification(String reponse){
        JSONObject extraMessageData = new JSONObject();
        try {
            extraMessageData.put("propositionVerification",reponse);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mGameManagerClient.sendGameMessage(appController.getIdPlayer(),extraMessageData);
    }
}
