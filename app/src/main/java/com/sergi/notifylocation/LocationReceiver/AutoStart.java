package com.sergi.notifylocation.LocationReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AutoStart extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Intent startServiceIntent = new Intent (context, LocationService.class);
        context.startService(startServiceIntent);
    }
}
