package com.leggo.parsing;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.leggo.Feed;

public class FeedSearchCommand extends RssSearchHubCommand {

	protected String path;
	
	public FeedSearchCommand(){
		super("feeds/");
	}
	
	/**
	 * returns a List<Feed> object containing a list of feeds obtained from the Web page  
	 * @throws IOException 
	 *
	 */
	public Object parseData(String searchTerm) throws IOException {
		String queryString = "?search=" + URLEncoder.encode(searchTerm, "UTF-8") + "&action=Search+Feeds";
		Document doc = Jsoup.connect(getRootURL() + getPath() + queryString)
				.timeout(300000)
				.get();
		
		return parseData(doc);
	}
	
	/**
	 * returns a List<Feed> object containing a list of feeds obtained from the Web page  
	 * @throws IOException 
	 *
	 */
	public Object parseData(Document doc) throws IOException {
		List<Feed> result = new ArrayList<Feed>();
		
		Elements feedElements = doc.select("a.feed_url");
		for (Element feedElem : feedElements){
			Feed feed = new Feed();
			String name = feedElem.attr("title");
			name = URLDecoder.decode(name, "UTF-8");
			feed.setName(feedElem.text());
			String feedURL = feedElem.attr("href");
			feed.setURL(feedURL);
			result.add(feed);
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
