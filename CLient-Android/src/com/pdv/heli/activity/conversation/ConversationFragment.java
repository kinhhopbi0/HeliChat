package com.pdv.heli.activity.conversation;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import com.pdv.heli.R;
import com.pdv.heli.activity.conversation.BaseViewHolder.OnItemClickListener;
import com.pdv.heli.manager.SharedPreferencesManager;
import com.pdv.heli.message.detail.ChatMessage;
import com.pdv.heli.model.ChatRowModel;
import com.pdv.heli.model.Contact;

public class ConversationFragment extends android.support.v4.app.Fragment
		implements OnClickListener, TextWatcher, OnItemClickListener {
	private RecyclerView mChatRecyclerView;
	private ConversationAdapter mAdapter;
	private EditText edtChatText;
	private ImageButton btnSendText;
	InputMethodManager im;
	int friendId = 0;
	public static final String KEY_SINGLE_FRIEND_ID = "friend_id";
	private LinearLayoutManager layoutManager;
	private BroadcastReceiver receiver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		im = (InputMethodManager) getActivity().getSystemService(
				Service.INPUT_METHOD_SERVICE);
		receiver = new BroadcastReceiver() {			
			@Override
			public void onReceive(Context context, Intent intent) {
				Bundle extras = intent.getExtras();
				ChatRowModel model = new ChatRowModel(friendId, extras.getString("textContent"));		
				mAdapter.add(0, model);	
				mAdapter.notifyItemInserted(0);
				if (layoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
				    layoutManager.scrollToPosition(0);
				}				
			}
		};
	}
	public static final String CHAT_ACTION = ConversationFragment.class.getName()+".actionChat."; 
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		IntentFilter filter = new IntentFilter();
		filter.addAction(CHAT_ACTION+friendId);
		getActivity().registerReceiver(receiver, filter);
		super.onResume();
	}
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		getActivity().unregisterReceiver(receiver);
		super.onPause();
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_conversation, container,
				false);
		mChatRecyclerView = (RecyclerView) view.findViewById(R.id.listChat);
		mAdapter = new ConversationAdapter(ChatRowModel.getDemos(),
				getActivity());
		 layoutManager = new LinearLayoutManager(
				getActivity());
		layoutManager.setReverseLayout(true);
		mChatRecyclerView.setLayoutManager(layoutManager);
		mChatRecyclerView.setAdapter(mAdapter);
		mAdapter.setOnItemClickListener(this);
		edtChatText = (EditText) view.findViewById(R.id.et_plainText);
		btnSendText = (ImageButton) view.findViewById(R.id.bt_sendText);
		btnSendText.setOnClickListener(this);
		edtChatText.addTextChangedListener(this);
		btnSendText.setVisibility(View.GONE);
		if (getArguments().getInt(KEY_SINGLE_FRIEND_ID) > 0) {
			int friend_id = getArguments().getInt(KEY_SINGLE_FRIEND_ID);			
		}

		return view;
	}
	public void setUserId(int id){
		friendId = id;
	}
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		getActivity().setTitle("Conversation");
		
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void onClick(View v) {
		String text = edtChatText.getText().toString();
		edtChatText.getText().clear();
		ChatMessage chatMessage = new ChatMessage();
		chatMessage.setParam("receiverId", friendId+"");
		chatMessage.setParam("textContent", text);
		com.pdv.heli.manager.MessageQueue.getInstance().offerOutMessage(chatMessage, null);
		
		ChatRowModel model = new ChatRowModel(SharedPreferencesManager.getLoginUserId(), text);		
		mAdapter.add(0, model);	
		mAdapter.notifyItemInserted(0);
		if (layoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
		    layoutManager.scrollToPosition(0);
		}		
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterTextChanged(Editable s) {
		if (s.length() > 0) {
			btnSendText.setVisibility(View.VISIBLE);
		} else {
			btnSendText.setVisibility(View.GONE);
		}

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);		

	}

	@Override
	public void onItemClick(int pos, View v) {	
		im.hideSoftInputFromWindow(edtChatText.getWindowToken(), 0);
	}
	

}
