package com.pdv.heli.activity.home;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.pdv.heli.R;

public class HomeActivity extends ActionBarActivity {
	private Toolbar toolBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.v("Home", "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		toolBar = (Toolbar) findViewById(R.id.app_bar);
		setSupportActionBar(toolBar);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		FrgmNavigationDrawer drawerFragment = (FrgmNavigationDrawer) getFragmentManager()
				.findFragmentById(R.id.left_slide);

		drawerFragment.setUp(R.id.left_slide,
				(DrawerLayout) findViewById(R.id.drawer_layout), toolBar);
		createFragmentContent(savedInstanceState);

	}

	private void createFragmentContent(Bundle inBundle) {
		if (findViewById(R.id.fragment_container) != null) {
			if (inBundle != null) {
				return;
			}
			Fragment friendsFragment = new AccountSettingFragment();
			friendsFragment.setArguments(getIntent().getExtras());
			getSupportFragmentManager().beginTransaction()
					.add(R.id.fragment_container, friendsFragment).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_home, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {

			return true;
		}
		if (id == R.id.home) {
			Toast.makeText(this, "home", Toast.LENGTH_SHORT).show();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.i("Home", "onResume");
	}

	@Override
	public void onBackPressed() {
		finish();
	}
}
