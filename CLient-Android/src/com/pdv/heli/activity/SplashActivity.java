package com.pdv.heli.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.pdv.heli.R;
import com.pdv.heli.activity.startup.StartFirstActivity;
import com.pdv.heli.manager.BackgroundManager;

public class SplashActivity extends Activity {
	private LoadingThread _loadingThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        _loadingThread = new LoadingThread();
        _loadingThread.start();        
    }

    private void finishLoadSplash() {    	
        Intent home = new Intent(getApplicationContext(), StartFirstActivity.class);        
        startActivity(home);
        this.finish();
    }

    @Override
    public void onBackPressed() {
        release();
        super.onBackPressed();
        this.finish();
    }

    private void release() {
        _loadingThread.interrupt();
        _loadingThread = null;
    }

    private void loadingFinish(){
        this.finishLoadSplash();
        this.finish();
    }

    public class LoadingThread extends Thread{
        @Override
        public void run() {
            loadResources();
            startBackground();
            int delay = 500;
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            SplashActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    SplashActivity.this.loadingFinish();
                }
            });
        }

       
        private void loadResources() {

        }
        private void startBackground() {
            BackgroundManager.getInstance().startService();
        }
    }

	
}
