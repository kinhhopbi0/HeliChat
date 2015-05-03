package com.pdv.heli.manager;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;

import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;
import com.nispok.snackbar.listeners.ActionClickListener;
import com.pdv.heli.R;
import com.pdv.heli.activity.SplashActivity;
import com.pdv.heli.activity.setting.AppSettingActivity;
import com.pdv.heli.app.HeliApplication;
import com.pdv.heli.component.HeliSnackbars;
import com.pdv.heli.manager.TcpClientManager.State;

/**
 * Created by via on 1/26/15.
 */
public class ActivitiesManager {
	private static ActivitiesManager instance;

	static {
		instance = new ActivitiesManager();
	}

	public static ActivitiesManager getInstance() {
		return instance;
	}

	private ActivitiesManager() {

	}

	private Activity mCurrentActivity;

	public Activity getCurrentActivity() {
		return mCurrentActivity;
	}

	private Snackbar snackbar;

	public void setCurrentActivity(final Activity mCurrentActivity) {
		if (snackbar != null) {
			snackbar = null;
		}
		State state = TcpClientManager.getInstance().getConnectState();
		if (state
				.equals(TcpClientManager.State.NOTCONNECTED) && !(mCurrentActivity instanceof SplashActivity)) {
			
			Handler handler = new Handler(HeliApplication.getInstance()
					.getMainLooper());
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					HeliSnackbars.showNetWorkNotConnect(mCurrentActivity);
				}
			}, 500);
		}

		this.mCurrentActivity = null;
		this.mCurrentActivity = mCurrentActivity;
	}

	public void removeCurrentActivity(Activity baseActivity) {
		if (mCurrentActivity != null && mCurrentActivity.equals(baseActivity)) {
			mCurrentActivity = null;
			if (snackbar != null) {
				snackbar.dismiss();
			}
		}
	}

}
