package com.pdv.heli.activity.conversation;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.pdv.heli.R;
import com.pdv.heli.model.ChatRowModel;

public class TextFromFriendViewHolder extends BaseViewHolder {
	private ImageView mAvatar;
	private TextView mText;
	
	public TextFromFriendViewHolder(View itemView) {
		super(itemView);
		mText = (TextView) itemView.findViewById(R.id.textViewContent);
		mAvatar = (ImageView) itemView.findViewById(R.id.im_friend_avatar);
	}

	@Override
	protected void bindData(ChatRowModel model) {
		mText.setText(model.getContentText());		
		if(model.getSender().getAvatarUri()!=null){
			try{
				mAvatar.setImageURI(model.getSender().getAvatarUri());
			}catch(Exception ex){				
				// default avatar
				mAvatar.setImageResource(R.drawable.ic_action_account_circle);				
			}
		}
		
		
	}

}
