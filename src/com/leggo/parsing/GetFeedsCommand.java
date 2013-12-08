package com.leggo.parsing;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.util.Log;

import com.leggo.Feed;
import com.leggo.FeedMap;

public class GetFeedsCommand extends SimplectaCommand {

	protected String path;
	
	public GetFeedsCommand(){
		super("feeds/");
	}
	
	/**
	 * returns a List<Feed> object containing a list of feeds obtained from the Web page  
	 * @throws IOException 
	 *
	 */
	public Object parseData(String cookie) throws IOException {
		Document doc = Jsoup.connect(getRootURL() + getPath())
				.timeout(300000)
				.cookie("ACSID",cookie)
				.post()
				;
		Log.d("COOKIE", "Cookie:" + cookie);
		Log.d("HTML", "HTML:" + doc);
		return parseData(doc);
	}
	
	/**
	 * returns a List<Feed> object containing a list of feeds obtained from the Web page  
	 * @throws IOException 
	 *
	 */
	public Object parseData(Document doc) throws IOException {
		List<Feed> result = new ArrayList<Feed>();
		
		Elements feedElements = doc.select("a.largefeedlink");
		for (Element feedElem : feedElements){
			Feed feed = new Feed(true); //setting isAdded
			feed.setName(feedElem.text());
			String rawfeedURL = feedElem.attr("href");
			String feedURL = rawfeedURL.substring(rawfeedURL.indexOf("?")+1);
			feed.setURL(feedURL);
			
			//get key from unsubscribe link
			Element feedUnsubscribeLink = doc.select("a[href$=" + feedURL+"] ~ a.peek").first();
			String unsubURL = feedUnsubscribeLink.attr("href");
			int queryStart = unsubURL.indexOf("?") + 1;
			String feedKey = unsubURL.substring(queryStart);
			feed.setKey(feedKey);
			feed.setAdded(true);
			result.add(feed);
			
			//store the feed in our map
			FeedMap.getInstance().put(feed.getURL(), feed);
		}
		
		return result;
	}
	
	
	public Object testFromFile(String filePath) throws IOException{
		Object result = null;
		File f = new File(filePath);
		Document doc = Jsoup.parse(f, null);
		result = parseData(doc);
		return result;
	}
}
