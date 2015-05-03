package com.pdv.heli.activity.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.pdv.heli.R;
import com.pdv.heli.activity.contact.FriendsFragment;
import com.pdv.heli.activity.home.DrawerRecyclerViewAdapter.IOnClickItem;
import com.pdv.heli.activity.setting.AccountSettingFragment;
import com.pdv.heli.activity.setting.AppSettingActivity;
import com.pdv.heli.activity.startup.StartFirstActivity;
import com.pdv.heli.controller.AccountController;
import com.pdv.heli.manager.SharedPreferencesManager;
import com.pdv.heli.model.DrawerMenuItem;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

public class FragmentNavigationDrawer extends Fragment implements IOnClickItem {

	public static final String PREF_FILE_NAME = "NavigationDrawerCache";
	public static final String KEY_USER = "USER";

	private ActionBarDrawerToggle drawerToggle;
	private DrawerLayout drawerLayout;
	private boolean mUser;
	private boolean mFromInstancestate;
	private View containerFragmetViewTag;
	private RecyclerView recyclerView;
	private DrawerRecyclerViewAdapter adapter;
	private ImageView imvPanel;
	private ImageView imvAvatar;
	private FragmentTransaction fragmentTransaction; 
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mUser = Boolean.valueOf(readPRE(this.getActivity(), KEY_USER, "false"));
		if (savedInstanceState != null) {
			mFromInstancestate = true;
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i("Drawer fragment", "onCreateV start");
		View layout = inflater.inflate(R.layout.fragment_drawer, container,
				false);
		recyclerView = (RecyclerView) layout.findViewById(R.id.drawer_list);
		adapter = new DrawerRecyclerViewAdapter(getActivity(),
				DrawerMenuItem.getDefaultItems());
		recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
		imvPanel = (ImageView) layout.findViewById(R.id.imvPanel);
		imvAvatar = (ImageView) layout.findViewById(R.id.imvAvatar);
		recyclerView.setAdapter(adapter);
		
		recyclerView
				.addItemDecoration(new HorizontalDividerItemDecoration.Builder(
						getActivity()).visibilityProvider(adapter)
						.marginProvider(adapter).build());
		adapter.setOnItemClickListener(this);
		Log.i("Drawer fragment", "onCreateV end");
		return layout;
	}

	public ActionBarDrawerToggle setUp(int fragmentId, DrawerLayout drawerLayout) {
		containerFragmetViewTag = getActivity().findViewById(fragmentId);
		this.drawerLayout = drawerLayout;
		drawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout,
				 R.string.drawer_open, R.string.drawer_close) {
			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				if (!mUser) {
					mUser = true;
					saveToPrefer(getActivity(), KEY_USER, mUser + "");
				}
				getActivity().invalidateOptionsMenu();
				InputMethodManager inputMethodManager = (InputMethodManager) getActivity()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				inputMethodManager.hideSoftInputFromWindow(getActivity()
						.getCurrentFocus().getWindowToken(), 0);
			}

			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
				getActivity().invalidateOptionsMenu();
				if(fragmentTransaction != null && isNewFragment){
					fragmentTransaction.commit();
					isNewFragment = false;
				}
				
			}

		};
		if (!mUser && !mFromInstancestate) {
			this.drawerLayout.openDrawer(containerFragmetViewTag);
		}
		drawerLayout.setDrawerListener(drawerToggle);
		drawerLayout.post(new Runnable() {
			@Override
			public void run() {
				drawerToggle.syncState();
			}
		});
		return drawerToggle;
	}

	public static void saveToPrefer(Context context, String name, String value) {

		SharedPreferences preferences = context.getSharedPreferences(
				PREF_FILE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(name, value);
		editor.apply();
	}

	public static String readPRE(Context context, String name, String value) {

		SharedPreferences preferences = context.getSharedPreferences(
				PREF_FILE_NAME, Context.MODE_PRIVATE);
		return preferences.getString(name, value);
	}

	@Override
	public void onItemClick(View v, int pos) {

		DrawerMenuItem model = adapter.getItemModel(pos);
		int item_id = model.getId();
		switch (item_id) {
		case DrawerMenuItem.ACTION_LOG_OUT:
			AccountController.doSignOut();
			Intent intent = new Intent(getActivity(), StartFirstActivity.class);
			startActivity(intent);	
			break;
		case DrawerMenuItem.ITEM_ACCOUNT_SETTING:
			replaceMainFragment(new AccountSettingFragment());
			break;
		case DrawerMenuItem.ITEM_ALL_CONTACT:
			replaceMainFragment(new FriendsFragment());
			break;
		case DrawerMenuItem.ITEM_APP_SETTING:
			Intent intent2 = new Intent(getActivity(), AppSettingActivity.class);
			startActivity(intent2);
			break;
		default:
			break;
		}

		adapter.setSelectedPosition(pos);
		drawerLayout.closeDrawer(containerFragmetViewTag);
	}

	private boolean isNewFragment = false;
	public void replaceMainFragment(Fragment newFragemet) {
		Fragment currentFragment = getFragmentManager().findFragmentById(R.id.fragment_container);
		if (currentFragment.getClass().equals(newFragemet.getClass())){
			
			return;
		}
		fragmentTransaction = getActivity()
				.getSupportFragmentManager().beginTransaction();
		fragmentTransaction.replace(R.id.fragment_container, newFragemet);
		fragmentTransaction.addToBackStack(null);	
		isNewFragment = true;		
	}

	public void updateUserAvatar(Bitmap avatar) {
		imvAvatar.setImageBitmap(avatar);

	}
}
