package com.pdv.heli.activity.startup;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;
import com.nispok.snackbar.listeners.ActionClickListener;
import com.pdv.heli.R;
import com.pdv.heli.component.HeliSnackbars;
import com.pdv.heli.controller.AccountController;



public class SignInFragment extends android.support.v4.app.Fragment implements
		OnClickListener, OnEditorActionListener {
	private static final String CACHE_FILE_NAME = "sigin_cache";
	private EditText edtPhone;
	private Spinner spnContryCode;
	
	private EditText edtPassword;

	private Button btnSignIn;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		Log.v(getTag(), "Create view fragment");
		View layout = inflater.inflate(R.layout.fragment_signin, container,
				false);
		initializeComponent(layout);
		return layout;
	}

	private void initializeComponent(View pLayout) {
		edtPhone = (EditText) pLayout.findViewById(R.id.edtPhone);
		spnContryCode = (Spinner) pLayout.findViewById(R.id.spnContryCode);
		//edtUsername = (EditText) pLayout.findViewById(R.id.edtUsername);
		edtPassword = (EditText) pLayout.findViewById(R.id.edtPassword);
		btnSignIn = (Button) pLayout.findViewById(R.id.btnSignIn);
		btnSignIn.setOnClickListener(this);
		edtPassword.setOnEditorActionListener(this);
		edtPassword.setTypeface(Typeface.DEFAULT);
		SharedPreferences  preferences = getActivity().getSharedPreferences(CACHE_FILE_NAME,0);
		edtPhone.setText(preferences.getString("last_sign_phone", ""));
	}

	@Override
	public void onResume() {
		Log.v(getTag(), "resume fragment");
		super.onResume();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.v(getTag(), "Create fragment");
		super.onCreate(savedInstanceState);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnSignIn:				
			doSignIn();
			break;		
		default:
			break;
		}
	}

	private void doSignIn() {
		if(!validateInput()){
			return;
		}		
		String password = edtPassword.getText().toString();			
		String phone = edtPhone.getText().toString();
		String fullPhone = phone;
		SharedPreferences  preferences = getActivity().getSharedPreferences(CACHE_FILE_NAME,0);
		preferences.edit().putString("last_sign_phone", fullPhone).commit();
		
		AccountController.requestSignInByPwd(fullPhone, password, getActivity());
		
	}

	private boolean validateInput() {
		if(edtPhone.getText().length()==0){
			edtPhone.setError("phone not empty");
			edtPhone.requestFocus();
			return false;
		}
		if(edtPassword.getText().length()==0){
			edtPassword.setError("password not empty");
			edtPassword.requestFocus();
			return false;
		}
		return true;
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (actionId == EditorInfo.IME_ACTION_DONE) {
			doSignIn();
			return true;
		}
		return false;
	}

	@Override
	public void onPause() {
		Log.v(getTag(), "pause fragment");
		super.onPause();
	}

	@Override
	public void onDestroy() {
		Log.v(getTag(), "fragment destroy");
		super.onDestroy();
	}

	
}
