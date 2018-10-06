package com.appz_world_hackathon.ryadi.Activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.appz_world_hackathon.ryadi.AppUtil.mApp.AppSharedPreferences;
import com.appz_world_hackathon.ryadi.AppUtil.mApp.Session;
import com.appz_world_hackathon.ryadi.R;


import java.util.Locale;

public class SplashActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private static final String LOG_TAG = "Refersh";
    Session session;
    AppSharedPreferences appSharedPreferences;
    ProgressBar progressBar;
    SwipeRefreshLayout mySwipeRefreshLayout;
    Button refreshBtn;
    TextView plsWaitTv;

    private void setLanguageForApp(String language) {
        String languageToLoad = language; //pass the language code as param
        Locale locale;
        if (languageToLoad == null) {
            locale = Locale.US;
        } else {
            locale = new Locale(languageToLoad);
        }
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setLanguageForApp(AppSharedPreferences.restoreLanguage(this));

/*        if (AppSharedPreferences.isThemeTypeGirl(this)) {
            setTheme(R.style.AppThemeFemale);
        } else {
            setTheme(R.style.AppTheme);
        }*/

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

/*        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.BLACK);
        toolbar.setSubtitleTextColor(Color.BLACK);*/

        session = new Session(this);
        appSharedPreferences = new AppSharedPreferences(this);

        progressBar = findViewById(R.id.progressBar);

        plsWaitTv = findViewById(R.id.pls_wait_txt_v);

        mySwipeRefreshLayout = findViewById(R.id.swipeRefresh);
        mySwipeRefreshLayout.setEnabled(false);

        refreshBtn = findViewById(R.id.refreshBtn);
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(LOG_TAG, "Refresh menu item selected");

                // Signal SwipeRefreshLayout to start the progress indicator
                mySwipeRefreshLayout.setRefreshing(true);

                // Start the refresh background task.
                // This method calls setRefreshing(false) when it's finished.
                myUpdateOperation();
            }
        });

        /*
         * Sets up a SwipeRefreshLayout.OnRefreshListener that is invoked when the user
         * performs a swipe-to-refresh gesture.
         */
        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i(LOG_TAG, "onRefresh called from SwipeRefreshLayout");
                        Intent intent = new Intent(SplashActivity.this, SplashActivity.class);
                        startActivity(intent);
                    }
                }
        );

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(4100);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loggedin();
                        }
                    });

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

    }

    public void loggedin() {
        if (session.loggedin() && session.isNetworkConnected(this)) {
//            session.isAccountEnabled(appSharedPreferences.readString("u_Id"));
            progressBar.setVisibility(View.GONE);
            plsWaitTv.setVisibility(View.VISIBLE);
            mySwipeRefreshLayout.setEnabled(true);
           // refreshBtn.setVisibility(View.VISIBLE);
         //   startActivity(new Intent(SplashActivity.this, AuthActivity.class));
        //    finish();
        } else if (!session.isNetworkConnected(this)) {
            progressBar.setVisibility(View.GONE);
            mySwipeRefreshLayout.setEnabled(true);
            plsWaitTv.setVisibility(View.VISIBLE);
            plsWaitTv.setText(getApplicationContext().getResources().getString(R.string.toastNoInternet));
//            Looper.prepare();
            Toast.makeText(SplashActivity.this, getApplicationContext().getResources().getString(R.string.toastNoInternet),
                    Toast.LENGTH_LONG).show();
            refreshBtn.setVisibility(View.VISIBLE);
//            Looper.loop();
        } else {
            startActivity(new Intent(SplashActivity.this, AuthActivity.class));
            finish();
        }
    }

    private void myUpdateOperation() {
        mySwipeRefreshLayout.setEnabled(true);
        Log.i(LOG_TAG, "onRefresh called from SwipeRefreshLayout");
        Intent intent = new Intent(SplashActivity.this, SplashActivity.class);
        mySwipeRefreshLayout.setRefreshing(false);
        startActivity(intent);

    }

    @Override
    public void onRefresh() {
        recreate();
    }
}
