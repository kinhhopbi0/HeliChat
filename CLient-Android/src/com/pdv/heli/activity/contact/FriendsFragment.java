package com.pdv.heli.activity.contact;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ItemDecoration;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.pdv.heli.R;
import com.pdv.heli.activity.contact.FriendListAdapter.OnItemClicked;
import com.pdv.heli.activity.conversation.ConversationFragment;
import com.pdv.heli.activity.conversation.ConversationReceiver;
import com.pdv.heli.activity.conversation.ConversationReceiver.OnBroadcastReceiveListener;
import com.pdv.heli.common.android.GroupNameDialog;
import com.pdv.heli.controller.ContactController;
import com.pdv.heli.manager.SharedPreferencesManager;
import com.pdv.heli.message.detail.SyncDeviceContactMessage;
import com.pdv.heli.model.Contact;
import com.pdv.heli.sqlite.ChatDbHelper;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

public class FriendsFragment extends Fragment implements OnItemClicked,
		OnBroadcastReceiveListener, OnQueryTextListener {
	protected RecyclerView mRecyclerView;
	protected FriendListAdapter mListFriendAdapter;
	protected AddPhoneDialog addFriendDialog;
	protected UpdateContactUIReceiver receiver;
	protected ConversationReceiver conversationReceiver;
	private LinearLayoutManager recyclerViewLayout;
	private List<String> selectedPhone;
	private List<View> selectedRowsView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setHasOptionsMenu(true);
		conversationReceiver = new ConversationReceiver();
		conversationReceiver.setCallBack(this);
		receiver = new UpdateContactUIReceiver(this);
		Handler handler = new Handler(getActivity().getMainLooper());
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				// do do here
			}
		}, 300);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		ContactController.callRequestOnlineContactState();
	}

	@Override
	public void onResume() {
		super.onResume();
		receiver.register();
		conversationReceiver.register(getActivity());
		while (contactsUpdateQueue.size() > 0) {
			Contact contact = contactsUpdateQueue.remove();
			mListFriendAdapter.addNewRow(contact);
		}
	}

	@Override
	public void onPause() {
		getActivity().unregisterReceiver(receiver);
		getActivity().unregisterReceiver(conversationReceiver);
		super.onPause();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_friends, container,
				false);
		initializeRecyclerView(view);
		return view;
	}

	private void initializeRecyclerView(View view) {
		mRecyclerView = (RecyclerView) view.findViewById(R.id.friend_list);
		mListFriendAdapter = new FriendListAdapter(this.getActivity(),
				Contact.findAll());
		recyclerViewLayout = new LinearLayoutManager(getActivity());
		mRecyclerView.setLayoutManager(recyclerViewLayout);
		mRecyclerView.setAdapter(mListFriendAdapter);
		mListFriendAdapter.setOnItemClickedListener(this);
		ItemDecoration deviler = new HorizontalDividerItemDecoration.Builder(
				getActivity()).visibilityProvider(mListFriendAdapter)
				.marginProvider(mListFriendAdapter).build();
		mRecyclerView.addItemDecoration(deviler);
	}

	public void refreshList() {
		mListFriendAdapter.mList = null;
		mListFriendAdapter.mList = Contact.findAll();
		mListFriendAdapter.notifyDataSetChanged();
	}

	@Override
	public void onStart() {
		super.onStart();
		getActivity().setTitle("Contacts");

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
		inflater.inflate(R.menu.menu_contact, menu);
		SearchManager searchManager = (SearchManager) getActivity()
				.getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.contact_search)
				.getActionView();
		searchView.setSearchableInfo(searchManager
				.getSearchableInfo(getActivity().getComponentName()));
		searchView.setOnQueryTextListener(this);
	}

	@Override
	public void onContactItemClick(View v, Contact c, int pos) {
		if (selectMany) {
			if (!v.isSelected()) {
				v.setSelected(true);
				selectedPhone.add(c.getPhoneNumber());
				selectedRowsView.add(v);
			} else {
				v.setSelected(false);
				selectedPhone.remove(c.getPhoneNumber());
				selectedPhone.remove(v);
			}
			updateSelectedTile();
			return;
		}

		FragmentTransaction transaction = getActivity()
				.getSupportFragmentManager().beginTransaction();
		ConversationFragment conversation = new ConversationFragment();
		Bundle bundle = new Bundle();
		if (c.getConversationId() != null) {
			bundle.putString("conversationId", c.getConversationId());
		}else{
			bundle.putString("phone", c.getPhoneNumber());			
		}

		conversation.setArguments(bundle);
		transaction.setCustomAnimations(R.anim.slide_in_right1,
				R.anim.slide_out_left);
		transaction.replace(R.id.fragment_container, conversation,
				"conversation");
		transaction.addToBackStack(null);
		transaction.commit();
	}

	private void updateSelectedTile() {
		mActionMode.setTitle(selectedPhone.size() + " contact selected");
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.itemAdd:
			addFriendDialog = new AddPhoneDialog();
			addFriendDialog.setUp(this);
			addFriendDialog.show(getFragmentManager(), "addContact");
			return true;

		case R.id.itemSyncContact:
			postSyncDeviceContact();
			break;
		case R.id.sort_by_name:
			SharedPreferencesManager
					.saveContactShortType(ChatDbHelper.SORT_BY_CONTACT_NAME);
			refreshList();
			return true;
		case R.id.sort_by_time:
			SharedPreferencesManager
					.saveContactShortType(ChatDbHelper.SORT_BY_CHAT_HIS);
			refreshList();
			return true;
		}
		return false;
	}

	private void postSyncDeviceContact() {
		LoadContactTask contactTask = new LoadContactTask();
		contactTask.execute("");

	}

	private class LoadContactTask extends
			AsyncTask<String, Integer, SyncDeviceContactMessage> {
		private ProgressDialog progressDialog = new ProgressDialog(
				getActivity());

		@Override
		protected SyncDeviceContactMessage doInBackground(String... params) {
			// ArrayList<Contact> contacts = new ArrayList<>();
			HashMap<String, String> contactsm = new HashMap<>();
			ArrayList<String> names = new ArrayList<>();
			ArrayList<String> phones = new ArrayList<>();
			ContentResolver cr = getActivity().getContentResolver();
			Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
					null, null, null);
			int numberOfContacts = cur.getCount();
			progressDialog.setMax(numberOfContacts);
			if (numberOfContacts > 0) {
				while (cur.moveToNext()) {
					String id = cur.getString(cur
							.getColumnIndex(ContactsContract.Contacts._ID));
					String name = cur
							.getString(cur
									.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
					if (Integer
							.parseInt(cur.getString(cur
									.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
						Cursor pCur = cr
								.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
										null,
										ContactsContract.CommonDataKinds.Phone.CONTACT_ID
												+ " = ?", new String[] { id },
										null);
						while (pCur.moveToNext()) {
							String phone = pCur
									.getString(pCur
											.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
							/*
							 * Contact contact = new Contact();
							 * contact.setContactName(name);
							 * contact.setPhoneNumber(phone);
							 * contacts.add(contact);
							 */
							if (contactsm.put(phone, name) == null) {
								phones.add(phone);
								names.add(name);
							}
						}
						pCur.close();
					}

					this.onProgressUpdate(cur.getPosition());
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(SyncDeviceContactMessage result) {
			super.onPostExecute(result);

			// String js = result.getJson();
			// MessageQueue.getInstance().offerOutMessage(result,getActivity());
			progressDialog.dismiss();
		}

		@Override
		protected void onPreExecute() {
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressDialog.setTitle("Reading contact from your device's");
			progressDialog.show();
			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			progressDialog.setProgress(values[0]);
		}
	}

	ArrayBlockingQueue<Contact> contactsUpdateQueue = new ArrayBlockingQueue<>(
			200);

	public void offerContactDelay(Contact c) {
		contactsUpdateQueue.offer(c);

	}

	protected void insertAndScroll(Contact contact) {
		if (!mListFriendAdapter.contains(contact)) {
			mListFriendAdapter.addNewRow(contact);
			recyclerViewLayout.scrollToPosition(mListFriendAdapter
					.getItemCount());
		}

	}

	protected boolean selectMany;

	@Override
	public void onContactItemOngClick(final View v, final Contact c,
			final int pos) {
		if (selectMany) {

			return;
		}
		ContactPopupMenu contactPopupMenu = new ContactPopupMenu(this,
				v.findViewById(R.id.rightIcon), v);
		contactPopupMenu
				.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

					@Override
					public boolean onMenuItemClick(MenuItem arg0) {
						JSONObject jsonObject = new JSONObject();
						switch (arg0.getItemId()) {
						case R.id.delete:
							Contact.deleteByPhone(c.getPhoneNumber());
							removeContactInUi(pos);
							ContactController.callDeleteContact(c
									.getPhoneNumber());
							return true;
						case R.id.block:
							removeContactInUi(pos);

							try {
								jsonObject.put("pn", c.getPhoneNumber());
								jsonObject.put("block", true);
								ContactController.callUpdateContact(jsonObject);
							} catch (JSONException e) {
								e.printStackTrace();
							}

							return true;
						case R.id.openChat:
							onContactItemClick(v, c, pos);
							return true;

						case R.id.select:
							if (mActionMode != null) {
								return false;
							}
							selectMany = true;
							mActionMode = getActivity().startActionMode(
									mActionModeCallback);
							selectedPhone = new ArrayList<String>();
							selectedRowsView = new ArrayList<View>();
							selectedPhone.add(c.getPhoneNumber());
							selectedRowsView.add(v);
							updateSelectedTile();
							return true;
						}

						return false;
					}
				});
		contactPopupMenu.show();
		v.setSelected(true);

	}

	protected void removeContactInUi(int pos) {
		mListFriendAdapter.mList.remove(pos);
		mListFriendAdapter.notifyItemRemoved(pos);
	}

	private ActionMode mActionMode;
	private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

		// Called when the action mode is created; startActionMode() was called
		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			// Inflate a menu resource providing context menu items
			MenuInflater inflater = mode.getMenuInflater();
			inflater.inflate(R.menu.multilpe_select_contact, menu);

			return true;
		}

		// Called each time the action mode is shown. Always called after
		// onCreateActionMode, but
		// may be called multiple times if the mode is invalidated.
		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

			return true; // Return false if nothing is done
		}

		// Called when the user selects a contextual menu item
		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			switch (item.getItemId()) {
			case R.id.createConversation:
				if (selectedPhone != null && selectedPhone.size() > 1) {
					GroupNameDialog setNameDialog = new GroupNameDialog();
					setNameDialog.setPhoneList(selectedPhone);
					setNameDialog.show(getFragmentManager(),
							"group_name_dialog");

				} else {
					Toast.makeText(getActivity(), "People too few",
							Toast.LENGTH_SHORT).show();
				}

				break;

			case R.id.delete_many:

				break;

			case R.id.block_many:

				break;

			}
			return true;
		}

		// Called when the user exits the action mode
		@Override
		public void onDestroyActionMode(ActionMode mode) {
			mActionMode = null;
			selectMany = false;
			selectedPhone.clear();
			for (int i = 0; i < selectedRowsView.size(); i++) {
				selectedRowsView.get(i).setSelected(false);
			}
			selectedRowsView.clear();
			selectedRowsView = null;
			selectedPhone = null;
			mListFriendAdapter.notifyItemRangeChanged(0,
					mListFriendAdapter.getItemCount());
		}
	};

	@Override
	public void onBroadcastCallBack(Context context, Intent intent) {
		String action = intent.getAction();
		if (action.equals(ConversationReceiver.ACTION_RECEIVE_LIVE_CHAT)
				|| action.equals(ConversationReceiver.ACTION_UPDATE_SYNC_CHAT)
				|| action
						.equals(ConversationReceiver.ACTION_CREATED_CONVERSATION)
				|| action
						.equals(ConversationReceiver.ACTION_CREATED_CONVERSATION)) {
			refreshList();
		}
	}

	@Override
	public boolean onQueryTextChange(String arg0) {
		List<Contact> contacts = Contact.search(arg0);
		mListFriendAdapter.mList = null;
		mListFriendAdapter.mList = contacts;
		mListFriendAdapter.notifyDataSetChanged();
		return true;
	}

	@Override
	public boolean onQueryTextSubmit(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}
}
