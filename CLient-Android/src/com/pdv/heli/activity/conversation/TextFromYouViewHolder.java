package com.pdv.heli.activity.conversation;

import android.view.View;
import android.widget.TextView;

import com.pdv.heli.R;
import com.pdv.heli.model.ChatRowModel;

public class TextFromYouViewHolder extends BaseViewHolder  {
	private TextView tvContentText;
	 
	
	public TextFromYouViewHolder(View itemView) {
		super(itemView);
		tvContentText = (TextView) itemView.findViewById(R.id.textViewContent);		
	
	}

	@Override
	protected void bindData(ChatRowModel model) {
		tvContentText.setText(model.getContentText());
	}

	
	
}
