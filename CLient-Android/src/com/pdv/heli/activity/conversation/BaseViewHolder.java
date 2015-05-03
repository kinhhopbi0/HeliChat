package com.pdv.heli.activity.conversation;

import com.pdv.heli.model.ChatRow;
import com.pdv.heli.model.Conversation;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;

public abstract class BaseViewHolder extends RecyclerView.ViewHolder implements OnClickListener{
	private OnItemClickListener listener;
	protected ConversationAdapter adapter;
	public BaseViewHolder(View itemView,Conversation conversation, ConversationAdapter adapter) {
		super(itemView);
		itemView.setOnClickListener(this);
		this.adapter = adapter;
	}
	protected abstract void bindData(ChatRow model) ;
	
	@Override
	public void onClick(View v) {		
		if(listener !=null){
			listener.onRecyclerViewAdapterItemClick(getPosition(),v);
		}
	}
	public interface OnItemClickListener{
		void onRecyclerViewAdapterItemClick(int pos, View v);
	}
	public void setOnItemClickListener(OnItemClickListener clickListener){
		this.listener = clickListener;
	}
}
