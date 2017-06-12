package com.chilindo.weathermap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.chilindo.weathermap.sync.ChilindoSyncAdapter;
import com.chilindo.weathermap.ui.NextFiveDaysForecastFragment;
import com.chilindo.weathermap.ui.OnFragmentInteractionListener;
import com.chilindo.weathermap.ui.SignIn;
import com.chilindo.weathermap.ui.TodayForecastFragment;
import com.chilindo.weathermap.utility.PreferencesManager;
import com.chilindo.weathermap.utility.Utility;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

public class WeatherDetail extends AppCompatActivity implements TodayForecastFragment.Callback,OnFragmentInteractionListener,NavigationView.OnNavigationItemSelectedListener {

    private String mLocation;
    private static final String DETAILFRAGMENT_TAG = "DFTAG";


    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInOptions googleSignInOptions;
    private PreferencesManager preferencesManager;
    android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
    android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

    private  TextView user_name;
    private TextView  user_email;
    private  View headerLayout;
    private String TAG = WeatherDetail.class.getCanonicalName();
    private String postalCode;
    private String cityName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferencesManager = new PreferencesManager(getApplicationContext());
        setContentView(R.layout.activity_weather_detil);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);




        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        headerLayout = navigationView.getHeaderView(0);

        user_name  = (TextView) headerLayout.findViewById(R.id.userName);
        user_email = (TextView) headerLayout.findViewById(R.id.userEmail);
        getSupportActionBar().setElevation(0f);

        googleSignInOptions = ((MyApplication) getApplication()).getGoogleSignInOptions();
        mGoogleApiClient = ((MyApplication) getApplication()).getGoogleApiClient(WeatherDetail.this);

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        updateView("");
        ChilindoSyncAdapter.initializeSyncAdapter(this);
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.weather_detil, menu);
        return true;
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.today) {
           updateView("TODAY");
        } else if (id == R.id.next) {
           updateView("NEXTFIVE");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            if(preferencesManager.getUserProfile() != null){
                final  ImageView imageView = (ImageView) headerLayout.findViewById(R.id.imageView);
                user_name.setText(preferencesManager.getUserProfile().get("name").toString());
                user_email.setText(preferencesManager.getUserProfile().get("email").toString());

                postalCode = preferencesManager.getPostalCode();
                cityName = preferencesManager.getCity();
                Glide.with(getApplicationContext()).
                        load(preferencesManager.getUserProfile().get("photourl").toString())
                        .asBitmap()
                        .centerCrop()
                        .override(300, 300)
                        .into(new BitmapImageViewTarget(imageView) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                RoundedBitmapDrawable circularBitmapDrawable =
                                        RoundedBitmapDrawableFactory.create(getApplicationContext().getResources(), resource);
                                circularBitmapDrawable.setCircular(true);
                                imageView.setImageDrawable(circularBitmapDrawable);
                            }
                        });


                if(!postalCode.isEmpty()){
                    preferencesManager.setLocation(postalCode);
                    mLocation = postalCode;
                }
                else if(!cityName.isEmpty()){
                    preferencesManager.setLocation(cityName);
                    mLocation = cityName;
                }
                /**
                 * Default Location
                 */
                else if(cityName.isEmpty() && postalCode.isEmpty()){

                    String location = Utility.getPreferredLocation( this );
                    if (location != null && !location.equals(mLocation)) {
                        mLocation = location;
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onItemSelected(Uri dateUri) {
        Intent intent = new Intent(this, DetailActivity.class)
                .setData(dateUri);
        startActivity(intent);
    }

    private void updateView(final String type) {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();


        switch (type) {
            case "TODAY":
                TodayForecastFragment todayForecastFragment = TodayForecastFragment.newInstance();
                todayForecastFragment.setUseTodayLayout(true);
                fragmentTransaction.replace(R.id.fragment_forecast, todayForecastFragment);
                break;
            case "NEXTFIVE":
                NextFiveDaysForecastFragment nextFiveDaysForecastFragment = NextFiveDaysForecastFragment.newInstance();
                nextFiveDaysForecastFragment.setUseTodayLayout(false);
                fragmentTransaction.replace(R.id.fragment_forecast, nextFiveDaysForecastFragment);
                break;
            default:
                fragmentTransaction.replace(R.id.fragment_forecast,SignIn.newInstance(googleSignInOptions,mGoogleApiClient));
        }
        fragmentTransaction.commit();
    }





    @Override
    public void onFragmentInteraction(JSONObject fragmentData, String string) {
        updateView(string);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }


}
