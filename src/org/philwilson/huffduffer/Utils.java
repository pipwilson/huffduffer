package org.philwilson.huffduffer;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class Utils {
	
	private static final String TAG = "HUFFDUFFER_UTILS";
	
	public static boolean isConnectedToNetwork(Activity activity) {
		Log.d(TAG, "checking network connectivity");
		ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			Log.d(TAG, "connected to network");
			return true;
		} else {
			Log.d(TAG, "not connected to network");
			return false;
		}
	}

}
