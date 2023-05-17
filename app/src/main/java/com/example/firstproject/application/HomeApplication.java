package com.example.firstproject.application;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;

import com.example.firstproject.security.JwtSecurityService;

import java.io.UnsupportedEncodingException;

public class HomeApplication extends Application implements JwtSecurityService {
    private static HomeApplication instance;
    private static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;
        appContext=getApplicationContext();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    public static HomeApplication getInstance() {
        return instance;
    }

    public static Context getAppContext() {
        return appContext;
    }

    @Override
    public void saveJwtToken(String token) {
        SharedPreferences prefs;
        SharedPreferences.Editor edit;
        prefs =  instance.getSharedPreferences("jwtStore", MODE_PRIVATE);
        edit=prefs.edit();
        try {
            edit.putString("token",token);
            edit.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public String getToken() {
        SharedPreferences prefs=instance.getSharedPreferences("jwtStore", Context.MODE_PRIVATE);
        String token = prefs.getString("token","");
        return token;
    }

    @Override
    public void deleteToken() {
        SharedPreferences prefs;
        SharedPreferences.Editor edit;
        prefs=instance.getSharedPreferences("jwtStore", Context.MODE_PRIVATE);
        edit=prefs.edit();
        try {
            edit.remove("token");
            edit.apply();
            edit.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isAuth() {
        if(getToken().equals(""))
            return false;
        return true;
    }

    @Override
    public String decoded() {
        String token = getToken();
        try {
            String[] split = token.split("\\.");
            String header = getJson(split[0]);
            String body = getJson(split[1]);
            return body;
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    private String getJson(String strEncoded) throws UnsupportedEncodingException {
        byte[] decodedBytes = Base64.decode(strEncoded, Base64.URL_SAFE);
        return new String(decodedBytes, "UTF-8");
    }
}