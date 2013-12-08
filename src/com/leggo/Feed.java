package com.leggo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Feed {

	protected String URL;
	protected String Name;
	protected String Key;
	protected FeedType Type;
	protected boolean isAdded;
	
	public Feed(){
		
	}

	public Feed(String url,
				String name,
				String key,
				FeedType type, boolean isAdded){
		this.setURL(url);
		this.setName(name);
		this.setKey(key);
		this.setType(type);
		this.setAdded(isAdded);
	}
	
	public Feed(boolean isAdded)
	{
		this.isAdded = isAdded;
	}

	public String getURL() {
		return URL;
	}
	
	public void setAdded(boolean isAdded){
		this.isAdded = isAdded;
	}
	
	public boolean isAdded(){
		return this.isAdded;
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
	
	public static List<Feed> search(String searchExpr, List<Feed> feeds){
		int relevance = -1;
		searchExpr = searchExpr.toLowerCase();
		
		List<FeedSearchResult> searchResults = new ArrayList<FeedSearchResult>();
		
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
				searchResults.add(new FeedSearchResult(f, relevance));
			}
		}
		Collections.sort(searchResults);
		
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
