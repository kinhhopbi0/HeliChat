package com.pdv.heli.app;

import android.app.Activity;

import com.pdv.heli.activity.home.ConversationActivity;

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

    public ConversationActivity getLiveConversation(){
        if(mCurrentActivity != null &&  mCurrentActivity instanceof ConversationActivity){
            if (((ConversationActivity) mCurrentActivity) .isVisible()){
                return (ConversationActivity) mCurrentActivity;
            }
        }
        return null;
    }
    
    public void removeCurrentActivity(Activity baseActivity) {
    	if(mCurrentActivity != null &&  mCurrentActivity.equals(baseActivity)){
            mCurrentActivity = null;
        }
    }
    
}
