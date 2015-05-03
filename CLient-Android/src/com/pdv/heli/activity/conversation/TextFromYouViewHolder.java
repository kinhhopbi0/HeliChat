package com.pdv.heli.activity.conversation;

import java.util.Date;

import android.view.View;
import android.widget.TextView;

import com.pdv.heli.R;
import com.pdv.heli.common.BeautyDate;
import com.pdv.heli.model.ChatRow;
import com.pdv.heli.model.Conversation;

public class TextFromYouViewHolder extends BaseViewHolder {
	private TextView tvContentText;
	private TextView mSubTextView;
	private Conversation conversation = null;

	public TextFromYouViewHolder(View itemView, Conversation conversation,
			ConversationAdapter adapter) {
		super(itemView, conversation, adapter);
		tvContentText = (TextView) itemView.findViewById(R.id.textViewContent);
		mSubTextView = (TextView) itemView.findViewById(R.id.sendTime);
		this.conversation = conversation;
	}

	@Override
	protected void bindData(ChatRow model) {
		tvContentText.setText(model.getContentText());
		String statusText = model.getStatus();

		long curr = new Date().getTime();
		String cp = BeautyDate.compareDateToString(model.getTimeStamp(), curr);
		if (getPosition() !=0) {
			ChatRow netModel = adapter.getListModel().get(getPosition() -1);
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
		if (statusText!=null && !statusText.isEmpty()) {
			cp = statusText + " "+cp;
			mSubTextView.setVisibility(View.VISIBLE);
		}
		mSubTextView.setText(cp);
	}

}
