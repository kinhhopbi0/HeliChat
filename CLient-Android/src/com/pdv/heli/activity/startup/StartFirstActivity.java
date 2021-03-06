package com.pdv.heli.activity.startup;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.pdv.heli.R;
import com.pdv.heli.activity.home.HomeActivity;
import com.pdv.heli.app.service.SignInStateReceiver;
import com.pdv.heli.app.service.SignInStateReceiver.ReceiveCallBack;
import com.pdv.heli.manager.ActivitiesManager;
import com.pdv.heli.message.detail.SignUpMessage;

public class StartFirstActivity extends FragmentActivity implements ReceiveCallBack {
	private ViewPager viewPager;
	private PageAdapter fragmentPageAdapter;
	private SignInStateReceiver stateReceiver;

	@Override
	protected void onCreate(Bundle saveInstanceState) {
		super.onCreate(saveInstanceState);
		initComponent();		
		stateReceiver = new SignInStateReceiver();
		stateReceiver.setCallBack(this);
		Log.v("test", "create start");
	}

	private void initComponent() {
		setContentView(R.layout.activity_start_first);

		viewPager = (ViewPager) findViewById(R.id.pager);
		
		fragmentPageAdapter = new PageAdapter(getSupportFragmentManager());
		viewPager.setAdapter(fragmentPageAdapter);
	}

	public void btnSignUpClick(View pView) {
		Toast.makeText(getApplicationContext(), "sign up", Toast.LENGTH_SHORT)
				.show();
	}

	@Override
	protected void onResume() {
		super.onResume();
		ActivitiesManager.getInstance().setCurrentActivity(this);
		stateReceiver.register(getApplicationContext());
		Log.v("test", "start resume");
	}

	@Override
	protected void onPause() {
		Log.v("Test", "activiti on pause");
		ActivitiesManager.getInstance().removeCurrentActivity(this);
		stateReceiver.unregister(getApplicationContext());
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		ActivitiesManager.getInstance().removeCurrentActivity(this);
		super.onDestroy();
	}

	private class PageAdapter extends FragmentPagerAdapter {

		public PageAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int pIndex) {
			Fragment frg = null;
			switch (pIndex) {
			case 0:
				frg = new SignInFragment();
				break;
			case 1:
				frg = new SignUpFragment();
				break;
			}
			return frg;
		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0:
				return getResources().getText(R.string.lb_sign_in);
			case 1:
				return getResources().getText(R.string.lb_sign_up);
			default:
				return null;
			}
		}

	}
	
	public Fragment getCurrentFragment(){
		Fragment page = getSupportFragmentManager().findFragmentByTag(
				"android:switcher:" + R.id.pager + ":"
						+ viewPager.getCurrentItem());
		return page;
	}
	
	public void processSignupMessage(SignUpMessage pMsg) {		
		Fragment page = getCurrentFragment();
		switch (pMsg.getStatus()) {
		case SignUpMessage.Status.CREATE_SUCCESS:
			if (page != null && page instanceof SignUpFragment) {
				((SignUpFragment) page).createStepOneSuccess();
			}
			break;
		case SignUpMessage.Status.CREATE_FAIL:
			switch (pMsg.getFailType()) {
			case SignUpMessage.FailType.PHONE_EXIST:				
				if (page != null && page instanceof SignUpFragment) {
					((SignUpFragment) page).showPhoneExist();
				}
				break;
			case SignUpMessage.FailType.OTHER:
				if (page != null && page instanceof SignUpFragment) {
					((SignUpFragment) page).doResponseOtherError();
				}
				Toast.makeText(getApplicationContext(),
						R.string.sign_up_undefied_error, Toast.LENGTH_SHORT)
						.show();
				break;
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onReciveCallBack(Context context, Intent intent) {
		if(intent.getAction().equals(SignInStateReceiver.ACTION_ON_SIGIN_SUCCESS)){
			Intent intent2 = new Intent(this, HomeActivity.class);
			startActivity(intent2);
			this.finish();
			return;
		}
		if(intent.getAction().equals(SignInStateReceiver.ACTION_ON_SIGIN_FAIL)){
			String text =  intent.getExtras().getString("error","");
			SnackbarManager.show(Snackbar.with(this).text(text).color(Color.RED));
		}
	}
}
