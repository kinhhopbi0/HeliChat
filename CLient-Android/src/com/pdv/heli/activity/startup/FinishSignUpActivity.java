package com.pdv.heli.activity.startup;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.pdv.heli.R;
import com.pdv.heli.activity.BaseActivity;
import com.pdv.heli.manager.MessageQueue;
import com.pdv.heli.message.detail.LinearStringMessage;

public class FinishSignUpActivity extends BaseActivity implements
		OnClickListener {
	private Button btnSignIn;
	private boolean isWaittingServer = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_finish_signup);
		btnSignIn = (Button) findViewById(R.id.btnSignIn);
		btnSignIn.setOnClickListener(this);
	}

	@Override
	public void onBackPressed() {
		if(isWaittingServer){
			return;
		}else{
			super.onBackPressed();
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == btnSignIn.getId()) {
			Bundle bundle = getIntent().getExtras();
			String phone = bundle.getString(ConfirmVerifyActivity.PHONE_KEY);
			String password = bundle
					.getString(ConfirmVerifyActivity.PASSWORD_KEY);
		
			isWaittingServer = true;
			btnSignIn.setText("Signing...");
			LinearStringMessage siginMsg = new LinearStringMessage();
			siginMsg.setAction("SignIn");
			siginMsg.setController("Account");
			siginMsg.putParam("pn", phone);
			siginMsg.putParam("pwd", password);		
			MessageQueue.getInstance().offerOutMessage(siginMsg, this);
			
		}
	}
	

}
