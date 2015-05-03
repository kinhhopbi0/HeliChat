package com.pdv.heli.activity.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.listeners.EventListener;
import com.pdv.heli.R;
import com.pdv.heli.activity.contact.FriendsFragment;
import com.pdv.heli.activity.conversation.ConversationFragment;
import com.pdv.heli.component.HeliSnackbars;
import com.pdv.heli.manager.ActivitiesManager;

public class HomeActivity extends ActionBarActivity {
	// private Toolbar toolBar;

	// public Toolbar getToolBar(){
	// return this.toolBar;
	// }
	private ActionBarDrawerToggle dr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.v("Home", "onCreate");
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		// toolBar = (Toolbar) findViewById(R.id.app_bar);
		// setSupportActionBar(toolBar);

		FragmentNavigationDrawer drawerFragment = (FragmentNavigationDrawer) getSupportFragmentManager()
				.findFragmentById(R.id.left_slide);

		dr = drawerFragment.setUp(R.id.left_slide,
				(DrawerLayout) findViewById(R.id.drawer_layout));

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		Intent intent = getIntent();
		Bundle extras = null;
		if ((extras = intent.getExtras()) != null) {
			if (extras.containsKey("conversationId")) {
				FragmentTransaction transaction = getSupportFragmentManager()
						.beginTransaction();
				ConversationFragment conversation = new ConversationFragment();
				Bundle bundle = new Bundle();
				bundle.putString("conversationId",
						extras.getString("conversationId"));
				conversation.setArguments(bundle);
				transaction.add(R.id.fragment_container, conversation,
						"conversation");
				transaction.addToBackStack("ContactList");
				transaction.commit();
				return;
			}
		}
		createFragmentContent(savedInstanceState);
	
	}

	private void createFragmentContent(Bundle inBundle) {
		if (findViewById(R.id.fragment_container) != null) {
			if (inBundle != null) {
				return;
			}
			Fragment friendsFragment = new FriendsFragment();
			friendsFragment.setArguments(getIntent().getExtras());
			getSupportFragmentManager().beginTransaction()
					.add(R.id.fragment_container, friendsFragment).commit();

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		dr.onOptionsItemSelected(item);
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
		ActivitiesManager.getInstance().setCurrentActivity(this);
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		ActivitiesManager.getInstance().removeCurrentActivity(this);
	}

	private boolean close = false;

	@Override
	public void onBackPressed() {
		if (close) {
			this.finish();
			return;
		}
		// FragmentManager manager = getSupportFragmentManager();
		// if(manager.getBackStackEntryCount()>0){
		Fragment fragmet = getSupportFragmentManager().findFragmentById(
				R.id.fragment_container);
		if (!(fragmet instanceof FriendsFragment)) {
			FragmentTransaction fragmentTransaction = this
					.getSupportFragmentManager().beginTransaction();

			fragmentTransaction.setCustomAnimations(R.anim.slide_in_left,
					R.anim.slide_out_right);
			fragmentTransaction.replace(R.id.fragment_container,
					new FriendsFragment());
			//
			fragmentTransaction.addToBackStack(null);
			fragmentTransaction.commit();
		} else {
			Snackbar snackbar = HeliSnackbars.showConfirmBeforeCloseApp(this)
					.eventListener(new EventListener() {

						@Override
						public void onShown(Snackbar snackbar) {

						}

						@Override
						public void onShow(Snackbar snackbar) {
							close = true;
						}

						@Override
						public void onDismissed(Snackbar snackbar) {
							close = false;
						}

						@Override
						public void onDismiss(Snackbar snackbar) {

						}
					});
			SnackbarManager.show(snackbar, this);
		}
		// }else{
		// finish();
		// }

	}

}
