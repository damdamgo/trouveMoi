package com.example.poste.trouvemoi;


import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.MediaRouteActionProvider;
import android.support.v7.media.MediaRouteSelector;
import android.support.v7.media.MediaRouter;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;


import com.google.android.gms.cast.CastMediaControlIntent;

public class MainActivity extends AppCompatActivity {
    private MediaRouteSelector mMediaRouteSelector;
    private MediaRouter mMediaRouter;
    private MediaRouter.Callback mMediaRouterCallback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //recupere instance de MediaRouter
        mMediaRouter = MediaRouter.getInstance(getApplicationContext());

        //permet d'associer le receiver a lancer avec le sender
        mMediaRouteSelector = new MediaRouteSelector.Builder()
                .addControlCategory(CastMediaControlIntent.categoryForCast("BC343A35"))
                .build();

        //permet de recuperer l'instance du mediarouteurcallback
        mMediaRouterCallback=CastConnect.getInstance();

        ((CastConnect)mMediaRouterCallback).setActivity(this);

        //permet a la classe qui gerer ce qui est afficher de le faire sur l'activite actuelle
        AppView appView = AppView.getInstance();
        appView.setActivity(this);
        appView.setContext(this.getApplicationContext());
        //permet d'afficher un message
        AppController.getInstance().diplayMessage("Connectez-vous à votre chromecast");



    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splash, menu);
        //associer le media router au bouton du cast
        MenuItem mediaRouteMenuItem = menu.findItem(R.id.media_route_menu_item);
        MediaRouteActionProvider mediaRouteActionProvider =
                (MediaRouteActionProvider) MenuItemCompat.getActionProvider(mediaRouteMenuItem);
        mediaRouteActionProvider.setRouteSelector(mMediaRouteSelector);

        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        AppView appView = AppView.getInstance();
        appView.setActivity(this);
        //on fait un trigger entre le media call back et l'instance du media router
        mMediaRouter.addCallback(mMediaRouteSelector, mMediaRouterCallback,
                MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY);
    }

    @Override
    protected void onStop() {
        //on supprime le trigger entre le media call back et l'instance du media router
        mMediaRouter.removeCallback(mMediaRouterCallback);
        super.onStop();
    }

}
