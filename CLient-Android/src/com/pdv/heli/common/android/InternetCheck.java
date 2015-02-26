package com.pdv.heli.common.android;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.pdv.heli.app.HeliApplication;

/**
 * Created by via on 1/26/15.
 */
public class InternetCheck {

    private static InternetCheck instance;
    private ConnectivityManager _connectivityManager;

    protected InternetCheck() {
        _connectivityManager = (ConnectivityManager) HeliApplication.getInstance()
                .getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    static {
        instance = new InternetCheck();
    }


    public boolean hasConnection() {
        Log.v(this.getClass().getName(),"Checking all internet state connection");
        return (hasActiveConnected() || hasWifiConnected() || hasMobileConnected());
    }

    public boolean hasWifiConnected() {
        Log.v(this.getClass().getName(),"Checking state wifi");
        NetworkInfo wifiNetworkInfo = _connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetworkInfo != null && wifiNetworkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    public boolean hasMobileConnected() {
        Log.v(this.getClass().getName(),"Checking state mobile");
        NetworkInfo mobileNetwork = _connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileNetwork != null && mobileNetwork.isConnected()) {
            return true;
        }
        return  false;
    }
    public boolean hasActiveConnected(){
        Log.v(this.getClass().getName(),"Checking state other");
        NetworkInfo activeNetwork = _connectivityManager.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            return true;
        }
        return false;
    }

    /**
     * get and set ter
     */
    public ConnectivityManager getConnectivityManager() {
        return _connectivityManager;
    }

    public static InternetCheck getInstance() {
        return instance;
    }

}
