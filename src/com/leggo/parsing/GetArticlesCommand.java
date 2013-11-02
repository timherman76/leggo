package com.leggo.parsing;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.util.Log;

import com.leggo.Article;
import com.leggo.ArticleMap;
import com.leggo.Feed;
import com.leggo.FeedMap;

public class GetArticlesCommand extends SimplectaCommand {

	protected String path;

	public GetArticlesCommand() {
		super("all/");
	}


	/**
	 * returns a List<Article> object containing a list of articles obtained from the Web page  
	 * @throws IOException 
	 *
	 */
	@Override
	public Object parseData(String cookie) throws IOException {
		Document doc = Jsoup.connect(getRootURL() + getPath())
				.timeout(300000)
				.cookie("ACSID",cookie)
				.post();
		Log.d("COOKIE", "Cookie:" + cookie);
		Log.d("HTML", "HTML:" + doc);
		return parseData(doc);
	}

	public Object parseData(Document doc) {
		List<Article> result = new ArrayList<Article>();

		/*
		 example HTML:
		<div class="item">
			<a class="feedlink" href="http://www.simplecta.com/feed/?http%3a%2f%2fwww.reddit.com%2f.rss">reddit: the front page of the internet</a>
			<div class="item_links">
				<a class="read_link" href="http://www.simplecta.com/read/?key=agtzfnNpbXBsZWN0YXK5AgsSCHVzZXJSb290Igh1c2VyUm9vdAwLEgR1c2VyIhUxMDk3OTUyOTc3MDAxNDU3NzU4NjIMCxIOc3Vic2NyaWJlZEl0ZW0i7wFhZ3R6Zm5OcGJYQnNaV04wWVhLakFRc1NDR1psWldSU2IyOTBJZ2htWldWa1VtOXZkQXdMRWdSbVpXVmtJaHBvZEhSd09pOHZkM2QzTG5KbFpHUnBkQzVqYjIwdkxuSnpjd3dMRWdScGRHVnRJbDlvZEhSd09pOHZkM2QzTG5KbFpHUnBkQzVqYjIwdmNpOTNiM0pzWkc1bGQzTXZZMjl0YldWdWRITXZNVzk0TUc4MEwzUm9aVjl6Wlc1aGRHVmZhWE5mZVdWMFgyRm5ZV2x1WDNSeWVXbHVaMTkwYjE5d1lYTnpYMk5wYzNCaEx3dww&link=http%3a%2f%2fwww.reddit.com%2fr%2fworldnews%2fcomments%2f1ox0o4%2fthe_senate_is_yet_again_trying_to_pass_cispa%2f">The Senate is yet again trying to pass CISPA.</a> 
				<a class="peek" href="http://www.reddit.com/r/worldnews/comments/1ox0o4/the_senate_is_yet_again_trying_to_pass_cispa/">(peek)</a> 
				<button class="ajax_link" data-mark="read" data-key="agtzfnNpbXBsZWN0YXK5AgsSCHVzZXJSb290Igh1c2VyUm9vdAwLEgR1c2VyIhUxMDk3OTUyOTc3MDAxNDU3NzU4NjIMCxIOc3Vic2NyaWJlZEl0ZW0i7wFhZ3R6Zm5OcGJYQnNaV04wWVhLakFRc1NDR1psWldSU2IyOTBJZ2htWldWa1VtOXZkQXdMRWdSbVpXVmtJaHBvZEhSd09pOHZkM2QzTG5KbFpHUnBkQzVqYjIwdkxuSnpjd3dMRWdScGRHVnRJbDlvZEhSd09pOHZkM2QzTG5KbFpHUnBkQzVqYjIwdmNpOTNiM0pzWkc1bGQzTXZZMjl0YldWdWRITXZNVzk0TUc4MEwzUm9aVjl6Wlc1aGRHVmZhWE5mZVdWMFgyRm5ZV2x1WDNSeWVXbHVaMTkwYjE5d1lYTnpYMk5wYzNCaEx3dww">mark read</button>
			</div>
		</div>
		 */

		Elements itemElements = doc.select("div.item");
		for (Element itemElem : itemElements) {

			Element readElem = itemElem.select("a.read_link").first();
			String title = readElem.text();

			Element peekElem = itemElem.select("a.peek").first();
			String url = peekElem.attr("href");

			Element readButtonElem = itemElem.select("button.ajax_link").first();
			String key = readButtonElem.attr("data-key");
			String data_mark = readButtonElem.attr("data-mark");
			boolean isRead = !data_mark.equalsIgnoreCase("read");

			Element feedElem = itemElem.select("a.feedlink").first();
			String feedURL = feedElem.attr("href");
			feedURL = feedURL.substring(feedURL.indexOf("?") + 1);
			String feedName = feedElem.text();

			Article article = new Article();
			article.setTitle(title);
			article.setKey(key);
			article.setURL(url);
			article.setRead(isRead);

			ArticleMap.getInstance().put(article.getURL(), article);

			Feed feed = null;
			//do we already have feed?
			if (FeedMap.getInstance().containsKey(feedURL)) {
				feed = FeedMap.getInstance().get(feedURL);
			} else {
				//create feed and add to map
				feed = new Feed();
				feed.setName(feedName);
				feed.setURL(feedURL);
				FeedMap.getInstance().put(feed.getURL(), feed);
			}

			article.setFeed(feed);
			result.add(article);
		}

		return result;
	}

	public Object testFromFile(String filePath) throws IOException {
		Object result = null;
		File f = new File(filePath);
		Document doc = Jsoup.parse(f, null);
		result = parseData(doc);
		return result;
	}

}
