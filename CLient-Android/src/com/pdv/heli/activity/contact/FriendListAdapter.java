package com.pdv.heli.activity.contact;

import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pdv.heli.R;
import com.pdv.heli.activity.contact.FriendListAdapter.FriendViewHolder;
import com.pdv.heli.model.Contact;

public class FriendListAdapter extends RecyclerView.Adapter<FriendViewHolder> {
	private List<Contact> mList;
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
		if (contact.getDisplayName() != null) {
			viewHolder.tvName.setText(contact.getDisplayName());
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
			OnClickListener {
		private ImageView imvAvatar;
		private TextView tvName;
		private TextView tvLineTwo;
		private ImageView rightIcon;
		private View bottomDeviler;

		public FriendViewHolder(View itemView) {
			super(itemView);
			imvAvatar = (ImageView) itemView.findViewById(R.id.avatar);
			tvName = (TextView) itemView.findViewById(R.id.tvName);
			rightIcon = (ImageView) itemView.findViewById(R.id.rightIcon);
			bottomDeviler = itemView.findViewById(R.id.diviler);
			itemView.setOnClickListener(this);

		}

		@Override
		public void onClick(View v) {
			if (listener != null) {
				int pos = getPosition();
				Contact ct = mList.get(pos);
				listener.onContactItemClick(v, ct, pos);
			}
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
	}

	public void setOnItemClickedListener(OnItemClicked listener) {
		this.listener = listener;
	}

}
