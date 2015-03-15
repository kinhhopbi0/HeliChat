package com.pdv.heli.app;

import android.app.Activity;

/**
 * Created by via on 1/26/15.
 */
public class ActivitiesManager {
    private static ActivitiesManager instance;

    static{
        instance = new ActivitiesManager();
    }

    public static ActivitiesManager getInstance() {
        return instance;
    }

    private ActivitiesManager(){

    }

    private Activity mCurrentActivity;

    public Activity getCurrentActivity() {
        return mCurrentActivity;
    }

    public void setCurrentActivity(Activity mCurrentActivity) {
        this.mCurrentActivity = mCurrentActivity;
    }

   
    
    public void removeCurrentActivity(Activity baseActivity) {
    	if(mCurrentActivity != null &&  mCurrentActivity.equals(baseActivity)){
            mCurrentActivity = null;
        }
    }
    
}
