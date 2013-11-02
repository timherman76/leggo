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
		
	@Override
	public boolean equals(Object obj){
		boolean result = false;
		if ( obj != null && obj instanceof Article){
			Article a = (Article) obj;
			result = this.getURL().equalsIgnoreCase(a.getURL());
		}
		return result;
	}
	
	@Override
	public String toString()
	{
		String result = "Title: "  + this.title;
		result += "\nURL: " + this.URL;
		return result;
	}
	
	public static List<ArticleSearchResult> search(String searchExpr, List<Article> articles){
		List<ArticleSearchResult> result = new ArrayList<ArticleSearchResult>();
		int relevance = -1;
		searchExpr = searchExpr.toLowerCase();
		
		for(Article a : articles){
			String aName = a.getTitle().toLowerCase();
			String aURL = a.getURL().toLowerCase();
			relevance = -1;
			
			if ( searchExpr.equals(aName) || searchExpr.equals(aURL) ){
				relevance = 0;
			}
			else{
				int nameIdx = aName.indexOf(searchExpr);
				if(nameIdx >= 0){
					relevance = 1000 + nameIdx;
				}else {
					int urlIdx = aURL.indexOf(searchExpr);
					if(urlIdx >= 0){
						relevance = 2000 + urlIdx;
					}
				}
			}
			
			if ( relevance >= 0 ){
				result.add(new ArticleSearchResult(a, relevance));
			}
		}
		
		Collections.sort(result);
		
		return result;
	}
	
	public static List<Article> GetArticles(List<ArticleSearchResult> searchResults){
		int numResults = searchResults != null ? searchResults.size() : 0;
		List<Article> result = new ArrayList<Article>(numResults);
		for (int i=0; i < numResults; i++){
			result.add(searchResults.get(i).article);
		}
		
		return result;
	}
	
	public static List<Article> GetArticlesByFeed(Feed feed, List<Article> articles){
		List<Article> result = new ArrayList<Article>();
		for(Article a : articles){
			if ( a.getFeed() != null && a.getFeed().equals(feed)){
				result.add(a);
			}
		}
		
		return result;
	}
	
	public static class ArticleSearchResult implements Comparable<ArticleSearchResult>{
		
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
		
		@Override
		public String toString()
		{
			String result = this.article.toString();
			result += "\nRelevance: " + this.relevance;
			return result;
		}
	}
	
	
	
	
}
