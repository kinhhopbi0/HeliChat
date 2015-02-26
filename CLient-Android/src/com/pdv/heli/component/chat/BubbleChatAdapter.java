package com.pdv.heli.component.chat;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pdv.heli.R;

/**
 * Created by via on 2/3/15.
 */
public class BubbleChatAdapter extends ArrayAdapter<BubbleInfo> {

    private List<BubbleInfo> messageList = new ArrayList<>();
    private TextView textViewContent;
    private Layout mLayout;

    /**
     * Constructor
     *
     * @param context  The current context.
     * @param resource The resource ID for a layout file containing a TextView to use when
     *                 instantiating views.
     * @param objects  The objects to represent in the ListView.
     */
    public BubbleChatAdapter(Context context, int resource, BubbleInfo[] objects) {
        super(context, resource, objects);
    }

    /**
     * Constructor
     *
     * @param context  The current context.
     * @param resource The resource ID for a layout file containing a TextView to use when
     */
    public BubbleChatAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public void add(BubbleInfo object) {
        messageList.add(object);
        super.add(object);
    }

    @Override
    public int getCount() {
        return messageList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        BubbleInfo info = getItem(position);
        if(info.isYou()){
            row = inflater.inflate(R.layout.view_chat_item_friend,parent,false);
        }else{
            row = inflater.inflate(R.layout.view_chat_item_you,parent,false);
        }
        textViewContent = (TextView) row.findViewById(R.id.textViewContent);
        textViewContent.setText(info.getTextContent());
        return row;
    }
}
