package com.leggo;

import java.util.ArrayList;
import java.util.List;

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
	
	public List<FeedSearchResult> search(String searchExpr, List<Feed> feeds){
		List<FeedSearchResult> result = new ArrayList<FeedSearchResult>();
		int relevance = -1;
		searchExpr = searchExpr.toLowerCase();
		
		for(Feed f : feeds){
			String fName = f.Name.toLowerCase();
			String fURL = f.URL.toLowerCase();
		
			if ( searchExpr.equals(fName) || searchExpr.equals(fURL) ){
				relevance = 0;
			}
			else{
				int nameIdx = fName.indexOf(searchExpr);
				if(nameIdx >= 0){
					relevance = 100 + nameIdx;
				}else {
					int urlIdx = fURL.indexOf(searchExpr);
					if(urlIdx >= 0){
						relevance = 200 + urlIdx;
					}
				}
			}
			
			if ( relevance >= 0 ){
				result.add(new FeedSearchResult(f, relevance));
			}
		}
		
		return result;
	}
	
	public class FeedSearchResult{
		public Feed feed;
		public int relevance;
		
		public FeedSearchResult(Feed feed, int relevance){
			this.feed = feed;
			this.relevance = relevance;
		}
	}
	
	public static enum FeedType{
		RSS, ATOM
	}
}
