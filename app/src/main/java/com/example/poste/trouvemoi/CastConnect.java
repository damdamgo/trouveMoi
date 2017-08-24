package com.example.poste.trouvemoi;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.media.MediaRouter;
import android.util.Log;

import com.google.android.gms.cast.ApplicationMetadata;
import com.google.android.gms.cast.Cast;
import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.games.GameManagerClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.io.IOException;

/**
 * Created by Poste on 26/11/2015.
 */
//permet de gerer la connexion avec la chromecast
public class CastConnect extends MediaRouter.Callback {
    private CastDevice mSelectedDevice;
    private Cast.Listener mCastClientListener;
    private GoogleApiClient.ConnectionCallbacks mConnectionCallbacks;
    private GoogleApiClient.OnConnectionFailedListener mConnectionFailedListener;
    private GoogleApiClient mApiClient;
    private Activity activity;
    private Client client;
    private String sessionId;
    private String applicationStatus;
    private ApplicationMetadata applicationMetadata;
    public boolean mWaitingForReconnect = false;
    private boolean mApplicationStarted = false;
    private static CastConnect castConnect=null;

    private CastConnect(){
        client=Client.getInstance();
    }

    //permet de recuprer l'instance du castConnect
    public static CastConnect getInstance(){
        if(castConnect==null)castConnect=new CastConnect();
        return castConnect;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    //quand on selectionne la chromecast
    @Override
    public void onRouteSelected(MediaRouter router, MediaRouter.RouteInfo info) {
        mSelectedDevice = CastDevice.getFromBundle(info.getExtras());
        String routeId = info.getId();
        //permet de lancer le receiver apres que l'on sache la cast device qui a ete selectionne
        launchReceiver();
    }
    //qiuand on deselectionne la chromecast
    @Override
    public void onRouteUnselected(MediaRouter router, MediaRouter.RouteInfo info) {

        mSelectedDevice = null;
    }

    //permet de lancer le receiver
    public void launchReceiver(){

        //permet de gerer le status pendant que le sender est relie au receiver informe le sender sur le receiver
        mCastClientListener = new Cast.Listener() {
            @Override
            public void onApplicationStatusChanged() {
                if (mApiClient != null) {
                    Log.w("erze", "onApplicationStatusChanged: "
                            + Cast.CastApi.getApplicationStatus(mApiClient));
                }
            }

            @Override
            public void onVolumeChanged() {
                if (mApiClient != null) {
                    Log.w("erze", "onVolumeChanged: " + Cast.CastApi.getVolume(mApiClient));
                }
            }

            @Override
            public void onApplicationDisconnected(int errorCode) {
                //teardown();
            }
        };

        //relie la chrome cast au client listener qui recupere le statut
        Cast.CastOptions.Builder apiOptionsBuilder = Cast.CastOptions
                .builder(mSelectedDevice, mCastClientListener);

        //initialise les classes callback
        mConnectionCallbacks=new ConnectionCallbacks() ;
        mConnectionFailedListener=new ConnectionFailedListener();

        //on cree une instance googleapiclient en liant les classes callback
        mApiClient = new GoogleApiClient.Builder(activity)
                .addApi(Cast.API, apiOptionsBuilder.build())
                .addConnectionCallbacks(mConnectionCallbacks)
                .addOnConnectionFailedListener(mConnectionFailedListener)
                .build();
        //on connect en utilisant une instance d'apiclient de google
        mApiClient.connect();
    }


    private class ConnectionCallbacks implements
            GoogleApiClient.ConnectionCallbacks {

        @Override
        public void onConnected(Bundle connectionHint) {

            if (mWaitingForReconnect) {
                mWaitingForReconnect = false;
                //reconnectChannels();
            } else {
                try {
                    //on lance l'aaplication
                    Cast.CastApi.launchApplication(mApiClient, "BC343A35", false)
                            .setResultCallback(
                                    new ResultCallback<Cast.ApplicationConnectionResult>() {
                                        @Override
                                        public void onResult(Cast.ApplicationConnectionResult result) {
                                            Status status = result.getStatus();

                                            if (status.isSuccess()) {
                                                mApplicationStarted=true;
                                                 applicationMetadata =
                                                        result.getApplicationMetadata();
                                                sessionId = result.getSessionId();
                                                applicationStatus = result.getApplicationStatus();
                                                boolean wasLaunched = result.getWasLaunched();
                                                //lance le game manager
                                                GameManagerClient.getInstanceFor(mApiClient, sessionId)
                                                        .setResultCallback(client);
                                            } else {
                                                teardown();
                                            }
                                        }
                                    });

                } catch (Exception e) {
                    Log.w("ez", "Failed to launch application", e);
                }
            }
        }

        @Override
        public void onConnectionSuspended(int cause) {
            mWaitingForReconnect = true;
        }
    }

    //si la connection faile
    private class ConnectionFailedListener implements
            GoogleApiClient.OnConnectionFailedListener {
        @Override
        public void onConnectionFailed(ConnectionResult result) {
            teardown();
        }
    }
    //si jamais perd la connexion
    private void teardown() {
        if (mApiClient != null) {
            if (mApplicationStarted) {
                if (mApiClient.isConnected() || mApiClient.isConnecting()) {
                    Cast.CastApi.stopApplication(mApiClient, sessionId);
                    //voir https://developers.google.com/cast/docs/android_sender teardonw()
                    mApiClient.disconnect();
                }
                mApplicationStarted = false;
            }
            mApiClient = null;
        }
        mSelectedDevice = null;
        mWaitingForReconnect = false;
        sessionId = null;
    }
}
