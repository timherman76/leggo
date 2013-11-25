package com.leggo;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

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
	
	public void putList(List<Feed> feeds){
		putList(feeds, true);
	}
	
	public void putList(List<Feed> feeds, boolean clear){
		if (clear){
			this.clear();
		}
		for(Feed f : feeds){
			this.put(f.getURL(), f);
		}
	}
	
	public List<Feed> toList(){
		List<Feed> result = new ArrayList<Feed>(this.values());
		return result;
	}
	
	

}
