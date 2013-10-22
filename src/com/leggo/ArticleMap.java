package com.leggo;

import java.util.Hashtable;

/**
 * 
 * @author Tim
 * Map of articles, keyed by the URL
 *
 */
public class ArticleMap extends Hashtable<String, Article>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1742274524544920429L;
	protected static ArticleMap instance;
	
	protected ArticleMap()
	{
	}
	
	public static synchronized ArticleMap getInstance(){
		if (instance == null){
			instance = new ArticleMap();
		}
		return instance;
	}

}
