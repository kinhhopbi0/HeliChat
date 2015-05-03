package com.pdv.heli.activity.conversation;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.pdv.heli.R;
import com.pdv.heli.activity.contact.FriendsFragment;
import com.pdv.heli.activity.conversation.BaseViewHolder.OnItemClickListener;
import com.pdv.heli.activity.conversation.ConversationReceiver.OnBroadcastReceiveListener;
import com.pdv.heli.activity.startup.StartFirstActivity;
import com.pdv.heli.component.HeliSnackbars;
import com.pdv.heli.component.IncomingChatNotification;
import com.pdv.heli.controller.ConversationController;
import com.pdv.heli.controller.TalkController;
import com.pdv.heli.manager.SharedPreferencesManager;
import com.pdv.heli.model.ChatRow;
import com.pdv.heli.model.Contact;
import com.pdv.heli.model.Conversation;

public class ConversationFragment extends android.support.v4.app.Fragment
		implements OnClickListener, TextWatcher, OnItemClickListener,
		OnBroadcastReceiveListener {
	protected RecyclerView mChatRecyclerView;
	protected ConversationAdapter mAdapter;
	protected EditText edtChatText;
	protected ImageButton btnSendText;
	protected InputMethodManager im;
	protected LinearLayoutManager layoutManager;
	protected ConversationReceiver receiver;
	private String tempPhone;
	protected Conversation conversation;
	protected String yourPhone;
	protected List<Contact> otherMembers;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		im = (InputMethodManager) getActivity().getSystemService(
				Service.INPUT_METHOD_SERVICE);
		receiver = new ConversationReceiver();
		receiver.setCallBack(this);
		yourPhone = SharedPreferencesManager.getLocalPhone();
		if (yourPhone == null) {
			startActivity(new Intent(this.getActivity(),
					StartFirstActivity.class));
		}
		Bundle bundle = getArguments();
		if (bundle != null) {
			if (bundle.containsKey("phone")) {
				String phone = bundle.getString("phone");
				tempPhone = phone;
								
				if (ConversationController.sendRequestConversation(phone,
						this.getActivity())) {

				} else {
					Toast.makeText(getActivity(), "Can't create conversation",
							Toast.LENGTH_LONG).show();					
				}

			}
			if (bundle.containsKey("conversationId")) {
				this.conversation = Conversation.findById(bundle.getString(
						"conversationId", ""));
				if (conversation == null) {

					FragmentTransaction fragmentTransaction = getActivity()
							.getSupportFragmentManager().beginTransaction();
					fragmentTransaction.replace(R.id.fragment_container,
							new FriendsFragment());
					fragmentTransaction.addToBackStack(null);
					fragmentTransaction.commit();
				} else {
					loadConversationMember();
				}
			}
		}

	}

	private void loadConversationMember() {
		JSONArray members = conversation.getMember();
		otherMembers = new ArrayList<Contact>();
		String localPhone = SharedPreferencesManager.getLocalPhone();
		for (int i = 0; i < members.length(); i++) {
			try {
				String phone = (String) members.get(i);
				Contact ct = Contact.findOneByPhone(phone);
				boolean isOtherPhone = !ct.getPhoneNumber().equals(localPhone);
				if (ct != null && isOtherPhone) {
					otherMembers.add(ct);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (conversation != null) {
			ConversationFragmentManager.getInstance().getConversations()
					.put(conversation.getId(), this);
			ChatRow.clearUnreadForConversation(conversation.getId());
			IncomingChatNotification.getInstance().updateNotifi(false);			
			ConversationController.sendUpdateSeen(conversation.getId());
		}		
		receiver.register(getActivity());
	}

	@Override
	public void onPause() {
		receiver.unRegiser(getActivity());
		if (conversation != null) {
			ConversationFragmentManager.getInstance().getConversations()
					.remove(conversation.getId());
		} else {

		}
		super.onPause();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_conversation, container,
				false);
		mChatRecyclerView = (RecyclerView) view.findViewById(R.id.listChat);
		edtChatText = (EditText) view.findViewById(R.id.et_plainText);
		btnSendText = (ImageButton) view.findViewById(R.id.bt_sendText);
		btnSendText.setOnClickListener(this);
		edtChatText.addTextChangedListener(this);
		edtChatText.setEnabled(false);
		btnSendText.setVisibility(View.GONE);
		view.setFocusableInTouchMode(true);

		
		List<ChatRow> defaultList = new ArrayList<ChatRow>();

		if (conversation != null) {
			defaultList.addAll(ChatRow.findChatByConversation(
					conversation.getId(), 0));
			edtChatText.setEnabled(true);
			edtChatText.setHint("Ready to send...");
		}else{
			
		}
		mAdapter = new ConversationAdapter(defaultList, this);
		layoutManager = new LinearLayoutManager(getActivity());
		layoutManager.setReverseLayout(true);
		mChatRecyclerView.setLayoutManager(layoutManager);
		mChatRecyclerView.setAdapter(mAdapter);
		mAdapter.setOnItemClickListener(this);
		return view;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		if (otherMembers != null) {
			if (otherMembers.size() > 0) {
				String title = "";
				for (Contact contact : otherMembers) {
					String availabelName = (contact.getContactName() != null) ? contact
							.getContactName() : contact.getPhoneNumber();
					title += availabelName + "; ";
				}
				title = title.substring(0, title.length() - 2);
				getActivity().setTitle(title);
			} else {
				getActivity().finish();
				return;
			}
		} else {
			if (tempPhone != null) {
				Contact cSingle = Contact.findOneByPhone(tempPhone);
				if (cSingle == null) {
					this.getActivity().setTitle(tempPhone);
				} else {
					this.getActivity().setTitle(cSingle.getContactName());
				}
			}
		}
		menu.clear();
		inflater.inflate(R.menu.conversation, menu);
	}

	@Override
	public void onClick(View v) {
		sendChat();
	}

	private void sendChat() {
		if (conversation == null) {
			Toast.makeText(getActivity(), "you can't send message now",
					Toast.LENGTH_SHORT).show();
			return;
		}
		String text = edtChatText.getText().toString().trim();
		edtChatText.getText().clear();
		ChatRow chatRowModel = ChatRow.makeChatToSend();
		chatRowModel.setFrom(yourPhone);
		chatRowModel.setConversationId(conversation.getId());
		JSONObject contentObj = new JSONObject();
		try {
			contentObj.put("text", text);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}

		chatRowModel.setContent(contentObj);

		if (TalkController.sendChat(chatRowModel, this.getActivity())) {
			chatRowModel.setStatus("Sending...");
			ChatRow.sendingList.put(chatRowModel.getSequence(), chatRowModel);
			insertBottomChatRow(chatRowModel);
			Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate);
			btnSendText.startAnimation(animation);
		};
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

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
		super.onConfigurationChanged(newConfig);

	}

	@Override
	public void onRecyclerViewAdapterItemClick(int pos, View v) {
		im.hideSoftInputFromWindow(edtChatText.getWindowToken(), 0);
	}

	public void insertBottomChatRow(ChatRow chatRowModel) {
		mAdapter.add(0, chatRowModel);
		if (mAdapter.getItemCount() > 1) {
			mAdapter.notifyItemRangeChanged(1,
					Math.min(5, mAdapter.getItemCount() - 1));
		}

		if (layoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
			layoutManager.scrollToPosition(0);
		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.clear:
			if (conversation != null) {
				int total = mAdapter.getItemCount();
				mAdapter.getListModel().clear();
				mAdapter.notifyItemRangeRemoved(0, total);
				ChatRow.deleteAllLocalbyConversationId(this.conversation
						.getId());
			}
			return true;
		}

		return false;
	}

	public void notifiUpdateDatabase() {
		if(this.conversation!=null){
			mAdapter.getListModel().clear();
			mAdapter.setNewList(ChatRow.findChatByConversation(
					conversation.getId(), 0));
			mAdapter.notifyDataSetChanged();
			if (layoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
				layoutManager.scrollToPosition(0);
			}
		}
		
	}

	public String getYourPhone() {
		return yourPhone;
	}
	public void updateSeenForConversation(){
		ChatRow.clearUnreadForConversation(conversation.getId());
		IncomingChatNotification.getInstance().updateNotifi(false);			
		ConversationController.sendUpdateSeen(conversation.getId());
	}
	@Override
	public void onBroadcastCallBack(Context context, Intent intent) {
		String action = intent.getAction();
		if (action.equals(ConversationReceiver.ACTION_CREATED_CONVERSATION)) {
			Bundle bundle = intent.getExtras();
			if (bundle.getString("pn").equals(this.tempPhone)) {
				if (bundle.containsKey("error")) {
					edtChatText.setHint(bundle.getString("error"));
					Toast.makeText(getActivity(), bundle.getString("error"),
							Toast.LENGTH_LONG).show();
				} else {
					this.conversation = Conversation
							.findByMemberPhone(tempPhone);
					if (this.conversation != null) {
						edtChatText.setEnabled(true);
						edtChatText.setHint("Ready too send...");						
						ConversationFragmentManager.getInstance().getConversations()
						.put(conversation.getId(), this);
						notifiUpdateDatabase();
					}
				}
			}
		}

		if (action.equals(ConversationReceiver.ACTION_UPDATE_SYNC_CHAT)) {
			mAdapter.setNewList(ChatRow.findChatByConversation(
					conversation.getId(), 0));
			mAdapter.notifyDataSetChanged();
		}
		if (action.equals(ConversationReceiver.ACTION_RESULT_SEND)) {

			String jsonStr = intent.getExtras().getString("json");
			try {
				JSONObject jsonObject = new JSONObject(jsonStr);
				int sequence = jsonObject.getInt("sequence");

				for (int j = 0; j < mAdapter.getItemCount(); j++) {
					ChatRow chatRow = mAdapter.getListModel().get(j);
					if (chatRow.getSequence() == sequence) {
						if (jsonObject.optBoolean("success")) {
							chatRow.setStatus("Sent");
							chatRow.setId(jsonObject.getString("_id"));
							mAdapter.notifyItemChanged(j);
						} else {
							chatRow.setStatus("Send FAIL!!!");
							HeliSnackbars.showErrorSendChat(getActivity());
							mAdapter.getListModel().remove(j);
							mAdapter.notifyItemRemoved(j);
						}

						break;
					}
				}

			} catch (JSONException e) {
			}

		}
		if (action.equals(ConversationReceiver.ACTION_SEND_ERRO)) {
			try {
				String error = intent.getExtras().getString("error");
				if (error.equals("conversationNotfound")) {
					Toast.makeText(getActivity(),
							"Conversation not found in server",
							Toast.LENGTH_LONG).show();
				}
				if (error.equals("notFriend")) {

				}
				mAdapter.notifyItemChanged(0);

			} catch (Exception ex) {

			}
		}
	}
}
