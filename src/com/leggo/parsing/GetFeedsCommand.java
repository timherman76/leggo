package com.leggo.parsing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.leggo.Feed;

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
	@Override
	public Object parseData() throws IOException {
		List<Feed> result = new ArrayList<Feed>();
		
		Document doc = getConnection().get();
		Elements feedElements = doc.select("a.largefeedlink");
		for (Element feedElem : feedElements){
			Feed feed = new Feed();
			feed.setName(feedElem.val());
			feed.setURL(feedElem.attr("href"));
			
			//get key from unsubscribe link
			Element feedUnsubscribeLink = doc.select("a.peek > a[href]=" + feed.getURL()).first();
			String unsubURL = feedUnsubscribeLink.val();
			int queryStart = unsubURL.indexOf("?") + 1;
			String feedKey = unsubURL.substring(queryStart);
			feed.setKey(feedKey);
			result.add(feed);
		}
		
		return result;
	}

}
