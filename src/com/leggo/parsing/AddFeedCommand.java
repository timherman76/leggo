package com.leggo.parsing;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.util.Log;

public class AddFeedCommand extends SimplectaCommand {

	public AddFeedCommand(String passPath) {
		super(passPath);
		path = passPath;

	}

	@Override
	public Object parseData(String cookie) throws IOException {
		Document doc = Jsoup.connect(getRootURL() + getPath()).timeout(300000)
				.cookie("ACSID", cookie).post();
		Log.d("COOKIE", "Cookie:" + cookie);
		Log.d("HTML", "HTML:" + doc);
		return parseData(doc);
	}
	
	protected Object parseData(Document doc) {
		if(doc.text() == "internal server error: Get test.com: API error 1 (urlfetch: INVALID_URL)")
			return false;
		else
			return true;
	}

}
