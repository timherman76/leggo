package com.leggo;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.widget.TextView;

public class SplashScreen extends Activity {

	public static Context context;

	private static int SPLASH_TIME = 2500;

	private SharedPreferences prefs;
	private SharedPreferences.Editor editor;

	private String currentAccountName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		context = this;

		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		editor = prefs.edit();

		TextView splashText = (TextView) findViewById(R.id.splashText);

		splashText.setText("Searching for account.");

		if (Utils.networkAvailability(this) == true) {
			AccountManager accountManager = AccountManager
					.get(getApplicationContext());
			Account[] accounts = accountManager.getAccountsByType("com.google");

			// If for some reason, your account was deleted, set account to None
			if (accounts.length == 0) {
				editor.putString("account_selection", "None");
				editor.commit();
			}

			currentAccountName = prefs
					.getString("account_selection", "default");

			if ((currentAccountName.equals("None") || currentAccountName
					.equals("default"))) {
				splashText.setText("No account found.");
			}

			else {
		
				// Find index where account is
				int accountIndex = 0;
				for (Account account : accounts) {
					if (account.name.equals(currentAccountName)) {
						// Found account, revalidate here
						splashText.setText("Authenticating as: "
								+ currentAccountName);
						AuthCookie.revalidateCookie(accountIndex, this);
					}
					accountIndex++;
				}

				if (accountIndex < accounts.length) {
					// Account doesn't exist anymore for some reason.
					editor.putString("account_selection", "None");
					editor.commit();
				}
			}

		}

		else {
			splashText.setText("No internet connection.");
		}

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				Intent i = new Intent(SplashScreen.this, MainActivity.class);
				startActivity(i);

				finish();
			}
		}, SPLASH_TIME);
	}

}
