package com.chilindo.weathermap;


import android.app.Application;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;


public class MyApplication extends Application implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{


    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInOptions gso;
    public AppCompatActivity activity;
    private SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;



    public GoogleSignInOptions getGoogleSignInOptions(){

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .requestIdToken(getString(R.string.server_client_id))
                .requestServerAuthCode(getString(R.string.server_client_id),false)
                .build();
        return gso;
    }


    public synchronized GoogleApiClient getGoogleApiClient(AppCompatActivity activity){
        this.activity = activity;
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this.activity, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, getGoogleSignInOptions())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .build();
        mGoogleApiClient.connect();
        return mGoogleApiClient;
    }

    @Override
    public void onCreate() {
        super.onCreate();


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

        switch (i){

            case GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST:
                break;
            case GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED:
                break;
            default:
        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }



}
