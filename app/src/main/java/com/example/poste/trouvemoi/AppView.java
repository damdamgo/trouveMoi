package com.example.poste.trouvemoi;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.nio.BufferUnderflowException;

/**
 * Created by Poste on 20/12/2015.
 */
public class AppView {
    private static AppView appView=null;
    public static Activity activity;
    private Context context;
    private RelativeLayout conteneur;
    private AppController appController;
    private int heightCardProposition;
    private int heightButtonProposition;
    private AppView(){

    }
    public static AppView getInstance(){
        if(appView==null)appView=new AppView();
        return appView;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
        conteneur = (RelativeLayout) activity.findViewById(R.id.conteneurApp);
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }


    public void diplayMessage(String message) {
        CardView card = (CardView) activity.getLayoutInflater().inflate(R.layout.message,conteneur,false);
        ((TextView)card.findViewById(R.id.message)).setText(message);
        conteneur.removeAllViews();
        conteneur.addView(card);
    }

    public void displayName() {
        appController=AppController.getInstance();
        CardView card = (CardView) activity.getLayoutInflater().inflate(R.layout.nom, conteneur, false);
        conteneur.removeAllViews();
        conteneur.addView(card);
        ((Button)activity.findViewById(R.id.buttonSendPseudo)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pseudo = ((EditText) activity.findViewById(R.id.editTextPseudo)).getText().toString();
                appController.sendPseudo(pseudo);
                conteneur.removeAllViews();
            }
        });
    }

    public void diplayGames(String jeu) {
        conteneur.removeAllViews();
        CardView card = (CardView)activity.getLayoutInflater().inflate(R.layout.choix_jeu,conteneur,false);
        Button jeuB = (Button) card.findViewById(R.id.buttonJeuChoix);
        jeuB.setText(jeu);
        conteneur.addView(card);
        jeuB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String jeu = ((Button) v).getText().toString();
                conteneur.removeAllViews();
                appController.choixJeuMessage(jeu);
            }
        });
    }

    public void displayViewJeuLibre(final int temps) {
        conteneur.removeAllViews();
        LinearLayout layout = (LinearLayout)activity.getLayoutInflater().inflate(R.layout.jeu_libre,conteneur,false);
        Button b = (Button) layout.findViewById(R.id.buttonSendReponse);
        conteneur.addView(layout);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reponse = ((EditText) activity.findViewById(R.id.editTextReponse)).getText().toString();
                appController.sendAnswerJeuLibre(reponse);
                conteneur.removeAllViews();
            }
        });
        new Thread(new Runnable() {
            public int time;
            public void run() {
                time = temps;
                final TextView text = (TextView) activity.findViewById(R.id.textViewTemps);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                while(time!=0){
                    try {
                        Thread.sleep(1000);
                        text.post(new Runnable() {
                            public void run() {
                                text.setText(String.valueOf(time));
                            }
                        });
                        time--;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                conteneur.post(new Runnable() {
                    public void run() {
                        conteneur.removeAllViews();
                    }
                });
            }
        }).start();
    }

    public void afficherProposition(String proposition,String premiereLettre){
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        heightCardProposition = size.y;
        ///initialise vue pour proposition
        conteneur.removeAllViews();
       final com.example.poste.trouvemoi.LayoutProposition layout = (com.example.poste.trouvemoi.LayoutProposition)activity.getLayoutInflater().inflate(R.layout.layout_proposition,conteneur, false);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layout.getLayoutParams();
        params.topMargin=heightCardProposition;
        layout.setLayoutParams(params);
        ((TextView)layout.findViewById(R.id.textViewPremiereLettre)).setText(premiereLettre);
        ((TextView)layout.findViewById(R.id.textViewProposition)).setText(proposition);
        conteneur.addView(layout);


        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        heightButtonProposition = Math.round(100 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
       final RelativeLayout layoutButton = (RelativeLayout)activity.getLayoutInflater().inflate(R.layout.layout_proposition_button, conteneur, false);
        params = (RelativeLayout.LayoutParams) layoutButton.getLayoutParams();
        params.bottomMargin=-heightButtonProposition;
        layoutButton.setLayoutParams(params);
        conteneur.addView(layoutButton);

        //initialise clique sur bouton
        RelativeLayout r = (RelativeLayout)(activity.findViewById(R.id.layoutRefuserButton));
        r.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Display display = AppView.activity.getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int width = -size.x;
                com.example.poste.trouvemoi.LayoutProposition layout = (com.example.poste.trouvemoi.LayoutProposition) activity.findViewById(R.id.viewProposition);
                AnimatorSet animSetXY = new AnimatorSet();
                ObjectAnimator x = ObjectAnimator.ofFloat(layout,
                        "x", layout.getX(), width - 100);
                animSetXY.playTogether(x);
                animSetXY.setDuration(100);
                animSetXY.start();

                appController.sendReponseVerification("fausse");
            }
        });
        r = (RelativeLayout)(activity.findViewById(R.id.layoutAccepterButton));
        r.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Display display = AppView.activity.getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int width = size.x;
                com.example.poste.trouvemoi.LayoutProposition layout = (com.example.poste.trouvemoi.LayoutProposition) activity.findViewById(R.id.viewProposition);
                AnimatorSet animSetXY = new AnimatorSet();
                ObjectAnimator x = ObjectAnimator.ofFloat(layout,
                        "x", layout.getX(), width + 100);
                animSetXY.playTogether(x);
                animSetXY.setDuration(100);
                animSetXY.start();
                appController.sendReponseVerification("bonne");
            }
        });

        //animation apparition
        Animation a = new Animation() {

            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layout.getLayoutParams();
                params.topMargin = (int) (heightCardProposition-(interpolatedTime *heightCardProposition))+50;
                layout.setLayoutParams(params);
            }
        };
        a.setDuration(800); // in ms
        layout.startAnimation(a);

        Animation b = new Animation() {

            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layoutButton.getLayoutParams();
                params.bottomMargin = (int) (-heightButtonProposition+(interpolatedTime *heightButtonProposition));
                layoutButton.setLayoutParams(params);
            }
        };
        b.setDuration(800); // in ms
        layoutButton.startAnimation(b);
    }

}
