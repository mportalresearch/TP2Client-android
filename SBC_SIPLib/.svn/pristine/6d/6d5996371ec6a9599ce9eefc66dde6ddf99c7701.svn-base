package org.linphone.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

public class SIPBroadcastReceiver extends BroadcastReceiver
{

    @Override
    public void onReceive(Context context, Intent intent)
    {
	if (intent == null)
	{
	    return;
	}

	String action = intent.getAction();
	if (action.equalsIgnoreCase(ConnectivityManager.CONNECTIVITY_ACTION))
	{
	    boolean lNoConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
	    // if (lNoConnectivity)
	    // {
	    // Toast.makeText(context.getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
	    // }
	    Intent t = new Intent(SIPService.NETWORK_STATE_CHANGE);
	    t.putExtra("connectionstate", lNoConnectivity);
	    context.getApplicationContext().startService(t);

	}
	else if (action.equalsIgnoreCase(Intent.ACTION_SCREEN_ON) || action.equalsIgnoreCase(Intent.ACTION_SCREEN_OFF))
	{
	    boolean screenOn = action.equalsIgnoreCase(Intent.ACTION_SCREEN_ON);
	    Intent t = new Intent(SIPService.SCREEN_STATE_CHANGE);
	    t.putExtra("screenstate", screenOn);
	    context.getApplicationContext().startService(t);
	}

    }
}
