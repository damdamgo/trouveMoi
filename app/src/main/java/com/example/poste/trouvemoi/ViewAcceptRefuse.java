package com.example.poste.trouvemoi;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.MediaRouteActionProvider;
import android.support.v7.media.MediaRouteSelector;
import android.support.v7.media.MediaRouter;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.cast.CastMediaControlIntent;

/**
 * Created by Poste on 20/12/2015.
 */
public class ViewAcceptRefuse extends AppCompatActivity {
    private View fr;
    private View touch = null;
    private float xtBefore=-1,ytBefore=-1,xEncrage,yEncrage,widthScreen=-1,xEncrageElement,yEncrageElement;
    private int[] tablocation = new int[2];
    private VelocityTracker mVelocityTracker = null;
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

        mMediaRouter = MediaRouter.getInstance(getApplicationContext());


        mMediaRouteSelector = new MediaRouteSelector.Builder()
                .addControlCategory(CastMediaControlIntent.categoryForCast("BC343A35"))
                .build();
        mMediaRouterCallback=CastConnect.getInstance();
        ((CastConnect)mMediaRouterCallback).setActivity(this);



        fr = (CardView) findViewById(R.id.frame);
        fr.post(new Runnable() {
            @Override
            public void run() {
                fr.getLocationOnScreen(tablocation);
                xEncrage = tablocation[0];
                yEncrage = tablocation[1];
                xEncrageElement = fr.getX();
                yEncrageElement = fr.getY();
            }
        });
        fr.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                touch = fr;
                return false;
            }
        });
        RelativeLayout r = (RelativeLayout) findViewById(R.id.conteneur);
        r.post(new Runnable() {
            @Override
            public void run() {
                RelativeLayout r = (RelativeLayout) findViewById(R.id.conteneur);
                float height = r.getHeight();
                float result = fr.getY();
                fr.setY(height);
                AnimatorSet animSetXY = new AnimatorSet();
                ObjectAnimator x = ObjectAnimator.ofFloat(fr,
                        "y", fr.getY(), result);
                animSetXY.playTogether(x);
                animSetXY.setDuration(600);
                animSetXY.start();
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splash, menu);

        MenuItem mediaRouteMenuItem = menu.findItem(R.id.media_route_menu_item);
        MediaRouteActionProvider mediaRouteActionProvider =
                (MediaRouteActionProvider) MenuItemCompat.getActionProvider(mediaRouteMenuItem);
        mediaRouteActionProvider.setRouteSelector(mMediaRouteSelector);

        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMediaRouter.addCallback(mMediaRouteSelector, mMediaRouterCallback,
                MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY);
    }

    @Override
    protected void onStop() {
        mMediaRouter.removeCallback(mMediaRouterCallback);
        super.onStop();
    }

    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        float xt = event.getX();
        float yt = event.getY();
        float xf=0,yf=0;
        Log.w("zerz", "");
        if(xtBefore==-1)xtBefore=xt;
        if(ytBefore==-1)ytBefore=yt;
        if(widthScreen==-1)widthScreen=xt;
        if(touch!=null){
            xf = touch.getX();
            yf = touch.getY();
            touch.setX(xf + (xt - xtBefore));
            touch.setY(yf + (yt - ytBefore));
            xtBefore = xt;
            ytBefore = yt;
            if(widthScreen<xt){
                float p = xt-widthScreen;
                p=((p*100)/250)/100;
                RelativeLayout r = (RelativeLayout)findViewById(R.id.accepte);
                r.setAlpha(p);
                TextView txt = (TextView) findViewById(R.id.textviewaccept);
                txt.setTextColor(Color.parseColor("#4CAF50"));
                txt = (TextView) findViewById(R.id.textviewrefuse);
                txt.setTextColor(Color.parseColor("#212121"));
            }
            else if(widthScreen>xt){
                float p = widthScreen-xt;
                p=((p*100)/250)/100;
                RelativeLayout r = (RelativeLayout)findViewById(R.id.refuse);
                r.setAlpha(p);
                TextView txt = (TextView) findViewById(R.id.textviewaccept);
                txt.setTextColor(Color.parseColor("#212121"));
                txt = (TextView) findViewById(R.id.textviewrefuse);
                txt.setTextColor(Color.parseColor("#F44336"));
            }
        }


        int index = event.getActionIndex();
        int pointerId = event.getPointerId(index);
        switch(action) {
            case MotionEvent.ACTION_DOWN:
                if(mVelocityTracker == null) {
                    mVelocityTracker = VelocityTracker.obtain();
                }
                else {
                    mVelocityTracker.clear();
                }
                mVelocityTracker.addMovement(event);
                break;
            case MotionEvent.ACTION_MOVE:
                mVelocityTracker.addMovement(event);
                mVelocityTracker.computeCurrentVelocity(1000);
                Log.w("aze", "X velocity: " +
                        VelocityTrackerCompat.getXVelocity(mVelocityTracker,
                                pointerId));
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if(touch!=null) {
                    float p = xt-widthScreen;
                    p=((p*100)/250)/100;
                    if(mVelocityTracker.getXVelocity()>2000 || p>1 ){
                        RelativeLayout r = (RelativeLayout)findViewById(R.id.accepte);
                        r.setAlpha(1);
                        Display display = getWindowManager().getDefaultDisplay();
                        Point size = new Point();
                        display.getSize(size);
                        int width = size.x;
                        Log.w("ee","ok");
                        AnimatorSet animSetXY = new AnimatorSet();
                        ObjectAnimator x = ObjectAnimator.ofFloat(touch,
                                "x", touch.getX(), width+100);
                        animSetXY.playTogether(x);
                        animSetXY.setDuration(100);
                        animSetXY.start();
                    }
                    else if( p<-1|| mVelocityTracker.getXVelocity()<-2000){
                        RelativeLayout r = (RelativeLayout)findViewById(R.id.refuse);
                        r.setAlpha(p);
                        Display display = getWindowManager().getDefaultDisplay();
                        Point size = new Point();
                        display.getSize(size);
                        int width = -size.x;
                        Log.w("ee","ok");
                        AnimatorSet animSetXY = new AnimatorSet();
                        ObjectAnimator x = ObjectAnimator.ofFloat(touch,
                                "x", touch.getX(), width-100);
                        animSetXY.playTogether(x);
                        animSetXY.setDuration(100);
                        animSetXY.start();
                    }
                    else{
                        fr.getLocationOnScreen(tablocation);
                        xt = xEncrage - tablocation[0];
                        yt = yEncrage - tablocation[1];

                        AnimatorSet animSetXY = new AnimatorSet();
                        ObjectAnimator y = ObjectAnimator.ofFloat(touch,
                                "y", touch.getY(), yEncrageElement);
                        ObjectAnimator x = ObjectAnimator.ofFloat(touch,
                                "x", touch.getX(), xEncrageElement);
                        animSetXY.playTogether(x, y);
                        animSetXY.setDuration(200);
                        animSetXY.start();
                        animSetXY.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {

                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });

                        xtBefore = -1;
                        ytBefore = -1;
                        widthScreen = -1;
                        touch = null;
                        RelativeLayout r = (RelativeLayout) findViewById(R.id.accepte);
                        r.setAlpha(0);
                        r = (RelativeLayout) findViewById(R.id.refuse);
                        r.setAlpha(0);
                    }
                    TextView txt = (TextView) findViewById(R.id.textviewaccept);
                    txt.setTextColor(Color.parseColor("#212121"));
                    txt = (TextView) findViewById(R.id.textviewrefuse);
                    txt.setTextColor(Color.parseColor("#212121"));
                }
                break;
        }

        return true;
    }
}

