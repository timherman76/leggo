package com.leggo;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class Feed {
	private String feedLink = null;
	private String feedName = null;
	private String unsubLink = null;
	
	public Feed(String newFeedLink,String newFeedName,String newUnsubLink){
		feedLink = newFeedLink;
		feedName = newFeedName;
		unsubLink = newUnsubLink;
	}
	
	public Feed(){
		//default
	}
	
	public String getFeedLink(){
		return feedLink;
	}
	
	public String getFeedName(){
		return feedName;
	}
	
	public void unsubscribe()
	{
		
		Log.d("FEEDS","Not yet testable due to login.");
		return;
		/*String url = "http://simplecta.com" + unsubLink;
		HttpGet get = new HttpGet(url);
		HttpClient conn = new DefaultHttpClient();
		try {
			conn.execute(get);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}*/
		
		
	}
}
