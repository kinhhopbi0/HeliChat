package com.pdv.heli.manager;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.pdv.heli.app.HeliApplication;
import com.pdv.heli.app.service.NetworkService;


/**
 * Created by vinhanh on 12/01/2015.
 */
public class BackgroundManager {
    private static final  String TAG = BackgroundManager.class.getSimpleName();
    private static BackgroundManager instance;

    static {
        instance = new BackgroundManager();
    }
    private BackgroundManager() { }
    public static BackgroundManager getInstance() {
        return instance;
    }

    public void startService(){
        if(!isServiceRunning(NetworkService.class)){
            Intent intent = new Intent(HeliApplication.getInstance(), NetworkService.class);
            intent.setAction(NetworkService.START_FOREGROUND);
            HeliApplication.getInstance().startService(intent);
        }
    }

    public void stopService(){
        if(isServiceRunning(NetworkService.class)){
            HeliApplication.getInstance().stopService(NetworkService.class);
        }
    }


    public static boolean isServiceRunning(Class<? extends Service> serviceClass) {
        ActivityManager manager = (ActivityManager) HeliApplication.getInstance().getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i(TAG,"Service "+serviceClass.toString()+" is running");
                return true;
            }
        }
        Log.i(TAG,"Service "+serviceClass.toString()+" is not running");
        return false;
    }
}

