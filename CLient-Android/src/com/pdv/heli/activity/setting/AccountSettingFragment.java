package com.pdv.heli.activity.setting;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;

import com.pdv.heli.R;
import com.pdv.heli.activity.home.FragmentNavigationDrawer;
import com.pdv.heli.manager.MessageQueue;
import com.pdv.heli.message.detail.LinearStringMessage;

public class AccountSettingFragment extends Fragment implements OnClickListener {
	private EditText displayName;
	private EditText dateOfbirth;
	private RadioGroup rdgGender;
	private String mPassword;
	private View vPasswordItem;
	private View vAvatarItem;
	private View vWallPictureItem;
	private boolean isShowPasswordDialog;
	private ImageButton btnSaveName;
	public static final String UPDATE_PASSWORD_DIALOG_TAG = "updatePasswordDialog";
	private DatePickerDialog datePickerDialog;
	private static final int SELECT_PICTURE_AVATAR = 1;

	DialogUpdatePassword dialogUpdatePassword;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		if (savedInstanceState != null) {

		}
	}
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		getActivity().setTitle("Account settings");
	}
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	private void initializeView(View root) {
		displayName = (EditText) root.findViewById(R.id.edt_displayName);
		dateOfbirth = (EditText) root.findViewById(R.id.edt_dob);
		rdgGender = (RadioGroup) root.findViewById(R.id.rg_gender);
		rdgGender
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {

					}
				});
		vPasswordItem = root.findViewById(R.id.layout_password);
		vAvatarItem = root.findViewById(R.id.layout_avatar);
		vWallPictureItem = root.findViewById(R.id.layout_wallPicture);

		btnSaveName = (ImageButton) root.findViewById(R.id.bt_save_name);

		displayName.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() > 0) {
					btnSaveName.setVisibility(View.VISIBLE);
				} else {
					btnSaveName.setVisibility(View.GONE);
				}
			}
		});
		dateOfbirth.setOnClickListener(this);
		dateOfbirth.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					showUpdateDateDlg();
				}
			}
		});
		btnSaveName.setOnClickListener(this);
		vPasswordItem.setOnClickListener(this);
		vAvatarItem.setOnClickListener(this);
		vWallPictureItem.setOnClickListener(this);

	}

	protected void showUpdateDateDlg() {
		Calendar now = Calendar.getInstance();
		String dateStr = dateOfbirth.getText().toString();
		SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date date = format1.parse(dateStr);
			now.setTime(date);
		} catch (ParseException e) {

		}
		datePickerDialog = new DatePickerDialog(getActivity(),
				new DatePickerDialog.OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {

					}
				}, now.get(Calendar.YEAR), now.get(Calendar.MONTH),
				now.get(Calendar.DAY_OF_MONTH));
		datePickerDialog.setButton(DatePickerDialog.BUTTON_POSITIVE, "OK",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						updateDOB();
					}
				});
		datePickerDialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, "Cancel",
				datePickerDialog);
		datePickerDialog.show();

	}

	private void updateDOB() {
		int year = datePickerDialog.getDatePicker().getYear();
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, datePickerDialog.getDatePicker().getMonth());
		cal.set(Calendar.DAY_OF_MONTH, datePickerDialog.getDatePicker()
				.getDayOfMonth());
		SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
		String strTime = format1.format(cal.getTime());
		AccountSettingFragment.this.dateOfbirth.setText(strTime);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_account_setting,
				container, false);
		initializeView(view);

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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.layout_password:
			if (dialogUpdatePassword == null) {
				dialogUpdatePassword = new DialogUpdatePassword();
			}
			dialogUpdatePassword.show(
					getActivity().getSupportFragmentManager(),
					UPDATE_PASSWORD_DIALOG_TAG);

			isShowPasswordDialog = true;
			break;
		case R.id.bt_save_name:
			LinearStringMessage updatePersonInfoMessage = new LinearStringMessage();
			updatePersonInfoMessage.putParam("displayName", this.displayName.getText().toString());
			MessageQueue.getInstance().offerOutMessage(updatePersonInfoMessage,null);
			break;
		case R.id.edt_dob:
			showUpdateDateDlg();
			break;
		case R.id.layout_avatar:
			selectAvatar();
			break;

		case R.id.layout_wallPicture:

			break;
		default:
			break;
		}

	}

	private void selectAvatar() {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent, "Select Picture"),
				SELECT_PICTURE_AVATAR);

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == SELECT_PICTURE_AVATAR) {
				Uri selectedImageUri = data.getData();
				File f = new File(selectedImageUri.getPath());
				Bitmap bmp = BitmapFactory.decodeFile(f.getAbsolutePath());
				
				
				FragmentNavigationDrawer drawer = (FragmentNavigationDrawer) getActivity()
						.getSupportFragmentManager().findFragmentById(
								R.id.left_slide);
				if(drawer != null){
					drawer.updateUserAvatar(bmp);
				}
			}
		}
	}

	public class DialogUpdatePassword extends DialogFragment {
		private EditText etNewPasswod;
		private EditText etOldPasswod;
		private EditText etRePasswod;
		public static final String KEY_OLD_PASSWORD = "OLD";
		public static final String KEY_NEW_PASSWORD = "NEW";
		public static final String KEY_RE_PASSWORD = "RE";

		@Override
		public void onStart() {
			super.onStart();
			AlertDialog d = (AlertDialog) getDialog();
			if (d != null) {
				Button positiveButton = (Button) d
						.getButton(Dialog.BUTTON_POSITIVE);
				positiveButton.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						if (validateInput()) {
							// TODO send message update password
						}

					}
				});
			}
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			super.onCreateDialog(savedInstanceState);
			setRetainInstance(true);
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			LayoutInflater inflater = getActivity().getLayoutInflater();
			View root = inflater.inflate(R.layout.dialog_change_password, null);
			etNewPasswod = (EditText) root.findViewById(R.id.password_new);
			etNewPasswod.setTypeface(Typeface.DEFAULT);
			etNewPasswod.setTransformationMethod(new PasswordTransformationMethod());
			
			etOldPasswod = (EditText) root.findViewById(R.id.password_curent);
			etOldPasswod.setTypeface(Typeface.DEFAULT);
			etOldPasswod.setTransformationMethod(new PasswordTransformationMethod());
			
			etRePasswod = (EditText) root.findViewById(R.id.password_re);
			etRePasswod.setTypeface(Typeface.DEFAULT);
			etRePasswod.setTransformationMethod(new PasswordTransformationMethod());
			if (savedInstanceState != null) {
				etOldPasswod.setText(savedInstanceState
						.getString(KEY_OLD_PASSWORD));
				etNewPasswod.setText(savedInstanceState
						.getString(KEY_NEW_PASSWORD));
				etRePasswod.setText(savedInstanceState
						.getString(KEY_RE_PASSWORD));
			}
			builder.setView(root);
			builder.setPositiveButton("Update", null);
			builder.setNegativeButton("Cancel", null);
			builder.setTitle("Change you signin password");
			AlertDialog dialog = builder.create();

			return dialog;
		}
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			return super.onCreateView(inflater, container, savedInstanceState);
		}
		private boolean validateInput() {
			if (etOldPasswod.getText().length() == 0) {
				etOldPasswod.setError("this field must not empty");
				etOldPasswod.requestFocus();
				return false;
			}
			if (etNewPasswod.getText().length() == 0) {
				etNewPasswod.setError("this field must not empty");
				etNewPasswod.requestFocus();
				return false;
			}
			if (!etRePasswod.getText().toString()
					.equals(etNewPasswod.getText().toString())) {
				etRePasswod.setError("not matches");
				etRePasswod.requestFocus();
				return false;
			}
			return true;
		}

		@Override
		public void onDismiss(DialogInterface dialog) {
			// TODO Auto-generated method stub
			super.onDismiss(dialog);
			isShowPasswordDialog = false;
		}

		@Override
		public void onSaveInstanceState(Bundle outState) {
			outState.putString(KEY_OLD_PASSWORD, etOldPasswod.getText()
					.toString());
			outState.putString(KEY_NEW_PASSWORD, etNewPasswod.getText()
					.toString());
			outState.putString(KEY_RE_PASSWORD, etRePasswod.getText()
					.toString());
			super.onSaveInstanceState(outState);
		}

		@Override
		public void onDestroyView() {
			if (getDialog() != null && getRetainInstance())
				getDialog().setDismissMessage(null);
			super.onDestroyView();
		}

	}

}
