package com.pdv.heli.activity;

import com.pdv.heli.app.ActivitiesManager;

import android.app.Activity;

public class BaseActivity extends Activity{
	@Override
	protected void onPause() {		
		super.onPause();
		ActivitiesManager.getInstance().removeCurrentActivity(this);
	}
	@Override
	protected void onResume() {		
		super.onResume();
		ActivitiesManager.getInstance().setCurrentActivity(this);
	}
	@Override
	protected void onDestroy() {		
		super.onDestroy();
		ActivitiesManager.getInstance().removeCurrentActivity(this);
	}
}
