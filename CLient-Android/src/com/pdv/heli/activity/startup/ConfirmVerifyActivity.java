package com.pdv.heli.activity.startup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.pdv.heli.R;
import com.pdv.heli.activity.BaseActivity;
import com.pdv.heli.manager.MessageQueue;
import com.pdv.heli.message.detail.ConfirmPasscodeMsg;

public class ConfirmVerifyActivity extends BaseActivity implements
		OnClickListener {
	public static final String PHONE_KEY = "phone";
	public static final String PASSWORD_KEY = "password";
	private Button btnOk;
	private EditText edtPasscode;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_confirm_passcode);
		edtPasscode = (EditText) findViewById(R.id.edtPasscode);
		btnOk = (Button) findViewById(R.id.btnOk);
		btnOk.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnOk) {
			String passcode = edtPasscode.getText().toString();
			btnOk.setEnabled(false);
			btnOk.setText("Submiting...");
			ConfirmPasscodeMsg msg = new ConfirmPasscodeMsg();
			try {
				msg.setPasscode(passcode);
			} catch (Exception e) {
				edtPasscode.setError(e.toString());
				edtPasscode.requestFocus();
				return;
			}
			msg.setStatus(ConfirmPasscodeMsg.Status.CONFIRM);
			//MessageQueue.getInstance().offerOutMessage(msg);
		}

	}

	public void onConfirmFromServer(ConfirmPasscodeMsg detail) {
		btnOk.setEnabled(true);
		btnOk.setText(getResources().getString(R.string.ok));
		switch (detail.getStatus()) {
		case ConfirmPasscodeMsg.Status.SUCCESS:
			Intent intent = new Intent(this,FinishSignUpActivity.class);
			Bundle bundle = getIntent().getExtras();
			if(bundle != null){				
				intent.putExtras(bundle);
				startActivity(intent);
			}else{
				Toast.makeText(this, "Error", Toast.LENGTH_SHORT);
			}
			
			this.finish();
			break;
		case ConfirmPasscodeMsg.Status.NOT_MATCHES:			
			edtPasscode.setError(getResources().getString(R.string.passcode_not_matches));
			edtPasscode.requestFocus();
			edtPasscode.selectAll();
			break;
		case ConfirmPasscodeMsg.Status.OTHER_ERROR:
			Toast.makeText(this, "Unknow error", Toast.LENGTH_SHORT).show();
			break;
		case ConfirmPasscodeMsg.Status.ACCOUNT_EXIST:
			Toast.makeText(this, "Phone number is exist", Toast.LENGTH_SHORT).show();			
			break;
		default:
			break;
		}
	}

	

}
