package com.leggo;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class SettingsActivity extends PreferenceActivity {

	private Context context;

	public static final String TAG = "SettingsActivity";

	private PreferenceManager prefManager;
	private OnPreferenceChangeListener accountChangeListener;
	
	private SharedPreferences prefs;
	private SharedPreferences.Editor editor;
	
	private ListPreference accountSelectionPref;

	private String ACCOUNT_SELECTION = "account_selection";
	private String NO_ACCOUNT_NAME = "None";
	private String APP_AUTH_URL = "http://simplecta.appspot.com/_ah/login?continue=http://localhost/&auth=";

	private String[] idRequests;
	private int id;

	private String auth_token;
	private String cookie;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		context = this;

		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		editor = prefs.edit();
		
		Theme.setPrefTheme(this);
        
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.activity_settings);
		
		// Create shared preference and set the name
		prefManager = getPreferenceManager();
		
		accountSelectionPref = (ListPreference) findPreference("account_selection");
		
		id = 0;
		idRequests = new String[10];

		// This listener checks whether the user selects another account
		accountChangeListener = new OnPreferenceChangeListener() {
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				// Only listen on the account selection preference
				if (preference.getKey().equals(ACCOUNT_SELECTION)) {
					// Set accountName a newValue
					String accountName = (String) newValue;

					// If there is no account name, set to no account
					if (accountName.equals(NO_ACCOUNT_NAME)) {
						ListPreference accountPref = (ListPreference) prefManager.findPreference(ACCOUNT_SELECTION);
						accountPref.setValue(NO_ACCOUNT_NAME);
						accountSelectionPref.setSummary("Logged in as: " + NO_ACCOUNT_NAME);

						return true;
					}

					// Store accounts into array
					AccountManager accountManager = AccountManager.get(getApplicationContext());
					Account[] accounts = accountManager.getAccountsByType("com.google");

					// Find index where account is and then try to get token
					int accountIndex = 0;
					for (Account account : accounts) {
						if (account.name.equals(accountName)) {
							accountManager.getAuthToken(accounts[accountIndex], "ah", true, new GetAuthTokenCallBack(accountName), null);

							return false;
						}
						accountIndex++;
					}

				}
				return false;
			}
		};

		// Set the listener the detects change on the account preferences
		ListPreference accountPref = (ListPreference) prefManager.findPreference(ACCOUNT_SELECTION);
		accountPref.setOnPreferenceChangeListener(accountChangeListener);

		Preference add_account_button = (Preference) findPreference("add_account");
		add_account_button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference arg0) {
				AccountManager accountManager = AccountManager.get(getApplicationContext());
				accountManager.addAccount("com.google", "ah", null, new Bundle(), SettingsActivity.this, null, null);
				return true;
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public boolean onOonsItemSelected(MenuItem item) {
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
		accountSelectionPref.setSummary("Logged in as: " + prefs.getString("account_selection", "default"));

		getAccountEntries();
	}

	private void getAccountEntries() {
		ListPreference accountPref = (ListPreference) prefManager.findPreference(ACCOUNT_SELECTION);
		if (accountPref != null) {
			// Fetch google accounts
			AccountManager accountManager = AccountManager.get(getApplicationContext());
			Account[] accounts = accountManager.getAccountsByType("com.google");

			// Increase size of account list array by 1 since there is a 'none'
			// account
			String[] accountList = new String[accounts.length + 1];

			// Set first account as None
			accountList[0] = NO_ACCOUNT_NAME;

			// Get the rest of the accounts
			int accountListIndex = 1;
			for (Account account : accounts) {
				accountList[accountListIndex] = account.name;
				accountListIndex++;
			}

			// Set the entries and values
			accountPref.setEntries(accountList);
			accountPref.setEntryValues(accountList);
		}
	}

	// Access token callback
	private class GetAuthTokenCallBack implements AccountManagerCallback<Bundle> {
		String accountName;

		public GetAuthTokenCallBack(String name) {
			accountName = name;
		}

		public void run(AccountManagerFuture<Bundle> result) {
			Bundle bundle;
			try {
				// Get bundle result
				bundle = result.getResult();
				Intent i = (Intent) bundle.get(AccountManager.KEY_INTENT);
				auth_token = bundle.getString(AccountManager.KEY_AUTHTOKEN);

				editor.putString("token", auth_token);
				editor.commit();

				if (i == null) {
					// User input not required, we have permission. Set
					// preference to the account.
					Toast.makeText(context, "DEBUG: Already Have Permission", Toast.LENGTH_SHORT).show();
					ListPreference accountPref = (ListPreference) prefManager.findPreference(ACCOUNT_SELECTION);
					accountPref.setValue(accountName);
					accountSelectionPref.setSummary("Logged in as: " + accountName);

					// Get and set cookie into preferences
					getCookie(auth_token);

				} else {
					// Send permission prompt intent.
					// Set flag on this intent so that we get the result of that
					// intent
					int flags = i.getFlags();
					flags &= ~Intent.FLAG_ACTIVITY_NEW_TASK;
					i.setFlags(flags);

					Toast.makeText(context, "DEBUG: Do Not Have Permission, Requesting It", Toast.LENGTH_SHORT).show();
					idRequests[id] = accountName;

					// StartActvitityForResult has a second parameter
					// identifying the call
					startActivityForResult(i, id);
					// Increment id in a way that it will not exceed the end of
					// the array
					id = (id + 1) % idRequests.length;
				}

			} catch (OperationCanceledException e) {
				Toast.makeText(context, "Operation Canceled Exception", Toast.LENGTH_SHORT).show();
			} catch (AuthenticatorException e) {
				Toast.makeText(context, "Authenticator Exception", Toast.LENGTH_SHORT).show();
			} catch (IOException e) {
				Toast.makeText(context, "IO Exception", Toast.LENGTH_SHORT).show();
			}
		}
	};

	// Called after the permission prompt intent.
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// User allows permission
		if (resultCode == RESULT_OK) {
			// The requestCode is the original identifier that was supplied in
			// startActivityForResult
			String accountName = idRequests[requestCode];
			if (accountName != null) {
				// Set preference to this new account
				ListPreference accountPref = (ListPreference) prefManager.findPreference(ACCOUNT_SELECTION);
				accountPref.setValue(accountName);
				accountSelectionPref.setSummary("Logged in as: " + accountName);
				idRequests[requestCode] = null;
				Toast.makeText(context, "Account Access Granted", Toast.LENGTH_SHORT).show();

				// Get and set cookie into preferences
				// Find index where account is and then try to get token
				AccountManager accountManager = AccountManager.get(getApplicationContext());
				Account[] accounts = accountManager.getAccountsByType("com.google");
				int accountIndex = 0;
				for (Account account : accounts) {
					if (account.name.equals(accountName)) {
						accountManager.getAuthToken(accounts[accountIndex], "ah", true, new GetAuthTokenCallBack(accountName), null);
					}
					accountIndex++;
				}
			}
			// User denies permission
			else if (resultCode == RESULT_CANCELED) {
				// Set preference to 'None' account
				ListPreference accountPref = (ListPreference) prefManager.findPreference(ACCOUNT_SELECTION);
				accountPref.setValue(NO_ACCOUNT_NAME);
				accountSelectionPref.setSummary("Logged in as: " + NO_ACCOUNT_NAME);
				Toast.makeText(context, "Account Access Denied", Toast.LENGTH_SHORT).show();
			}
		}

	}

	private void getCookie(final String authToken) {
		new Thread(new Runnable() {
			public void run() {
				String href = APP_AUTH_URL + authToken;

				DefaultHttpClient httpclient = new DefaultHttpClient();
				final HttpParams params = new BasicHttpParams();

				// Don't follow redirects
				HttpClientParams.setRedirecting(params, false);
				httpclient.setParams(params);
				HttpGet httpget = new HttpGet(href);
				try {
					HttpResponse response = httpclient.execute(httpget);
					HttpEntity entity = response.getEntity();
					if (entity != null) {
						entity.consumeContent();
					}
					// Get all the cookies
					List<Cookie> cookies = httpclient.getCookieStore().getCookies();
					if (cookies.isEmpty()) {
						Log.d(TAG, "No Cookies");
					} else {
						// Search for the SACSID cookie and store it
						for (int i = 0; i < cookies.size(); i++) {
							Cookie c = cookies.get(i);
							if (c.getName().contentEquals("ACSID")) {
								Log.d(TAG, "ACSID Found");
								cookie = c.getValue();
								Log.d(TAG, "Cookie set to: " + cookie);
								editor.putString("cookie", cookie);
								editor.commit();
							}
						}
					}
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}).start();
	}
}
