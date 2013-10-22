package com.leggo;

import java.util.Hashtable;

/**
 * 
 * @author Tim
 * Map of feeds, keyed by the URL
 *
 */
public class FeedMap extends Hashtable<String, Feed>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 787237571769559392L;
	protected static FeedMap instance;
	
	protected FeedMap()
	{
	}
	
	public static synchronized FeedMap getInstance(){
		if (instance == null){
			instance = new FeedMap();
		}
		return instance;
	}

}
