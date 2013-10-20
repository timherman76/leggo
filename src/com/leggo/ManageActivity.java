package com.leggo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;

import com.leggo.Feed;

public class ManageActivity extends Activity {
	boolean atom = false;
	boolean rss = false;
	private Feed allFeeds[] = null;
	private File sdCard = Environment.getExternalStorageDirectory(); 
	public File filesDir = new File(sdCard + "/Android/data/com.leggo/files");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage);
		filesDir.mkdirs();
		refresh();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
    private boolean networkAvailability() {
        ConnectivityManager CM 
              = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo active = CM.getActiveNetworkInfo();
        return active != null && active.isConnected();
    }

    private void loadFeeds()
	{
		File output = new File(filesDir, "feeds.htm");
		if(networkAvailability() && false) //until login is taken care of
		{
			Log.d("FEEDS", "Trying now");
			try {
				filesDir.mkdirs();
				HttpGet get = new HttpGet("http://simplecta.com/feeds/");
				HttpClient getFile = new DefaultHttpClient();
	            HttpResponse response = getFile.execute(get);
	            if (response != null) {
	                 HttpEntity entity = response.getEntity();
	                if(entity != null)
	                {
	                  try {
	                    FileOutputStream writeToFile = new FileOutputStream(output);
	                    entity.writeTo(writeToFile);
	                    writeToFile.close();
	                  } catch (Exception e1) {
	                    //e1.printStackTrace();
	                  }
	                }
	            }
			} catch (Exception e) {
				Log.d("FEEDS", "Error", e);
			}
		}
		loadFeeds(output);
	}
		
	
	private void loadFeeds(File input)
	{
		if(input.exists())
		{
			Log.d("FEEDS", "Here. It exists");
			Document feedsPage = null;
			try {
				feedsPage = Jsoup.parse(input, "UTF-8", "http://simplecta.com/");
				Elements feeds = feedsPage.select("a.largefeedlink");
				Elements unsubs = feedsPage.select("a.peek");
				allFeeds = new Feed[feeds.size()];
				for(int i = 0; i < feeds.size(); i++)
				{
					Element currFeed = feeds.get(i);
					Element currUnsub = unsubs.get(i);
					String feedName = currFeed.text();
					String feedLink = currFeed.attr("href");
					String unsubLink = currUnsub.attr("href");
					if(!feedName.isEmpty() && !unsubLink.isEmpty() && !feedLink.isEmpty())
					{
						allFeeds[i] = new Feed(feedLink, feedName, unsubLink);
						Log.d("FEEDS", "storing a feed");
					}
				}
			} catch (IOException e) {
				Log.d("FEEDS", "Error", e);
			}
		} else {
			Toast warning = Toast.makeText(this, "Please connect to the internet to load your feeds.", Toast.LENGTH_SHORT);
			warning.show();
			return;
		}
	}
	
	public void addFeed(View v) {
		EditText uri = (EditText)findViewById(R.id.add_feed_uri);
		String text = uri.getText().toString();
		if(text == null || text.isEmpty())
		{
			Toast warning = Toast.makeText(this, "Please enter a feed url.", Toast.LENGTH_SHORT);
			warning.show();
			return;
		}
		else if(atom == false && rss == false)
		{
			Toast warning = Toast.makeText(this, "Please select a feed type and try again.", Toast.LENGTH_SHORT);
			warning.show();
			return;
		}
		String addlink = "http://simplecta.com/";
		if(atom)
		{
			try {
				addlink += ("addAtom/" + URLEncoder.encode(text, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				return;
			}
		} else {
			try {
				addlink += ("addRSS/" + URLEncoder.encode(text, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				return;
			}
		}
		if(networkAvailability() && false)
		{
			HttpGet get = new HttpGet(addlink);
			HttpClient conn = new DefaultHttpClient();
			try {
				conn.execute(get);
			} catch (Exception e) {
				return;
			}
			//allFeeds = null;
			refresh();
		} else {
			Toast warning = Toast.makeText(this, "Please connect to the internet to add a feed.", Toast.LENGTH_SHORT);
			warning.show();
			return;
		}
		
	}
	
	public void onRadioButtonClicked(View view) {
	    // Is the button now checked?
	    boolean checked = ((RadioButton) view).isChecked();
	    
	    // Check which radio button was clicked
	    switch(view.getId()) {
	        case R.id.radio_atom:
	            if (checked)
	                setAtom();
	            break;
	        case R.id.radio_rss:
	            if (checked)
	                setRSS();
	            break;
	    }
	}
	
	private void listFeeds()
	{
		if(allFeeds != null)
		{
			LinearLayout feedScroll = (LinearLayout)findViewById(R.id.feed_list);
			for(int i = 0; i < 2*allFeeds.length; i += 2)
			{
				LinearLayout currFeed = new LinearLayout(this);
				currFeed.setOrientation(LinearLayout.HORIZONTAL);
				Button feedName = new Button(this);
				feedName.setId(i);
				feedName.setText((CharSequence)(allFeeds[i/2].getFeedName()));
				feedName.setOnClickListener(new View.OnClickListener() {
		             public void onClick(View v) {
		            	 int id = v.getId();
		            	 if(id == R.id.add_feed)
		            		 addFeed(v);
		            	 else if(id%2 == 0)
		            		 viewFeed(allFeeds[id/2]);         	 
		             }
		         });
				Button unsubscribe = new Button(this);
				unsubscribe.setId(i+1);
				unsubscribe.setText("unsubscribe");
				unsubscribe.setOnClickListener(new View.OnClickListener() {
		             public void onClick(View v) {
		            	 int id = v.getId();
		            	 if(id == R.id.add_feed)
		            		 addFeed(v);
		            	 else if(id%2 == 1)
		            		 allFeeds[(id-1)/2].unsubscribe();         	 
		             }
		         });
				currFeed.addView(feedName);
				currFeed.addView(unsubscribe);
				feedScroll.addView(currFeed);
			}
		}
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
	
	public void setAtom()
	{
		rss=false;
		atom=true;
	}
	
	public void setRSS()
	{
		atom = false;
		rss=true;
	}
	
	private void refresh()
	{
		loadFeeds();
		listFeeds();
	}
	
	public void viewFeed(Feed feed)
	{
		Log.d("FEEDS", "Yes, the feed will be launched");
		//TODO: implement this. Might need to add a new activity.
	}
}
