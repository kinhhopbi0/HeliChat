package com.pdv.heli.activity.contact;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.pdv.heli.R;
import com.pdv.heli.activity.contact.FriendListAdapter.OnItemClicked;
import com.pdv.heli.activity.conversation.ConversationFragment;
import com.pdv.heli.manager.MessageQueue;
import com.pdv.heli.message.detail.SyncDeviceContactMessage;
import com.pdv.heli.model.Contact;

public class FriendsFragment extends Fragment implements OnItemClicked {
	private RecyclerView mRecyclerView;
	private FriendListAdapter mListFriendAdapter;
	private AddFriendDialog addFriendDialog;
	public static final String FRIEND_ACTION = FriendsFragment.class.getName()
			+ ".friendAction";

	public static final String ADD_PHONE_NUMBER_ACTION = FriendsFragment.class
			.getName() + ".ADD_PHONE_NUMBER_ACTION";
	BroadcastReceiver receiver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				Bundle bundle = intent.getExtras();
				
				if (action.equals(ADD_PHONE_NUMBER_ACTION)) {
					if(addFriendDialog!=null && addFriendDialog.isCancelable()){
						addFriendDialog.dismiss();
					}
					if (bundle.getBoolean("found", false)) {
						if (bundle.getBoolean("requested", false)) {
							SnackbarManager.show(Snackbar.with(getActivity())
									.text("Đã gửi lời mời kết bạn đến vơi số điện thoại"));
						} else {
							SnackbarManager.show(Snackbar.with(getActivity())
									.text("Gửi lời mời đến "));
						}

					} else {

					}

				}
				String friendId = intent.getExtras().getString("friendId");
				Contact contact = new Contact("012345", "dsada");
				contact.setUser_id(Integer.parseInt(friendId));
				mListFriendAdapter.addNewRow(contact);
			}
		};

	}

	@Override
	public void onResume() {
		super.onResume();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(FRIEND_ACTION);
		intentFilter.addAction(ADD_PHONE_NUMBER_ACTION);
		getActivity().registerReceiver(receiver, intentFilter);
	}

	@Override
	public void onPause() {
		getActivity().unregisterReceiver(receiver);
		super.onPause();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_friends, container,
				false);
		mRecyclerView = (RecyclerView) view.findViewById(R.id.friend_list);
		mListFriendAdapter = new FriendListAdapter(this.getActivity(),
				Contact.getDemos());
		mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
		mRecyclerView.setAdapter(mListFriendAdapter);
		mListFriendAdapter.setOnItemClickedListener(this);

		SyncDeviceContactMessage deviceContactMessage = new SyncDeviceContactMessage();
		deviceContactMessage
				.setType(SyncDeviceContactMessage.Type.CLIENT_GET_CONTACT);
		return view;
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
	}

	@Override
	public void onContactItemClick(View v, Contact c, int pos) {
		FragmentTransaction transaction = getActivity()
				.getSupportFragmentManager().beginTransaction();
		ConversationFragment conversation = new ConversationFragment();
		conversation.setUserId(c.getUser_id());
		Bundle bundle = new Bundle();
		bundle.putInt(ConversationFragment.KEY_SINGLE_FRIEND_ID, c.getUser_id());
		conversation.setArguments(bundle);
		transaction.replace(R.id.fragment_container, conversation);
		transaction.addToBackStack(null);
		transaction.commit();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.itemAdd:
			addFriendDialog = new AddFriendDialog();
			addFriendDialog.show(getFragmentManager(), "addFriend");
			break;
		case R.id.itemSyncContact:
			postSyncContact();
			break;
		default:
			break;
		}
		return false;
	}

	private void postSyncContact() {
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

			SyncDeviceContactMessage contactMessage = new SyncDeviceContactMessage();
			contactMessage
					.setType(SyncDeviceContactMessage.Type.CLIENT_POST_CONTACT);
			contactMessage.setNames(names);
			contactMessage.setPhones(phones);
			return contactMessage;
		}

		@Override
		protected void onPostExecute(SyncDeviceContactMessage result) {
			super.onPostExecute(result);
			result.makeContactsToJSONString();
			String js = result.getJson();
			MessageQueue.getInstance().offerOutMessage(result,getActivity());
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

}
