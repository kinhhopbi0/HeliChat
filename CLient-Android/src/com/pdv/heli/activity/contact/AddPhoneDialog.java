package com.pdv.heli.activity.contact;

import android.app.AlertDialog;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.pdv.heli.R;
import com.pdv.heli.manager.MessageQueue;
import com.pdv.heli.message.detail.LinearStringMessage;

public class AddFriendDialog extends DialogFragment implements OnEditorActionListener{
	
	public static final String DISSMIS_ACTION = AddFriendDialog.class.getName()+"."+"DISSMIS_ACTION";
	public static final String RESPONSE_ACTION = AddFriendDialog.class.getName()+"."+"RESPONSE_ACTION";
	
	private EditText edtNumber;
	private View progress_loading;
	private TextView tv_dialog_title;
	private FriendsFragment container;
	private BroadcastReceiver reciver;
	private TextView responseText;
	private static boolean isShow;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {			
		setRetainInstance(true);				
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View root = inflater.inflate(R.layout.dialog_add_friend_number, null);
		edtNumber = (EditText) root.findViewById(R.id.phone);
		progress_loading =  root.findViewById(R.id.progress_loading);
		tv_dialog_title = (TextView) root.findViewById(R.id.tv_dialog_title);
		responseText = (TextView) root.findViewById(R.id.responseText);
		edtNumber.setOnEditorActionListener(this);
		builder.setView(root);
		builder.setPositiveButton("Add", null);
		builder.setNegativeButton("Dismis", null);
		AlertDialog dialog = builder.create();
		Log.v("test", "dialog create dialog");
		
		return dialog;
	}
	@Override
	public void onDismiss(DialogInterface dialog) {
		Log.v("test", "dialog dissmis");
		super.onDismiss(dialog);
	}
	public void setUp(FriendsFragment fragment){
		container = fragment;
		
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.v("test", "dialog create");
		super.onCreate(savedInstanceState);
		reciver = new BroadcastReceiver() {			
			@Override
			public void onReceive(Context context, Intent intent) {
				if(intent.getAction().equals(DISSMIS_ACTION)){
					dismissAllowingStateLoss();
					return;
				}
				if(intent.getAction().equals(RESPONSE_ACTION)){
					String state = intent.getExtras().getString("state");
					String requestPhone =  intent.getExtras().getString("phone");
					String notifiText;
					switch (state) {
					case "notFound":
						notifiText = "Phone number " + requestPhone
								+ " not use Helli";
						break;
					case "requested":
						notifiText = "Request friend with " + requestPhone
								+ " success";
						break;
					case "requestFail":
						notifiText = "Request friend with " + requestPhone
								+ " fail";
						break;
					case "requestBefore":
						notifiText = "You readly request " + requestPhone;
						break;
					case "readyFriend":
						notifiText = "You and " + requestPhone
								+ " is readly friend";
						break;
					case "accepted":
						notifiText = "You and " + requestPhone
						+ " is friend now";
						break;
					default:
						notifiText = "Request friend with " + requestPhone
								+ " undefined error";
						break;
					}
					responseText.setText(notifiText);
					responseText.setVisibility(View.VISIBLE);
					edtNumber.setEnabled(true);
					positiveButton.setEnabled(true);
					progress_loading.setVisibility(View.GONE);
					tv_dialog_title.setText("Add your friend phone number");
				}
			}
		};
		IntentFilter filter = new IntentFilter();
		filter.addAction(DISSMIS_ACTION);
		filter.addAction(RESPONSE_ACTION);
		
		getActivity().registerReceiver(reciver, filter);
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
		Log.v("test", "dialog stop");
		super.onStop();
	}
	
	@Override
	public void onDestroy() {
		Log.v("test", "dialog destroy");
		getActivity().unregisterReceiver(reciver);
		super.onDestroy();
	}
	private Button positiveButton;
	@Override
	public void onStart() {		
		super.onStart();
		AlertDialog d = (AlertDialog) getDialog();
		if (d != null) {
			positiveButton = (Button) d
					.getButton(Dialog.BUTTON_POSITIVE);
			positiveButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {					
					sendRequest();					
				}
			});
		}
	}
	public static boolean isShow() {
		return isShow;
	}
	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if(v.getId() == R.id.phone){
			if(actionId == EditorInfo.IME_ACTION_DONE){				
				sendRequest();
				return true;
			}
		}
		return false;
	}
	
	private void sendRequest(){
		positiveButton.setEnabled(false);
		responseText.setText("");
		if(edtNumber.getText().length() == 0){
			edtNumber.setError("Phone number must not empty");
			return;
		}
		
		edtNumber.setEnabled(false);
		progress_loading.setVisibility(View.VISIBLE);
		tv_dialog_title.setText("Finding phone number ...");
		
		LinearStringMessage findFriendMsg = new LinearStringMessage();
		findFriendMsg.setController("Relationship");
		findFriendMsg.setAction("RequestFriend");		
		findFriendMsg.putParam("pn", edtNumber.getText().toString());
		MessageQueue.getInstance().offerOutMessage(findFriendMsg,getActivity());
	}
}
