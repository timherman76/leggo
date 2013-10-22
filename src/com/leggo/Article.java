package com.leggo;

public class Article {
	
	protected String URL;
	protected boolean isRead;
	protected String key;
	protected String title;
	protected Feed feed;
	
	public Article(){
		
	}
	
	public Article(String url, 
					boolean isRead,
					String key,
					String title,
					Feed feed){
		
		this.setURL(url);
		this.setKey(key);
		this.setFeed(feed);
		this.setTitle(title);
		this.setRead(isRead);
	}

	public String getURL() {
		return URL;
	}

	public void setURL(String url) {
		URL = url;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public boolean isRead() {
		return isRead;
	}

	public void setRead(boolean isRead) {
		this.isRead = isRead;
	}

	public Feed getFeed() {
		return feed;
	}

	public void setFeed(Feed feed) {
		this.feed = feed;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
		
}
