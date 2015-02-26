package com.pdv.heli.app.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by via on 2/6/15.
 */
public class NetworkServiceReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(NetworkService.getInstance(),"call broacast",Toast.LENGTH_SHORT).show();
    }
}
