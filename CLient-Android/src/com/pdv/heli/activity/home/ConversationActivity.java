package com.pdv.heli.activity.home;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.pdv.heli.R;
import com.pdv.heli.app.ActivitiesManager;
import com.pdv.heli.component.chat.BubbleChatAdapter;
import com.pdv.heli.component.chat.BubbleInfo;
import com.pdv.heli.message.common.MessageMode;
import com.pdv.heli.message.detail.TextMessage;

/**
 * Created by vinhanh on 11/01/2015.
 */
public class ConversationActivity extends Activity  {
    private static final  String TAG = ConversationActivity.class.getSimpleName();
    private BubbleChatAdapter listViewAdapter;
    private Button btnSend;
    private ListView listView;
    private EditText editText;
    private boolean isVisible = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitiesManager.getInstance().setCurrentActivity(this);
        
        setContentView(R.layout.activity_splash);
        btnSend = (Button) findViewById(R.id.btnSend);
        editText = (EditText) findViewById(R.id.edit_text_chat);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(editText.getText().toString().isEmpty()){
                    btnSend.setVisibility(View.GONE);
                }else{
                    btnSend.setVisibility(View.VISIBLE);
                }
            }
        });
        listView = (ListView) findViewById(R.id.listViewConvention);

        listViewAdapter = new BubbleChatAdapter(getApplicationContext(),R.layout.view_chat_item_friend);
        listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
        listView.setAdapter(listViewAdapter);
        listViewAdapter.add(new BubbleInfo("Xin cha ban", false));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {      
        return super.onOptionsItemSelected(item);
    }

    public void editTextClick(View v){

    }


    public void btnSendClick(View v){
        String message = editText.getText().toString().trim();
        editText.setText("");
        // lost focus editext
        editText.clearFocus();
        /// hide virtual key board
        InputMethodManager imm = (InputMethodManager)getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

        addBubble(new BubbleInfo(message,true));
        postMessage(message);
       
        Intent intent = new Intent("com.phamvinh.alo.service.NetworkService");
        sendBroadcast(intent);

    }

    private void postMessage(String pContent) {
       
        
        
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver(){
    	@Override
    	public void onReceive(Context context, Intent intent) {    		
    		addBubble(new BubbleInfo("hello", false));
    	}
    };
    @Override
    protected void onResume() {
    	ActivitiesManager.getInstance().setCurrentActivity(this);
        super.onResume();
     // Register mMessageReceiver to receive messages.
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
            new IntentFilter("my-event"));
        isVisible = true;
    }

    @Override
    protected void onPause() {
    	ActivitiesManager.getInstance().removeCurrentActivity(this);
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        isVisible = false;
    }

    public boolean isVisible() {
        return isVisible;
    }
    public void insertBubbleChat(TextMessage message){
       addBubble(new BubbleInfo(message.getContent(),false));
    }
    public void addBubble(BubbleInfo info){
        listViewAdapter.add(info);
        listView.setSelection(listViewAdapter.getCount()-1);
    }


	public void showTextMesage(TextMessage message) {
		
		addBubble(new BubbleInfo(message.getContent(),message.getMessageMode()==MessageMode.SEND));
	}
}
