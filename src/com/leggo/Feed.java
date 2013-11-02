package com.leggo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.leggo.Article.ArticleSearchResult;

public class Feed {

	protected String URL;
	protected String Name;
	protected String Key;
	protected FeedType Type;
	
	public Feed(){
		
	}

	public Feed(String url,
				String name,
				String key,
				FeedType type){
		this.setURL(url);
		this.setName(name);
		this.setKey(key);
		this.setType(type);
	}

	public String getURL() {
		return URL;
	}

	public void setURL(String url) {
		URL = url;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getKey() {
		return Key;
	}

	public void setKey(String key) {
		Key = key;
	}

	public FeedType getType() {
		return Type;
	}

	public void setType(FeedType type) {
		Type = type;
	}
	
	public static List<FeedSearchResult> search(String searchExpr, List<Feed> feeds){
		List<FeedSearchResult> result = new ArrayList<FeedSearchResult>();
		int relevance = -1;
		searchExpr = searchExpr.toLowerCase();
		
		for(Feed f : feeds){
			String fName = f.getName().toLowerCase();
			String fURL = f.getURL().toLowerCase();
			relevance = -1;
			
			if ( searchExpr.equals(fName) || searchExpr.equals(fURL) ){
				relevance = 0;
			}
			else{
				int nameIdx = fName.indexOf(searchExpr);
				if(nameIdx >= 0){
					relevance = 1000 + nameIdx;
				}else {
					int urlIdx = fURL.indexOf(searchExpr);
					if(urlIdx >= 0){
						relevance = 2000 + urlIdx;
					}
				}
			}
			
			if ( relevance >= 0 ){
				result.add(new FeedSearchResult(f, relevance));
			}
		}
		Collections.sort(result);
		
		return result;
	}
	
	public static List<Feed> GetFeeds(List<FeedSearchResult> searchResults){
		int numResults = searchResults != null ? searchResults.size() : 0;
		List<Feed> result = new ArrayList<Feed>(numResults);
		for (int i=0; i < numResults; i++){
			result.add(searchResults.get(i).feed);
		}
		
		return result;
	}
	
	@Override
	public boolean equals(Object obj){
		boolean result = false;
		if ( obj != null && obj instanceof Feed){
			Feed a = (Feed) obj;
			result = this.getURL().equalsIgnoreCase(a.getURL());
		}
		return result;
	}
	
	public static class FeedSearchResult implements Comparable<FeedSearchResult>{
		
		public Feed feed;
		public int relevance;
		
		public FeedSearchResult(Feed feed, int relevance){
			this.feed = feed;
			this.relevance = relevance;
		}
		
		@Override
		public int compareTo(FeedSearchResult arg0) {
			int result = 0;
			if (arg0 == null || arg0.relevance < this.relevance){
				result = -1;
			}else if ( arg0.relevance > this.relevance){
				result = 1;
			}else{
				result = this.feed.getName().compareTo(arg0.feed.getName());
			}
			return result;
		}
	}
	
	public static enum FeedType{
		RSS, ATOM
	}
}
