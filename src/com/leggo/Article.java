package com.leggo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
		

	
	public List<ArticleSearchResult> search(String searchExpr, List<Article> articles){
		List<ArticleSearchResult> result = new ArrayList<ArticleSearchResult>();
		int relevance = -1;
		searchExpr = searchExpr.toLowerCase();
		
		for(Article a : articles){
			String aName = a.getTitle().toLowerCase();
			String aURL = a.getURL().toLowerCase();
		
			if ( searchExpr.equals(aName) || searchExpr.equals(aURL) ){
				relevance = 0;
			}
			else{
				int nameIdx = aName.indexOf(searchExpr);
				if(nameIdx >= 0){
					relevance = 100 + nameIdx;
				}else {
					int urlIdx = aURL.indexOf(searchExpr);
					if(urlIdx >= 0){
						relevance = 200 + urlIdx;
					}
				}
			}
			
			if ( relevance >= 0 ){
				result.add(new ArticleSearchResult(a, relevance));
			}
			
			Collections.sort(result);
		}
		
		return result;
	}
	
	public class ArticleSearchResult implements Comparable<ArticleSearchResult>{
		
		public Article article;
		public int relevance;
		
		public ArticleSearchResult(Article article, int relevance){
			this.article = article;
			this.relevance = relevance;
		}
		
		@Override
		public int compareTo(ArticleSearchResult arg0) {
			int result = 0;
			if (arg0 == null || arg0.relevance < this.relevance){
				result = -1;
			}else if ( arg0.relevance > this.relevance){
				result = 1;
			}else{
				result = this.article.getTitle().compareTo(arg0.article.getTitle());
			}
			return result;
		}
	}
	
}