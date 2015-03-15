package com.pdv.heli.activity.conversation;

import java.util.Collections;
import java.util.List;

import android.app.Service;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.pdv.heli.R;
import com.pdv.heli.activity.conversation.BaseViewHolder.OnItemClickListener;
import com.pdv.heli.app.HeliApplication;
import com.pdv.heli.manager.SharedPreferencesManager;
import com.pdv.heli.model.ChatRowModel;

public class ConversationAdapter extends
		RecyclerView.Adapter<BaseViewHolder> implements  OnItemClickListener {
	public static final int ROW_TYPE_TEXT_FROM_YOU = 1;
	public static final int ROW_TYPE_TEXT_FROM_FRIEND = 2;
	public static final int ROW_TYPE_FILE_FROM_YOU = 3;
	public static int userId = SharedPreferencesManager.getLoginUserId();

	private List<ChatRowModel> mList = Collections.emptyList();
	private Context mContext;
	
	
	public ConversationAdapter(List<ChatRowModel> mList, Context mContext) {
		super();
		this.mList = mList;
		this.mContext = mContext;
	}
	
	@Override
	public int getItemCount() {
		return mList.size();
	}

	@Override
	public void onBindViewHolder(BaseViewHolder viewHolder, int pos) {
		ChatRowModel chatRowModel = mList.get(pos);
		viewHolder.bindData(chatRowModel);
	}
	@Override
	public int getItemViewType(int position) {
		ChatRowModel model = mList.get(position);		
		if(model.getSender().getUser_id() ==userId ){
			return ROW_TYPE_TEXT_FROM_YOU;
		}else{
			return ROW_TYPE_TEXT_FROM_FRIEND;
		}
	}

	@Override
	public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {		
		LayoutInflater inflater = LayoutInflater.from(mContext);
		BaseViewHolder viewHolder = null;
		View row = null;
		switch (viewType) {
		case ROW_TYPE_TEXT_FROM_YOU:
			row = inflater.inflate(R.layout.view_chat_item_you, parent,false);
			viewHolder = new TextFromYouViewHolder(row);
			break;

		case ROW_TYPE_TEXT_FROM_FRIEND:
			row = inflater.inflate(R.layout.view_chat_item_friend, parent,false);
			viewHolder = new TextFromFriendViewHolder(row);
			break;			
		default:
			
			break;
		}
		viewHolder.setOnItemClickListener(this);
		return viewHolder; 
	}
	public void popMyText(String text){
		ChatRowModel model = new ChatRowModel(userId, text);
	
		mList.add(0, model);	
		notifyItemInserted(0);
		
	}

	@Override
	public void onItemClick(int pos, View v) {
		if(listener!=null){
			listener.onItemClick(pos, v);
		}
	}
	private BaseViewHolder.OnItemClickListener listener;
	public void setOnItemClickListener(BaseViewHolder.OnItemClickListener clickListener){
		this.listener = clickListener;
	}

	public void add(int i, ChatRowModel model) {
		mList.add(i,model);
		
	}
}
