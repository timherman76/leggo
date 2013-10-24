package com.leggo;

import java.io.File;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.leggo.parsing.GetArticlesCommand;
import com.leggo.parsing.GetFeedsCommand;

public class MainActivity extends Activity {


	public static final String TAG = "MainActivity";	private Context context;
	private String currentAccountName;

	private SharedPreferences prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		context = this;

		prefs = context.getSharedPreferences(
				SettingsActivity.ACCOUNT_PREFERENCE_NAME, Context.MODE_PRIVATE);
		
		testGetFeeds();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i = null;
		switch (item.getItemId()) {
		case R.id.action_settings:
			i = new Intent(this, SettingsActivity.class);
			startActivity(i);
			break;
		case R.id.action_manage:
			i = new Intent(this, ManageActivity.class);
			startActivity(i);
			break;
		}
		return true;
	}

	@Override
	public void onResume() {
		super.onResume();

		if (networkAvailability() == false) {
			noNetworkAlert();
		}

		else {
			currentAccountName = prefs
					.getString("account_selection", "default");
			Toast.makeText(context,
					"Currently logged in to " + currentAccountName,
					Toast.LENGTH_SHORT).show();

			if (currentAccountName.equals("None")
					|| currentAccountName.equals("default")) {
				noAccountAlert();
			} else {
				AccountManager accountManager = AccountManager
						.get(getApplicationContext());
				Account[] accounts = accountManager
						.getAccountsByType("com.google");

				// Find index where account is and then try to get token
				int accountIndex = 0;
				for (Account account : accounts) {
					if (account.name.equals(currentAccountName)) {
						// Get cookie here.
						String auth_token = prefs
								.getString("token", "default");
						Toast.makeText(context, "" + auth_token,
								Toast.LENGTH_SHORT).show();
						
					}
					accountIndex++;
				}

				if (accountIndex < accounts.length) {
					// Account doesn't exist anymore for some reason.
					noAccountAlert();
				}
			}
		}
	}

	private boolean networkAvailability() {
		ConnectivityManager CM = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo active = CM.getActiveNetworkInfo();
		return active != null && active.isConnected();
	}

	private void noNetworkAlert() {
		new AlertDialog.Builder(this)
				.setTitle("No Network Connection")
				.setMessage(
						"leggo cannot detect a network connection on this device.  Please check Network Settings to connect to an available network to use leggo.")
				.setPositiveButton("Okay",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// nothing
							}
						}).show();
	}

	private void noAccountAlert() {
		new AlertDialog.Builder(this)
				.setTitle("No Google Account Selected")
				.setMessage(
						"leggo requires a Google Account to store your subscriptions.  Please select an existing account or create an account in Settings.")
				.setPositiveButton("Settings",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								Intent i = null;
								i = new Intent(context, SettingsActivity.class);
								startActivity(i);
							}
						}).show();
	}
	
	public Object testGetArticles(){
		Object result = null;
		try{
			File dir = this.getFilesDir();
			String fileName = "simplecta_all.htm";
			String testFile = new File(dir + File.separator + fileName).getAbsolutePath();
			GetArticlesCommand cmd = new GetArticlesCommand();
			result = cmd.testFromFile(testFile);
		}catch (Throwable ex){
			Log.e(TAG, ex.getMessage(), ex);
		}
		return result;
	}
	
	public Object testGetFeeds(){
		Object result = null;
		try{
			File dir = this.getFilesDir();
			String fileName = "simplecta_feeds.htm";
			String testFile = new File(dir + File.separator + fileName).getAbsolutePath();
			GetFeedsCommand cmd = new GetFeedsCommand();
			result = cmd.testFromFile(testFile);
		}catch (Throwable ex){
			Log.e(TAG, ex.getMessage(), ex);
		}
		return result;
	}

}
