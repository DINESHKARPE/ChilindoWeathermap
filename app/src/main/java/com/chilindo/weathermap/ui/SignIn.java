package com.chilindo.weathermap.ui;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chilindo.weathermap.R;
import com.chilindo.weathermap.utility.PreferencesManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.identity.intents.Address;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import pub.devrel.easypermissions.EasyPermissions;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link } interface
 * to handle interaction events.
 * Use the {@link SignIn#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignIn extends Fragment implements  View.OnClickListener {

    private String TAG = SignIn.class.getSimpleName();
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int RC_SETTINGS_SCREEN = 125;
    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInOptions googleSignInOptions;

    private static final int RC_SIGN_IN = 9001;

    private static final String ARG_PARAM1 = "mGoogleApiClient";
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    private PreferencesManager preferencesManager;
    private OnFragmentInteractionListener mListener;





    public SignIn() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param
     * @param
     * @param googleSignInOptions
     * @param mGoogleApiClient
     * @return A new instance of fragment SignIn.
     */
    // TODO: Rename and change types and number of parameters
    public static SignIn newInstance(GoogleSignInOptions googleSignInOptions, GoogleApiClient mGoogleApiClient) {
        SignIn fragment = new SignIn();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        fragment.setGoogleSignInOptions(googleSignInOptions);
        fragment.setmGoogleApiClient(mGoogleApiClient);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        preferencesManager = new PreferencesManager(getContext());
        View rootView = inflater.inflate(R.layout.fragment_sign_in, container,false);
        SignInButton signInButton = (SignInButton) rootView.findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        return rootView;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + getString(R.string.implementListner));
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }


    private void signIn() {

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {

            if(preferencesManager.isRegister()){
                mListener.onFragmentInteraction(null,"Profile");
            }else {

                GoogleSignInAccount acct = result.getSignInAccount();
                Log.d(TAG,"Person loaded");
                Log.d(TAG,("DisplayName "+acct.getDisplayName()));
                Log.d(TAG,"Url "+acct.getPhotoUrl());
                JSONObject userAccount = new JSONObject();
                TelephonyManager mngr = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);

                try {
                    userAccount.put(getString(R.string.username),acct.getDisplayName());
                    userAccount.put(getString(R.string.userprofilepic),acct.getPhotoUrl().toString());
                    userAccount.put(getString(R.string.useremail),acct.getEmail());
                    preferencesManager.setUserProfile(userAccount);
                    Log.d(TAG,userAccount.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mListener.onFragmentInteraction(userAccount,"TODAY");

            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT > 23) {

            String[] perms = {Manifest.permission.GET_ACCOUNTS,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.READ_PHONE_STATE};


            if (!EasyPermissions.hasPermissions(getContext(), perms)) {
                EasyPermissions.requestPermissions(this, getString(R.string.all_permission_are_required),
                        PERMISSION_REQUEST_CODE, perms);
            }
        }
        if(getmGoogleApiClient() != null){

            getmGoogleApiClient().connect();
            OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(getmGoogleApiClient());

            if (opr.isDone()) {

                GoogleSignInResult result = opr.get();
                handleSignInResult(result);

            } else {

                opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {

                    @Override
                    public void onResult(GoogleSignInResult googleSignInResult) {
                        handleSignInResult(googleSignInResult);

                    }
                });
            }
        }
        checkLocationSettings();
    }



    public GoogleSignInOptions getGoogleSignInOptions() {
        return googleSignInOptions;
    }

    public void setGoogleSignInOptions(GoogleSignInOptions googleSignInOptions) {
        this.googleSignInOptions = googleSignInOptions;
    }

    public GoogleApiClient getmGoogleApiClient() {
        return mGoogleApiClient;
    }

    public void setmGoogleApiClient(GoogleApiClient mGoogleApiClient) {
        this.mGoogleApiClient = mGoogleApiClient;
    }



    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void checkLocationSettings() {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(5000/2);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient,
                        builder.build()
                );
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                switch (status.getStatusCode()) {

                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i(TAG, getString(R.string.location_sucess));
                        try {
                            setCurrentLocation();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i(TAG, getString(R.string.resolution_required));

                        try {
                            status.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i(TAG, getString(R.string.request_check_setting));
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i(TAG, getString(R.string.settings_chnages));
                        break;
                }
            }
        });
    }

    private void setCurrentLocation() throws JSONException {

        if (Build.VERSION.SDK_INT > 23) {

            String[] perms = {
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
            };


            if (!EasyPermissions.hasPermissions(getContext(), perms)) {
                EasyPermissions.requestPermissions(this, getString(R.string.all_permission_are_required),
                        PERMISSION_REQUEST_CODE, perms);
            }
        }
        try {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            if(mLastLocation != null){

                Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                List<android.location.Address> addresses = geocoder.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1);
                preferencesManager.setCity(addresses.get(0).getSubAdminArea());
                preferencesManager.setPostalCode(addresses.get(0).getPostalCode());
            }


        }catch (SecurityException e){

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
