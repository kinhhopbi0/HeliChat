package com.pdv.heli.activity.contact;

import java.util.List;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.pdv.heli.R;
import com.pdv.heli.activity.contact.FriendListAdapter.FriendViewHolder;
import com.pdv.heli.app.HeliApplication;
import com.pdv.heli.model.Contact;
import com.pdv.heli.model.Conversation;
import com.yqritc.recyclerviewflexibledivider.FlexibleDividerDecoration.VisibilityProvider;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration.MarginProvider;

public class FriendListAdapter extends RecyclerView.Adapter<FriendViewHolder>
		implements VisibilityProvider, MarginProvider {
	protected List<Contact> mList;
	private OnItemClicked listener;
	private Context context;

	@Override
	public int getItemCount() {
		return mList.size();
	}

	public FriendListAdapter(Context context) {
		this.context = context;
	}

	public FriendListAdapter(Context context, List<Contact> mList) {
		this.context = context;
		this.mList = mList;
	}

	/**
	 * bind row to ui
	 */
	@Override
	public void onBindViewHolder(FriendViewHolder viewHolder, int pos) {
		Contact contact = mList.get(pos);		
		if (contact.getAvatarUri() != null) {
			viewHolder.imvAvatar.setImageURI(contact.getAvatarUri());
		} else {

		}
		if (contact.getContactName() != null) {
			viewHolder.tvName.setText(contact.getContactName());
			viewHolder.tvPhone.setText(contact.getPhoneNumber());
		} else {
			viewHolder.tvName.setText(contact.getPhoneNumber());
			viewHolder.tvPhone.setVisibility(View.GONE);
			if (contact.getDisplayName() != null) {
				viewHolder.tvName.setText(contact.getDisplayName());
			}
		}

		viewHolder.tv_count_mess.setVisibility(View.GONE);
		viewHolder.tv_count_mess.setText("");
		viewHolder.lastChat.setText("");
		viewHolder.lastChat.setVisibility(View.GONE);
		
		String conversationId = contact.getConversationId();
		if (conversationId != null) {
			Conversation conversation = Conversation.findById(conversationId);
			if (conversation != null ) {
				if(conversation.getUnread()>0){
					viewHolder.tv_count_mess.setVisibility(View.VISIBLE);
					
					viewHolder.tv_count_mess.setText(conversation.getUnread() + "");
					Animation animation = AnimationUtils.loadAnimation(HeliApplication.getInstance(), R.anim.rotate);
					viewHolder.tv_count_mess.startAnimation(animation);
				}
				if(conversation.getLastChatContent()!=null){
					viewHolder.lastChat.setText(conversation.getLastChatContent());
					viewHolder.lastChat.setVisibility(View.VISIBLE);
				}
								
			}

		}
		Integer state = HeliApplication.getInstance().getOnlineState()
				.get(contact.getPhoneNumber());
		if (state == null || state == 2) {
			viewHolder.view_online_state
					.setBackgroundResource(R.drawable.online_state_off_bgr);
		} else {
			if (state == 1) {
				viewHolder.view_online_state
						.setBackgroundResource(R.drawable.online_state_bgr);
			}
		}

	}

	/**
	 * create a view row
	 */
	@Override
	public FriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

		View itemView = LayoutInflater.from(context).inflate(
				R.layout.c_friend_row, parent, false);
		FriendViewHolder friendViewHolder = new FriendViewHolder(itemView);
		return friendViewHolder;
	}

	public class FriendViewHolder extends RecyclerView.ViewHolder implements
			OnClickListener, OnLongClickListener {
		private ImageView imvAvatar;
		private TextView tvName;
		private TextView tvPhone;
		private ImageView rightIcon;
		private TextView tv_count_mess;
		private View view_online_state;
		private TextView lastChat ;

		public FriendViewHolder(View itemView) {
			super(itemView);
			imvAvatar = (ImageView) itemView.findViewById(R.id.avatar);
			tvName = (TextView) itemView.findViewById(R.id.tvName);
			
			rightIcon = (ImageView) itemView.findViewById(R.id.rightIcon);
			tvPhone = (TextView) itemView.findViewById(R.id.tvPhone);
			tv_count_mess = (TextView) itemView
					.findViewById(R.id.tv_count_mess);
			view_online_state = itemView.findViewById(R.id.view_online_state);
			lastChat = (TextView) itemView.findViewById(R.id.tv_last_chat);
			
			itemView.setOnClickListener(this);
			itemView.setOnLongClickListener(this);

		}

		@Override
		public void onClick(View v) {
			if (listener != null) {
				int pos = getPosition();
				Contact ct = mList.get(pos);
				listener.onContactItemClick(v, ct, pos);
			}
		}

		@Override
		public boolean onLongClick(View v) {
			if (listener != null) {
				int pos = getPosition();
				Contact ct = mList.get(pos);
				listener.onContactItemOngClick(v, ct, pos);
				return true;
			}
			return false;
		}

	}

	public void addNewRow(Contact contact) {
		mList.add(contact);

		this.notifyItemInserted(getItemCount());
	}

	public void addNewRow(Contact contact, int pos) {
		mList.add(pos, contact);
		this.notifyItemInserted(pos);
	}

	public void removeRow(int pos) {
		mList.remove(pos);
		this.notifyItemRemoved(pos);
	}

	public interface OnItemClicked {
		void onContactItemClick(View v, final Contact c, int pos);

		void onContactItemOngClick(View v, final Contact c, int pos);
	}

	public void setOnItemClickedListener(OnItemClicked listener) {
		this.listener = listener;
	}

	public void updateRowByPhone(String string) {
		for (int i = 0; i < mList.size(); i++) {
			if (mList.get(i).getPhoneNumber().equals(string)) {
				Contact contact = Contact.findOneByPhone(string);
				if (contact == null) {

				}
				mList.remove(i);
				mList.add(i, contact);
				this.notifyItemChanged(i);
				return;
			}
		}
	}

	@Override
	public boolean shouldHideDivider(int position, RecyclerView parent) {

		return false;
	}

	@Override
	public int dividerLeftMargin(int position, RecyclerView parent) {
		return context.getResources().getDimensionPixelSize(
				R.dimen.activity_horizontal_margin);
	}

	@Override
	public int dividerRightMargin(int position, RecyclerView parent) {
		return context.getResources().getDimensionPixelSize(
				R.dimen.activity_horizontal_margin);
	}

	public boolean contains(Contact contact) {
		return mList.contains(contact);
	}

}
