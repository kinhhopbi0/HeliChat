package com.pdv.heli.activity.startup;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.pdv.heli.R;
import com.pdv.heli.activity.BaseActivity;
import com.pdv.heli.manager.MessageQueueProcessor;
import com.pdv.heli.message.detail.ConfirmPasscodeMsg;

public class ConfirmPasscodeActivity extends BaseActivity implements
		OnClickListener {
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
			MessageQueueProcessor.getInstance().offerOutMessage(msg);
		}

	}

	public void onConfirmFromServer(ConfirmPasscodeMsg detail) {
		btnOk.setEnabled(true);
		switch (detail.getStatus()) {
		case ConfirmPasscodeMsg.Status.SUCCESS:
			// TODO start activity
			break;
		case ConfirmPasscodeMsg.Status.NOT_MATCHES:
			btnOk.setText(getResources().getString(R.string.ok));
			edtPasscode.setError(getResources().getString(R.string.passcode_not_matches));
			edtPasscode.requestFocus();
			break;
		case ConfirmPasscodeMsg.Status.OTHER_ERROR:
			Toast.makeText(this, "Unknow error", Toast.LENGTH_SHORT).show();
			break;
		default:
			break;
		}
	}

}
