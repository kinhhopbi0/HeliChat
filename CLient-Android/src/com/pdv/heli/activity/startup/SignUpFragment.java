package com.pdv.heli.activity.startup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.pdv.heli.R;
import com.pdv.heli.manager.MessageQueueProcessor;
import com.pdv.heli.manager.TcpClientManager;
import com.pdv.heli.message.common.MessageMode;
import com.pdv.heli.message.detail.SignUpMessage;
import com.pdv.heli.util.PasswordValidator;

public class SignUpFragment extends Fragment implements OnEditorActionListener,
		OnClickListener {
	public static final String TAG = SignUpFragment.class.getSimpleName();
	private EditText edtPhone;
	private Spinner spnContryCode;
	// private EditText edtUsername;
	private EditText edtPassword;
	private EditText edtRePassword;
	private Button btnSignUp;
	private ProgressDialog progressDialog;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.fragment_signup, container, false);
		initializeComponent(view);
		return view;
	}

	private void initializeComponent(View pLayout) {
		edtPhone = (EditText) pLayout.findViewById(R.id.edtPhone);
		spnContryCode = (Spinner) pLayout.findViewById(R.id.spnContryCode);
		// edtUsername = (EditText) pLayout.findViewById(R.id.edtUsername);
		edtPassword = (EditText) pLayout.findViewById(R.id.edtPassword);
		edtRePassword = (EditText) pLayout.findViewById(R.id.edtRePassword);
		btnSignUp = (Button) pLayout.findViewById(R.id.btnSignUp);

		edtRePassword.setOnEditorActionListener(this);
		btnSignUp.setOnClickListener(this);
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		doSignUp();
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnSignUp:
			doSignUp();
			break;
		default:
			break;
		}
	}

	private String user_phone;
	private String user_pass;
	private void doSignUp() {
		if (validate()) {
			progressDialog = ProgressDialog.show(this.getActivity(),
					getResources().getString(R.string.please_wait),
					getResources()
							.getString(R.string.requesting_create_account),
					true, false);
			String password = edtPassword.getText().toString();
			String phone = edtPhone.getText().toString();
			String code = spnContryCode.getSelectedItem().toString();

			SignUpMessage accountMessage = new SignUpMessage(MessageMode.SEND);
			user_phone = phone;
			accountMessage.setPhone(user_phone);
			accountMessage.setStatus(SignUpMessage.Status.CREATE_NEW);
			user_pass = password;
			accountMessage.setPassword(user_pass);
			MessageQueueProcessor.getInstance().offerOutMessage(accountMessage);

		}
	}

	private boolean validate() {
		if (TcpClientManager.getInstance().getConnectState() != TcpClientManager.State.READY) {
			Toast.makeText(getActivity(),
					getResources().getString(R.string.connection_error),
					Toast.LENGTH_SHORT).show();
			return false;
		}
		String phone;
		if ((phone = edtPhone.getText().toString()).length() == 0) {
			edtPhone.setError(getResources().getString(
					R.string.phone_number_not_empty));
			edtPhone.requestFocus();
			return false;
		}
		if (phone.length() > 13 || phone.length() < 10) {
			edtPhone.setError(getResources().getString(
					R.string.phone_number_notcorrect));
			edtPhone.requestFocus();
			return false;
		}
		String password1;
		if ((password1 = edtPassword.getText().toString()).length() == 0) {
			edtPassword.setError(getResources().getString(
					R.string.password_not_empty));
			edtPassword.requestFocus();
			return false;
		}
		if (!new PasswordValidator().validate(password1)) {
			edtPassword.setError(getResources().getString(
					R.string.password_not_correct));
			edtPassword.requestFocus();
			return false;
		}
		if (!password1.equals(edtRePassword.getText().toString())) {
			edtRePassword.setError(getResources().getString(
					R.string.password_not_));
			edtRePassword.requestFocus();
			return false;
		}

		return true;
	}

	public void showPhoneExist() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
		edtPhone.setError(getResources().getString(
				R.string.phone_number_readly_exist));
		edtPhone.requestFocus();
	}

	public void doResponseOtherError() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}

	public void createStepOneSuccess() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
		Intent intent = new Intent(this.getActivity(),
				ConfirmVerifyActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString(ConfirmVerifyActivity.PHONE_KEY, user_phone);
		bundle.putString(ConfirmVerifyActivity.PASSWORD_KEY,this.user_pass);
		intent.putExtras(bundle);
		startActivity(intent);
	}
}
