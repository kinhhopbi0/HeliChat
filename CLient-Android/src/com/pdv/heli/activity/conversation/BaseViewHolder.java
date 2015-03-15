package com.pdv.heli.activity.conversation;

import com.pdv.heli.model.ChatRowModel;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;

public abstract class BaseViewHolder extends RecyclerView.ViewHolder implements OnClickListener{
	private OnItemClickListener listener;
	public BaseViewHolder(View itemView) {
		super(itemView);
		itemView.setOnClickListener(this);
	}
	protected abstract void bindData(ChatRowModel model) ;
	
	@Override
	public void onClick(View v) {		
		if(listener !=null){
			listener.onItemClick(getPosition(),v);
		}
	}
	public interface OnItemClickListener{
		void onItemClick(int pos, View v);
	}
	public void setOnItemClickListener(OnItemClickListener clickListener){
		this.listener = clickListener;
	}
}
