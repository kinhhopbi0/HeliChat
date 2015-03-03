package com.pdv.heli.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.TextView;

import com.pdv.heli.R;
import com.pdv.heli.activity.home.ConversationActivity;
import com.pdv.heli.activity.home.HomeActivity;
import com.pdv.heli.activity.startup.StartFirstActivity;
import com.pdv.heli.app.service.BgService;
import com.pdv.heli.manager.BackgroundManager;
import com.pdv.heli.manager.SharedPreferencesManager;
import com.pdv.heli.manager.TcpClientManager;
import com.pdv.heli.manager.TcpClientManager.State;

public class SplashActivity extends BaseActivity {
	public static final String ACTION_UPDATE_STATUS = BCReceiver.class.getName()
			+ ".action.LOADING";
	public static final String ACTION_CONNECT_FAIL = BCReceiver.class.getName()
			+ ".action.CONNECT_FAIL";
	public static final String ACTION_CONNECT_SUCCESS = BCReceiver.class
			.getName() + ".action.CONNECT_SUCCESS";
	public static final String KEY_STATUS_TEXT = "STATUS_KEY";

	private SplashActivity.BCReceiver receiver;
	private TextView tvStatus;
	private boolean isVisible;
	private boolean isConnectFail;	
	private String lastest_status;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		tvStatus = (TextView) findViewById(R.id.tv_status);
		receiver = new BCReceiver();
		registerReceiver(receiver, receiver.getIntentFilter());
		new Thread(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(SplashActivity.this, BgService.class);
				if (!BackgroundManager.isServiceRunning(BgService.class)) {
					startService(intent);
				} else {
					TcpClientManager.State state = TcpClientManager
							.getInstance().getConnectState();
					if (state == State.NEW || state == State.NOTCONNECTED) {
						stopService(intent);
						startService(intent);
					} else if (state == State.READY) {
						if (TcpClientManager.getInstance().isLogined()) {
							gotoHomeActivity();
						} else {							
							if (SharedPreferencesManager.getLoginUserId() > 0) {								
								TcpClientManager.getInstance().reLoginFromCookie();
							} else {
								gotoSigninAcivity();
							}
						}
					}

				}
			}
		}).start();
	}

	public void startNetWorkService() {
		Intent intent = new Intent(SplashActivity.this, BgService.class);
		startService(intent);
	}

	private void gotoHomeActivity() {
		Intent homeIntent = new Intent(getApplicationContext(),
				HomeActivity.class);
		startActivity(homeIntent);
		this.finish();
	}

	public void gotoSigninAcivity() {
		Intent signInActivity = new Intent(getApplicationContext(),
				StartFirstActivity.class);
		startActivity(signInActivity);
		this.finish();
	}

	@Override
	public void onBackPressed() {
		release();
		super.onBackPressed();
		this.finish();
	}

	private void release() {

	}

	public class BCReceiver extends BroadcastReceiver {
		private IntentFilter intentFilter;

		public BCReceiver() {
			intentFilter = new IntentFilter();
			intentFilter.addAction(SplashActivity.ACTION_UPDATE_STATUS);
			intentFilter.addAction(SplashActivity.ACTION_CONNECT_FAIL);
			intentFilter.addAction(SplashActivity.ACTION_CONNECT_SUCCESS);
			intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
		}

		@Override
		public void onReceive(Context context, Intent intent) {
			String action;
			if (intent != null && (action = intent.getAction()) != null) {
				if (action.equals(ACTION_UPDATE_STATUS)) {
					lastest_status = intent.getExtras().getString(
							KEY_STATUS_TEXT);
					if (isVisible) {
						SplashActivity.this.tvStatus.setText(lastest_status);
					}
					return;
				}
				if (action.equals(ACTION_CONNECT_FAIL)) {
					if (isVisible) {
						if(SharedPreferencesManager.getLoginUserId()>0){
							gotoHomeActivity();
						}else{
							gotoSigninAcivity();
						}
					} else {
						isConnectFail = true;
					}

				}
				if (action.equals(ACTION_CONNECT_SUCCESS)) {					
					if (SharedPreferencesManager.getLoginUserId() > 0) {						
						
					} else {
						if(isVisible){
							gotoSigninAcivity();
						}else{
							isConnectFail = true;
						}
					}
				}
			}
		}

		public IntentFilter getIntentFilter() {
			return intentFilter;
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		isVisible = true;
		if(isConnectFail){
			if(SharedPreferencesManager.getLoginUserId()>0){
				gotoHomeActivity();
			}else{
				gotoSigninAcivity();
			}
			return;
		}		
		this.tvStatus.setText(lastest_status);

	}

	@Override
	protected void onPause() {
		super.onPause();
		isVisible = false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	}

}
