package com.pdv.heli.activity.conversation;

import java.util.Collections;
import java.util.List;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pdv.heli.R;
import com.pdv.heli.activity.conversation.BaseViewHolder.OnItemClickListener;
import com.pdv.heli.model.ChatRow;
import com.yqritc.recyclerviewflexibledivider.FlexibleDividerDecoration.VisibilityProvider;

public class ConversationAdapter extends
		RecyclerView.Adapter<BaseViewHolder> implements  OnItemClickListener, VisibilityProvider {
	public static final int ROW_TYPE_TEXT_FROM_YOU = 1;
	public static final int ROW_TYPE_TEXT_FROM_FRIEND = 2;
	public static final int ROW_TYPE_FILE_FROM_YOU = 3;
			
	private List<ChatRow> mList = Collections.emptyList();
	private ConversationFragment container;
	
	
	public ConversationAdapter(List<ChatRow> mList,  ConversationFragment container) {
		super();
		this.mList = mList;		
		this.container = container;
	}
	
	@Override
	public int getItemCount() {
		return mList.size();
	}

	@Override
	public void onBindViewHolder(BaseViewHolder viewHolder, int pos) {
		ChatRow chatRowModel = mList.get(pos);
		viewHolder.bindData(chatRowModel);
		Log.v("test", "bind "+pos);
	}
	@Override
	public int getItemViewType(int position) {
		ChatRow model = mList.get(position);		
		if(model.getSenderPhone().equals(container.getYourPhone())){
			return ROW_TYPE_TEXT_FROM_YOU;
		}else{
			return ROW_TYPE_TEXT_FROM_FRIEND;
		}
	}

	@Override
	public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {		
		LayoutInflater inflater = LayoutInflater.from(container.getActivity());
		BaseViewHolder viewHolder = null;
		View row = null;
		switch (viewType) {
		case ROW_TYPE_TEXT_FROM_YOU:
			row = inflater.inflate(R.layout.view_chat_item_you, parent,false);
			viewHolder = new TextFromYouViewHolder(row,container.conversation, this);
			break;

		case ROW_TYPE_TEXT_FROM_FRIEND:
			row = inflater.inflate(R.layout.view_chat_item_friend, parent,false);
			viewHolder = new TextFromFriendViewHolder(row,container.conversation, this);
			break;			
		default:
			
			break;
		}
		viewHolder.setOnItemClickListener(this);
		return viewHolder; 
	}
	
	@Override
	public void onRecyclerViewAdapterItemClick(int pos, View v) {
		if(listener!=null){
			listener.onRecyclerViewAdapterItemClick(pos, v);
		}
	}
	private BaseViewHolder.OnItemClickListener listener;
	public void setOnItemClickListener(BaseViewHolder.OnItemClickListener clickListener){
		this.listener = clickListener;
	}

	public void add(int i, ChatRow model) {
		mList.add(i,model);
		notifyItemInserted(i);
	}
	public void setNewList(List<ChatRow> lst){
		this.mList = null;
		this.mList = lst;		
	}
	public List<ChatRow> getListModel(){
		return mList;
	}

	@Override
	public boolean shouldHideDivider(int position, RecyclerView parent) {
		
		// TODO Auto-generated method stub
		return false;
	}
}
