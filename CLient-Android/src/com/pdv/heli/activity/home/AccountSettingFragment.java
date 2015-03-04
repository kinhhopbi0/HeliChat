package com.pdv.heli.activity.home;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pdv.heli.R;

public class AccountSettingFragment extends Fragment {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_account_setting, container,
				false);
		
		return view;
	}
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {		
		super.onCreateOptionsMenu(menu, inflater);
		menu.clear();
		inflater.inflate(R.menu.test, menu);
	}
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

}
