package com.leggo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Utils {
	
	public static final String urlRegex = "(http|https):\\/\\/([\\w\\-_]+(?:(?:\\.[\\w\\-_]+)+))([\\w\\-\\.,@?^=%&amp;:/~\\+#]*[\\w\\-\\@?^=%&amp;/~\\+#])?";
	
	
	public static boolean networkAvailability(Activity activity) {
		ConnectivityManager CM = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo active = CM.getActiveNetworkInfo();
		return active != null && active.isConnected();
	}

	public static void noNetworkAlert(Activity activity) {
		new AlertDialog.Builder(activity)
				.setTitle("No Network Connection")
				.setMessage(
						"leggo cannot detect a network connection on this device. Please check Network Settings to connect to an available network to use leggo.")
				.setPositiveButton("Okay",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// nothing
							}
						}).show();
	}
	
	public static void timeOutAlert(Activity activity) {
		new AlertDialog.Builder(activity)
				.setTitle("Connection to Server Failed")
				.setMessage(
						"Your connection to the server has timed out.  Please check your network connection and try again.")
				.setPositiveButton("Okay",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// nothing
							}
						}).show();
	}

	public static void noAccountAlert(final Activity activity) {
		new AlertDialog.Builder(activity)
				.setTitle("No Google Account Selected")
				.setMessage(
						"leggo requires a Google Account to store your subscriptions. Please select an existing account or create an account in Settings.")
				.setPositiveButton("Settings",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								Intent i = null;
								i = new Intent(activity, SettingsActivity.class);
								activity.startActivity(i);
							}
						}).show();
	}
	
	public static void restartActivity(Activity activity)
	{
		Intent intent = activity.getIntent();

		intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

		activity.finish();
		activity.overridePendingTransition(0, 0);

		activity.startActivity(intent);
		activity.overridePendingTransition(0, 0);
	}

}
