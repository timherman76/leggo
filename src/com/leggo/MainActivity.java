package com.leggo;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.leggo.Article.ArticleSearchResult;
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
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
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
		SimpleDateFormat df = new SimpleDateFormat("HH:mm");
		Date now = new Date();
		TextView refreshBar = (TextView) findViewById(R.id.main_refresh_bar);
		refreshBar.setText(df.format(now).toString());
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
		case R.id.action_search:
			ActionBar actionBar = getActionBar();
			//actionBar.hide();
			actionBar.setCustomView(R.layout.searchbar);
			EditText search = (EditText) actionBar.getCustomView().findViewById(R.id.action_searchfield);
			search.setOnEditorActionListener(new OnEditorActionListener() {

			      @Override
			      public boolean onEditorAction(TextView v, int actionId,
			          KeyEvent event) {
			    	  Article searcher = new Article();
			    	  List<ArticleSearchResult> results = searcher.search(v.getText().toString(),articles);
			    	  Log.d("LOLOL", v.getText().toString());
						LinearLayout linearLayout = (LinearLayout)findViewById(R.id.article_list);
						if(((LinearLayout) linearLayout).getChildCount() > 0) 
						    ((LinearLayout) linearLayout).removeAllViews();
  			    	  LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                              LayoutParams.MATCH_PARENT,
                              LayoutParams.WRAP_CONTENT, 0.10f);
                      LinearLayout.LayoutParams param2 = new LinearLayout.LayoutParams(
                              LayoutParams.MATCH_PARENT,
                              LayoutParams.WRAP_CONTENT, 0.90f);
			    	  for(int i = 0; i < 2*results.size(); i+=2)
			    	  {
			    		  LinearLayout articleScroll = (LinearLayout)findViewById(R.id.article_list);
			    		  LinearLayout currArticle = new LinearLayout(getBaseContext());
	                        currArticle.setOrientation(LinearLayout.HORIZONTAL);
	                        currArticle.setPadding(5, 5, 5, 5);
	                        Button articleName = new Button(getBaseContext());
	                        articleName.setId(i);
	                        articleName.setText((CharSequence)(results.get(i/2).article.getTitle()));
	                        articleName.setTextColor(Color.parseColor("#000000"));
	                        articleName.setGravity(Gravity.LEFT);
	                        articleName.setBackground(getBaseContext().getResources().getDrawable(R.drawable.textlines));
	                        articleName.setOnClickListener(new View.OnClickListener() {
	                        	public void onClick(View v) {
	                        		int id = v.getId();
	                        		if(id%2 == 0);
	                        			//replace with call for view;         
	                        	}
	                        });
	                        articleName.setLayoutParams(param);
	                        ImageButton peek = new ImageButton(getBaseContext());
	                        peek.setId(i+1);
	                        peek.setBackground(getBaseContext().getResources().getDrawable(R.drawable.ic_read));
	                        peek.setOnClickListener(new View.OnClickListener() {
						        public void onClick(View v) {
						                 int id = v.getId();
						                 if(id%2 == 1);
						                       //replace with call for peek         
						        }
					        });
	                        peek.setLayoutParams(param2);
	                        currArticle.addView(articleName);
	                        currArticle.addView(peek);
	                        articleScroll.addView(currArticle);
			    		  
			    	  }
			    	  ActionBar actionBar = getActionBar();
			    	  actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME
						        | ActionBar.DISPLAY_SHOW_HOME);
			        return false;
			      }
			    });
			    actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
			        | ActionBar.DISPLAY_SHOW_HOME);
			//actionBar.show();
			
			break;
		case R.id.action_refresh:
			LinearLayout linearLayout = (LinearLayout)findViewById(R.id.article_list);
			if(((LinearLayout) linearLayout).getChildCount() > 0) 
			    ((LinearLayout) linearLayout).removeAllViews();
			GetArticlesCommand refresh = new GetArticlesCommand();
			GetArticles get = new GetArticles();
			SimpleDateFormat df = new SimpleDateFormat("HH:mm");
			get.execute(refresh);
			Date now = new Date();
			TextView refreshBar = (TextView) findViewById(R.id.main_refresh_bar);
			refreshBar.setText(df.format(now).toString());
			break;
		}
		return true;
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
            LinearLayout articleScroll = (LinearLayout)findViewById(R.id.article_list);
	    	LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                      LayoutParams.MATCH_PARENT,
                      LayoutParams.WRAP_CONTENT, 0.10f);
            LinearLayout.LayoutParams param2 = new LinearLayout.LayoutParams(
                      LayoutParams.MATCH_PARENT,
                      LayoutParams.WRAP_CONTENT, 0.90f);
                for(int i = 0; i < 2*articles.size(); i += 2)
                {
                        LinearLayout currArticle = new LinearLayout(this);
                        currArticle.setOrientation(LinearLayout.HORIZONTAL);
                        currArticle.setPadding(100000, 100000, 100000, 100000);
                        currArticle.setBackground(this.getResources().getDrawable(R.drawable.box));
                        Button articleName = new Button(this);
                        articleName.setId(i);
                        articleName.setText((CharSequence)(articles.get(i/2).getTitle()));
                        articleName.setBackground(getResources().getDrawable(R.drawable.roundbutton));
                        articleName.setTextColor(Color.parseColor("#000000"));
                        articleName.setGravity(Gravity.LEFT);
                        articleName.setOnClickListener(new View.OnClickListener() {
                        	public void onClick(View v) {
                        		int id = v.getId();
                        		if(id%2 == 0);
                        			//replace with call for view;         
                        	}
                        });
                        articleName.setLayoutParams(param);
                        ImageButton peek = new ImageButton(this);
                        peek.setId(i+1);
                        peek.setBackground(getBaseContext().getResources().getDrawable(R.drawable.ic_read));
                        peek.setOnClickListener(new View.OnClickListener() {
					        public void onClick(View v) {
					                 int id = v.getId();
					                 if(id%2 == 1);
					                       //replace with call for peek         
					        }
				        });
                        peek.setLayoutParams(param2);
                        currArticle.addView(articleName);
                        currArticle.addView(peek);
                        articleScroll.addView(currArticle);
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
