package com.leggo;

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
	
	
	public static enum FeedType{
		RSS, ATOM
	}
}
