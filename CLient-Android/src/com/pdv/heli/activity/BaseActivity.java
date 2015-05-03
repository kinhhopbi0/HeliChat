package com.pdv.heli.activity;

import android.support.v7.app.ActionBarActivity;

import com.pdv.heli.manager.ActivitiesManager;

public class BaseActivity extends ActionBarActivity{
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
