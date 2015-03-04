package com.pdv.heli.activity.home;

import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.pdv.heli.R;
import com.pdv.heli.activity.home.DrawerRecyclerViewAdapter.DrawerViewHolder;
import com.pdv.heli.model.DrawerMenuItem;

public class DrawerRecyclerViewAdapter extends
		RecyclerView.Adapter<DrawerViewHolder> {

	private LayoutInflater inflater;
	private List<DrawerMenuItem> items = Collections.emptyList();
	private Context context;
	private IOnClickItem listener;

	public DrawerRecyclerViewAdapter(Context context, List<DrawerMenuItem> data) {
		inflater = LayoutInflater.from(context);
		items = data;
		this.context = context;

	}

	@Override
	public DrawerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = inflater.inflate(R.layout.c_drawer_row, parent, false);
		DrawerViewHolder drawerViewHolder = new DrawerViewHolder(view);
		return drawerViewHolder;
	}

	@Override
	public int getItemCount() {
		return items.size();
	}

	public void removeItem(int pos) {
		items.remove(pos);
		notifyItemRemoved(pos);
	}
	public DrawerMenuItem getItemModel(int pos){
		return items.get(pos);
	}
	@Override
	public void onBindViewHolder(DrawerViewHolder pHolder, final int pPosition) {
		DrawerMenuItem setting = items.get(pPosition);
		if(setting.isDiviler()){
			pHolder.diviler.setVisibility(View.VISIBLE);
			pHolder.tvRowName.setVisibility(View.GONE);
			pHolder.tvBage.setVisibility(View.GONE);
			pHolder.imvIcon.setVisibility(View.GONE);
			
		}else{
			pHolder.tvRowName.setText(setting.getName());
			int bageCount = setting.getCount();
			if (bageCount > 0) {
				pHolder.tvBage.setText(setting.getCount() + "");
			} else {
				pHolder.tvBage.setText("");
			}
			int iconId = setting.getIconResId();
			if (iconId != 0) {
				pHolder.imvIcon.setImageResource(iconId);
			}
		}
		
		
		

	}

	class DrawerViewHolder extends RecyclerView.ViewHolder implements
			View.OnClickListener {
		private TextView tvBage;
		private TextView tvRowName;
		private ImageView imvIcon;
		private View diviler;

		public DrawerViewHolder(View itemView) {
			super(itemView);
			itemView.setOnClickListener(this);
			tvBage = (TextView) itemView.findViewById(R.id.tvBage);
			tvRowName = (TextView) itemView.findViewById(R.id.tvRowName);
			imvIcon = (ImageView) itemView.findViewById(R.id.imvIcon);
			diviler = itemView.findViewById(R.id.diviler);
			
			if(getPosition() == 1){
				LayoutParams l = (LayoutParams) ((RelativeLayout)itemView).getLayoutParams();
				l.setMargins(0, 16, 0, 0);
			}
			
		}
		
		@Override
		public void onClick(View v) {			
			if(listener != null){
				listener.onItemClick(v, getPosition());
			}
		}

	}
	public interface IOnClickItem{
		void onItemClick(View v, int pos);
	}
	public void setOnItemClickListener(IOnClickItem clickItem){
		this.listener = clickItem;
	}
}
