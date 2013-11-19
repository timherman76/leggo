package com.leggo.parsing;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.util.Log;

public class MarkReadCommand extends SimplectaCommand {
	private String path;
	public MarkReadCommand(String key) {
		super("markRead/?" + key);
	}

	@Override
	public Object parseData(String cookie) throws IOException {
		Document doc = Jsoup.connect(getRootURL() + getPath()).timeout(300000)
				.cookie("ACSID", cookie).post();
		Log.d("COOKIE", "Cookie:" + cookie);
		Log.d("HTML", "HTML:" + doc);
		return true;
	}

}
