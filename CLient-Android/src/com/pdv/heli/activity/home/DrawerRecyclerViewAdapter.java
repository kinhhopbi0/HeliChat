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
import com.yqritc.recyclerviewflexibledivider.FlexibleDividerDecoration.ColorProvider;
import com.yqritc.recyclerviewflexibledivider.FlexibleDividerDecoration.VisibilityProvider;

public class DrawerRecyclerViewAdapter extends
		RecyclerView.Adapter<DrawerViewHolder>
		implements
		VisibilityProvider,
		ColorProvider,
		com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration.MarginProvider {

	private LayoutInflater inflater;
	private List<DrawerMenuItem> items = Collections.emptyList();

	private IOnClickItem listener;
	private int selectedIndex = -1;
	private Context context;
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

	public DrawerMenuItem getItemModel(int pos) {
		return items.get(pos);
	}

	@Override
	public void onBindViewHolder(DrawerViewHolder pHolder, final int pPosition) {
		DrawerMenuItem setting = items.get(pPosition);
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

		if (setting.isSelected()) {
			pHolder.itemView.setSelected(true);		
		} else {
			pHolder.itemView.setSelected(false);
		}		
		

	}

	public class DrawerViewHolder extends RecyclerView.ViewHolder implements
			View.OnClickListener {
		private TextView tvBage;
		private TextView tvRowName;
		private ImageView imvIcon;
		private View diviler;
		private View itemRow;

		public DrawerViewHolder(View itemView) {
			super(itemView);
			itemRow = itemView;
			tvBage = (TextView) itemView.findViewById(R.id.tvBage);
			tvRowName = (TextView) itemView.findViewById(R.id.tvRowName);
			imvIcon = (ImageView) itemView.findViewById(R.id.imvIcon);
			diviler = itemView.findViewById(R.id.diviler);

			if (getPosition() == 0) {
				LayoutParams l = (LayoutParams) ((RelativeLayout) itemView)
						.getLayoutParams();
				l.setMargins(0, 16, 0, 0);
			}
			itemView.setOnClickListener(this);

		}

		@Override
		public void onClick(View v) {
			// Toast.makeText(HeliApplication.getInstance(),
			// "clickl "+getPosition(), Toast.LENGTH_SHORT).show();
			if (listener != null) {
				listener.onItemClick(v, getPosition());
			}
		}

	}

	public void setSelectedPosition(int pos) {
		DrawerMenuItem item = items.get(pos);		
		if (selectedIndex == pos) {
			return;
		}
		if (selectedIndex >= 0) {
			items.get(selectedIndex).setSelected(false);
			notifyItemChanged(selectedIndex);
		}
		item.setSelected(true);
		notifyItemChanged(pos);
		this.selectedIndex = pos;
	}

	public int getSelectedPosition() {
		return selectedIndex;
	}

	public void setOnItemClickListener(IOnClickItem clickItem) {
		this.listener = clickItem;
	}

	public interface IOnClickItem {
		void onItemClick(View v, int pos);
	}

	@Override
	public boolean shouldHideDivider(int position, RecyclerView parent) {
		if (items.get(position).isDiviler()) {
			return false;
		}
		return true;
	}

	@Override
	public int dividerColor(int position, RecyclerView parent) {
		
		return context.getResources().getColor(R.color.Orange);
	}

	@Override
	public int dividerLeftMargin(int position, RecyclerView parent) {		
		View image = parent.findViewHolderForPosition(position).itemView.findViewById(R.id.imvIcon);
		LayoutParams lp = (LayoutParams) image.getLayoutParams();
		int left  = lp.leftMargin;
		return left;
	}

	@Override
	public int dividerRightMargin(int position, RecyclerView parent) {		
		return 0;
	}
}
