package com.leggo;

import java.io.IOException;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class SettingsActivity extends PreferenceActivity {

	private Context context;

	public static final String TAG = "SettingsActivity";

	private PreferenceManager prefManager;
	private OnPreferenceChangeListener accountChangeListener;
	private OnPreferenceChangeListener themeChangeListener;

	private SharedPreferences prefs;
	private SharedPreferences.Editor editor;

	private ListPreference accountSelectionPref;

	private String ACCOUNT_SELECTION = "account_selection";
	private String NO_ACCOUNT_NAME = "None";

	private String[] idRequests;
	private int id;

	private String auth_token;

	@SuppressWarnings("deprecation")
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
					
			
				
					if ((MainActivity.articleScroll).getChildCount() > 0)
						(MainActivity.articleScroll).removeAllViews();
					
					if (ManageActivity.feedScroll != null && (ManageActivity.feedScroll).getChildCount() > 0)
						(ManageActivity.feedScroll).removeAllViews();
					
					MainActivity.shouldRefresh = true;
					ManageActivity.shouldRefresh = true;
					
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
		
		// Listen for changes in Night Mode
		themeChangeListener = new OnPreferenceChangeListener() {
			public boolean onPreferenceChange(Preference preference, Object newValue) {

				if (preference.getKey().equals("nightMode")) {
					boolean nightToggle = (Boolean) newValue;
					if (nightToggle == false) 
					{
						Theme.changeTheme(SettingsActivity.this, Theme.THEME_DEFAULT);
						MainActivity.shouldRestart = true;
						ManageActivity.shouldRestart = true;
						return true;
					}
					else
					{
						Theme.changeTheme(SettingsActivity.this, Theme.THEME_NIGHT);
						MainActivity.shouldRestart = true;
						ManageActivity.shouldRestart = true;
						return true;
					}
				}

				return false;
			}
		};
		
		// Set the listener to detect changes on the nightmode preferences
		CheckBoxPreference nightModePref = (CheckBoxPreference) prefManager.findPreference("nightMode");
		nightModePref.setOnPreferenceChangeListener(themeChangeListener);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i = null;
		switch (item.getItemId()) {
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
					ListPreference accountPref = (ListPreference) prefManager.findPreference(ACCOUNT_SELECTION);
					accountPref.setValue(accountName);
					accountSelectionPref.setSummary("Logged in as: " + accountName);

					// Get and set cookie into preferences
					AuthCookie.getCookie(auth_token,context);

				} else {
					// Send permission prompt intent.
					// Set flag on this intent so that we get the result of that
					// intent
					int flags = i.getFlags();
					flags &= ~Intent.FLAG_ACTIVITY_NEW_TASK;
					i.setFlags(flags);

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
	@SuppressWarnings("deprecation")
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
}
