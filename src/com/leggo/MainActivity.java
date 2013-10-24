package com.leggo;

import java.io.IOException;
import java.util.List;

import com.leggo.parsing.GetArticlesCommand;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends Activity {

	private Context context;
	private String currentAccountName;
	public static List<Article> articles = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		loadArticles();
		context = this;

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		ActionBar action = getActionBar();
		action.show();
		return super.onCreateOptionsMenu(menu);
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
		SharedPreferences prefs = context.getSharedPreferences(SettingsActivity.ACCOUNT_PREFERENCE_NAME, Context.MODE_PRIVATE);
		currentAccountName = prefs.getString("account_selection", "default");
		Toast.makeText(context, "Currently logged in to " + currentAccountName, Toast.LENGTH_SHORT).show();

		if (currentAccountName.equals("None") || currentAccountName.equals("default")) {
			noAccountAlert();
		} else {
			AccountManager accountManager = AccountManager.get(getApplicationContext());
			Account[] accounts = accountManager.getAccountsByType("com.google");

			// Find index where account is and then try to get token
			int accountIndex = 0;
			for (Account account : accounts) {
				if (account.name.equals(currentAccountName)) {
					// get cookie here
				}
				accountIndex++;
			}

			if (accountIndex < accounts.length) {
				// Account doesn't exist anymore for some reason.
			}
		}
	}
	
	private void loadArticles()
    {
    	if(networkAvailability()) //until login is taken care of
        {
    		GetArticles get = new GetArticles();
    		GetArticlesCommand command = new GetArticlesCommand();
    		get.execute(command);
        }
     }  

	private boolean networkAvailability() {
		ConnectivityManager CM = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo active = CM.getActiveNetworkInfo();
		return active != null && active.isConnected();
	}

	private void noNetworkAlert() {
		new AlertDialog.Builder(this).setTitle("No Network Connection").setMessage("leggo cannot detect a network connection on this device.  Please check Network Settings to connect to an available network to use leggo.").setPositiveButton("Okay", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// nothing
			}
		}).show();
	}

	private void noAccountAlert() {
		new AlertDialog.Builder(this).setTitle("No Google Account Selected").setMessage("leggo requires a Google Account to store your subscriptions.  Please select an existing account or create an account in Settings.").setPositiveButton("Settings", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Intent i = null;
				i = new Intent(context, SettingsActivity.class);
				startActivity(i);
			}
		}).show();
	}
	
	public void listArticles() {
		if(articles != null)
        {
        	Log.d("ARTICLES", "Here " + articles.size());
                LinearLayout feedScroll = (LinearLayout)findViewById(R.id.article_list);
                for(int i = 0; i < 2*articles.size(); i += 2)
                {
                        LinearLayout currArticle = new LinearLayout(this);
                        currArticle.setOrientation(LinearLayout.HORIZONTAL);
                        Button articleName = new Button(this);
                        articleName.setId(i);
                        articleName.setText((CharSequence)(articles.get(i/2).getTitle()));
                        articleName.setOnClickListener(new View.OnClickListener() {
                        	public void onClick(View v) {
                        		int id = v.getId();
                        		if(id%2 == 0);
                        			//replace with call for view;         
                        	}
                        });
                        Button peek = new Button(this);
                        peek.setId(i+1);
                        peek.setText("peek");
                        peek.setOnClickListener(new View.OnClickListener() {
					        public void onClick(View v) {
					                 int id = v.getId();
					                 if(id%2 == 1);
					                       //replace with call for peek         
					        }
				        });
                        currArticle.addView(articleName);
                        currArticle.addView(peek);
                        feedScroll.addView(currArticle);
                }
        }
	}
	
	protected class GetArticles extends AsyncTask<GetArticlesCommand, Integer, List<Article>> {
		@Override
		protected List<Article> doInBackground(GetArticlesCommand... params)
		{
			GetArticlesCommand get = params[0];
			List<Article> articles = null;
			try{
				articles = (List<Article>)get.parseData();
				Log.d("ARTICLES","In try " + articles.size());
			} catch(IOException e) {
				Log.d("ARTICLES", "IOException caught");
				return null;
			}
			return articles;
		}
		
		@Override
	     protected void onPostExecute(List<Article> result) {
			Log.d("ARTICLES", "On Post Execute "+result.size());
			MainActivity.articles = result;
			listArticles();  
	     }
	}

}
