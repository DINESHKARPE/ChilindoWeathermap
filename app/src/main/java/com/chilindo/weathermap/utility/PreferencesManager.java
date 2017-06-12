package com.chilindo.weathermap.utility;

import android.content.Context;
import android.content.SharedPreferences;

import com.chilindo.weathermap.R;
import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by DINESH KARPE <contact@dineshkarpe.me></>
 */

public class PreferencesManager {

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context _context;
    private JSONObject userProfile;
    private boolean isRegister;
    private int PRIVATE_MODE = 0;
    private boolean profileStatus;
    private static final String PREF_NAME = "ChilindoWeathermap";

    private static final String IS_FIRST_TIME_LAUNCH = "firstTimeLaunch";
    private static final String CURRENT_VIEW = "currentview";
    private static final String USER_PROFILE = "userprofile";
    private String currentView;

    public PreferencesManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();

    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTime() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }



    public String getCurrentView() {
        return pref.getString(CURRENT_VIEW,"");
    }

    public void setCurrentView(String currentView) {
        editor.putString(CURRENT_VIEW, currentView);
        editor.commit();
    }

    public JSONObject getUserProfile() {

        Gson gson = new Gson();
        String json = pref.getString(USER_PROFILE, "");
        return gson.fromJson(json, JSONObject.class);
    }

    public void setUserProfile(JSONObject userProfile) {
        String userProfileString = new Gson().toJson(userProfile);
        editor.putString(USER_PROFILE, userProfileString);
        editor.commit();
    }

    public String getValueFromKey(String key){
        String value;
        try {
            value =  getUserProfile().get(key).toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
        return value;

    }

    public boolean isRegister() {
        return pref.getBoolean(String.valueOf(R.string.user_sign_in), false);
    }

    public void setRegister(boolean register) {
        editor.putBoolean(String.valueOf(R.string.user_sign_in),true);
        editor.commit();
        isRegister = register;
    }

    public boolean isProfileStatus() {
        return profileStatus;
    }

    public void setProfileStatus(boolean profileStatus) {
        this.profileStatus = profileStatus;
    }

    public void setPostalCode(String postalCode) {
        editor.putString(String.valueOf(R.string.postalcode),postalCode);
        editor.commit();
    }

    public void setCity(String city) {
        editor.putString(String.valueOf(R.string.city),city);
        editor.commit();
    }

    public String getPostalCode(){
        return pref.getString(String.valueOf(R.string.postalcode),"");
    }
    public String getCity(){
        return pref.getString(String.valueOf(R.string.city),"");
    }

    public void setLocation(String location){
        editor.putString(String.valueOf(R.string.pref_location_key),location);
        editor.commit();
    }
}
