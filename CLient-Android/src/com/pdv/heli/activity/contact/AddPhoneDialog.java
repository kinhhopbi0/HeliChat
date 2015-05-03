package com.pdv.heli.activity.contact;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.pdv.heli.R;
import com.pdv.heli.controller.ContactController;
import com.pdv.heli.controller.ConversationController;
import com.pdv.heli.manager.MessageQueue;
import com.pdv.heli.message.detail.LinearStringMessage;
import com.pdv.heli.model.Contact;
import com.pdv.heli.model.Conversation;

public class AddPhoneDialog extends DialogFragment implements
		OnEditorActionListener {

	public static final String RESPONSE_ADD_ACTION = AddPhoneDialog.class.getName()
			+ "." + "RESPONSE_ADD_ACTION";
	public static final String RESPONSE_FIDN_ACTION = AddPhoneDialog.class.getName()
			+ "." + "RESPONSE_FIDN_ACTION";

	private EditText edtNumber;
	// private View progress_loading;
	private EditText edtName;
	@SuppressWarnings("unused")
	private FriendsFragment container;
	private BroadcastReceiver receiver;
	private TextView responseText;
	private static boolean isShow;
	private Contact contact;
	private MaterialDialog coreDialog;
	View btnCancel;
	View btnFind;
	View btnAdd;

	@SuppressLint("InflateParams")
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		setRetainInstance(true);

		LayoutInflater inflater = getActivity().getLayoutInflater();
		View root = inflater.inflate(R.layout.dialog_add_friend_number, null);

		edtNumber = (EditText) root.findViewById(R.id.phone);
		// progress_loading = root.findViewById(R.id.progress_loading);

		responseText = (TextView) root.findViewById(R.id.responseText);
		responseText.setVisibility(View.VISIBLE);
		edtName = (EditText) root.findViewById(R.id.name);
		edtNumber.setOnEditorActionListener(this);

		MaterialDialog.Builder builder2 = new MaterialDialog.Builder(
				getActivity()).title("Add new contact").customView(root, true)
				.positiveText("add").neutralText("Find")
				.negativeText("cancel").autoDismiss(false).cancelable(false)
				.callback(new MaterialDialog.ButtonCallback() {
					@Override
					public void onNegative(MaterialDialog dialog) {
						dialog.dismiss();
					}

					@Override
					public void onNeutral(MaterialDialog dialog) {

						if (validate()) {
							String pn = edtNumber.getText().toString();
							String name = edtName.getText().toString();
							ContactController.callFindFone(pn, name);
							btnFind.setEnabled(false);
							btnAdd.setEnabled(false);
							dialog.setActionButton(DialogAction.NEUTRAL,
									"finding...");
						}

					}

					// add btn
					@Override
					public void onPositive(MaterialDialog dialog) {
						if (validate()) {
							String pn = edtNumber.getText().toString();
							String name = edtName.getText().toString();
							ContactController.callPushContact(pn, name);
							ConversationController.callRequestSyncConversation();
							btnFind.setEnabled(false);
							btnAdd.setEnabled(false);
							dialog.setActionButton(DialogAction.POSITIVE,
									"Adding...");
						}
					}
				});

		 coreDialog = builder2.build();
		btnCancel = coreDialog.getActionButton(DialogAction.NEGATIVE);
		btnFind = coreDialog.getActionButton(DialogAction.NEUTRAL);
		btnAdd = coreDialog.getActionButton(DialogAction.POSITIVE);
		return coreDialog;
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		super.onDismiss(dialog);
	}

	public void setUp(FriendsFragment fragment) {
		container = fragment;

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				Bundle extras = null;
				if (action.equals(AddPhoneDialog.RESPONSE_FIDN_ACTION)) {
					extras = intent.getExtras();
					JSONObject resObj;
					try {
						resObj = new JSONObject(extras.getString("json"));
						String pn = resObj.getString("pn");
						boolean found = resObj.getBoolean("found");
						btnFind.setEnabled(true);						
						btnAdd.setEnabled(true);
						coreDialog.setActionButton(DialogAction.NEUTRAL,
								"find");
						if(found){							
							responseText.setText("Number "+pn+" found, you can add contact now");
						}else{
							responseText.setText("Number "+pn+" not found");
						}
					} catch (JSONException e) {						
						e.printStackTrace();
					}
					
				}
				if(action.equals(RESPONSE_ADD_ACTION)){
					boolean success = intent.getBooleanExtra("com.heli.addPhone.success", false);
					btnFind.setEnabled(true);						
					btnAdd.setEnabled(true);
					coreDialog.setActionButton(DialogAction.POSITIVE,
							"Add");
					if(success){
						responseText.setText("Add or update contact success");
						ContactController.callRequestSyncContact();
					}else{
						responseText.setText("Add or update contact fail");
					}
					
				}
			}
		};
		IntentFilter filter = new IntentFilter();
		filter.addAction(RESPONSE_ADD_ACTION);
		filter.addAction(RESPONSE_FIDN_ACTION);
		getActivity().registerReceiver(receiver, filter);
	}

	@Override
	public void onResume() {
		Log.v("test", "dialog resume");
		isShow = true;
		super.onResume();
	}

	@Override
	public void onPause() {
		isShow = false;
		super.onPause();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onDestroy() {
		getActivity().unregisterReceiver(receiver);
		super.onDestroy();
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	public static boolean isShow() {
		return isShow;
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (v.getId() == R.id.phone) {
			if (actionId == EditorInfo.IME_ACTION_DONE) {
				sendRequest();
				return true;
			}
		}
		return false;
	}

	private boolean validate() {
		responseText.setText("");
		if (edtNumber.getText().length() == 0) {
			edtNumber.setError("Phone number must not empty");
			return false;
		}
		return true;
	}

	private void sendRequest() {
		validate();

		edtNumber.setEnabled(false);
		// progress_loading.setVisibility(View.VISIBLE);
		String contactName = edtName.getText().toString();
		LinearStringMessage findFriendMsg = new LinearStringMessage();
		findFriendMsg.setController("Relationship");
		findFriendMsg.setAction("AddPhoneNumber");
		findFriendMsg.putParam("pn", edtNumber.getText().toString());
		findFriendMsg.putParam("name", contactName);

		// create in sqlite database
		contact = new Contact();
		contact.setPhoneNumber(edtNumber.getText().toString());
		contact.setContactName(contactName);
		// Send add contact to server
		MessageQueue.getInstance()
				.offerOutMessage(findFriendMsg, getActivity());
	}
}
