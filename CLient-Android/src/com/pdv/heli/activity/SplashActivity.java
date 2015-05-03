package com.pdv.heli.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.pdv.heli.R;
import com.pdv.heli.activity.home.HomeActivity;
import com.pdv.heli.activity.startup.StartFirstActivity;
import com.pdv.heli.app.service.BgService;
import com.pdv.heli.app.service.SignInStateReceiver;
import com.pdv.heli.app.service.SignInStateReceiver.ReceiveCallBack;
import com.pdv.heli.manager.BackgroundManager;
import com.pdv.heli.manager.SharedPreferencesManager;
import com.pdv.heli.manager.TcpClientManager;
import com.pdv.heli.manager.TcpClientManager.State;

public class SplashActivity extends BaseActivity implements ReceiveCallBack {
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
	com.pdv.heli.app.service.SignInStateReceiver inStateReceiver;

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
					intent.setAction(BgService.CONNECT_TO_SERVER);
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
							if (SharedPreferencesManager.getLocalPhone() != null) {								
								TcpClientManager.getInstance().loginByCookie();
							} else {
								gotoSigninAcivity();
							}
						}
					}

				}
			}
		}).start();
		inStateReceiver = new SignInStateReceiver();
		inStateReceiver.register(getApplicationContext());
		inStateReceiver.setCallBack(this);
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
	
	@Override
	public void finish() {
		super.finish();
			
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
						if(SharedPreferencesManager.getLocalPhone()!=null){
							gotoHomeActivity();
						}else{
							gotoSigninAcivity();
						}
					} else {
						isConnectFail = true;
					}

				}
				if (action.equals(ACTION_CONNECT_SUCCESS)) {					
					if (SharedPreferencesManager.getLocalPhone() !=null) {						
						
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
			if(SharedPreferencesManager.getLocalPhone()!=null){
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
		inStateReceiver.unregister(getApplicationContext());
	}

	@Override
	public void onReciveCallBack(Context context, Intent intent) {
		if(intent.getAction().equals(SignInStateReceiver.ACTION_ON_SIGIN_SUCCESS)){
			Intent intent2 = new Intent(this, HomeActivity.class);
			startActivity(intent2);			
		}
		if(intent.getAction().equals(SignInStateReceiver.ACTION_ON_SIGIN_FAIL)){			
			Toast.makeText(getApplicationContext(), intent.getExtras().getString("error"), Toast.LENGTH_LONG).show();
			Intent intent2 = new Intent(getApplicationContext(), StartFirstActivity.class);
			startActivity(intent2);
			
		}
		this.finish();
	}

}
