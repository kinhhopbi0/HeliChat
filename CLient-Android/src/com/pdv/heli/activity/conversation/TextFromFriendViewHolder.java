package com.pdv.heli.activity.conversation;

import java.util.Date;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.pdv.heli.R;
import com.pdv.heli.common.BeautyDate;
import com.pdv.heli.model.ChatRow;
import com.pdv.heli.model.Conversation;

public class TextFromFriendViewHolder extends BaseViewHolder {
	private ImageView mAvatar;
	private TextView mText;
	private TextView mSubTextView;

	public TextFromFriendViewHolder(View itemView, Conversation conversation,
			ConversationAdapter adapter) {
		super(itemView, conversation, adapter);
		mText = (TextView) itemView.findViewById(R.id.textViewContent);
		mAvatar = (ImageView) itemView.findViewById(R.id.im_friend_avatar);
		mSubTextView = (TextView) itemView.findViewById(R.id.sendTime);
	}

	@Override
	protected void bindData(ChatRow model) {
		mText.setText(model.getContentText());
		
		String statusText = model.getStatus();
		long curr = new Date().getTime();
		String cp = BeautyDate.compareDateToString(model.getTimeStamp(), curr);
		if (getPosition() !=0) {
			ChatRow netModel = adapter.getListModel().get(getPosition() - 1);
			String nextTime = BeautyDate.compareDateToString(
					netModel.getTimeStamp(), curr);
			if (nextTime.equals(cp)) {
				mSubTextView.setVisibility(View.GONE);
			} else {
				mSubTextView.setVisibility(View.VISIBLE);
			}
		} else {
			mSubTextView.setVisibility(View.VISIBLE);
		}
		if (statusText != null && !statusText.isEmpty() && getOldPosition() == 0) {
			cp = statusText + " " + cp;
		}
		mSubTextView.setText(cp);

	}

}
