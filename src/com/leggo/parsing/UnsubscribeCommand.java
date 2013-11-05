package com.leggo.parsing;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class UnsubscribeCommand extends SimplectaCommand {

	@SuppressWarnings("unused")
	private String path;
	
	public UnsubscribeCommand(String passPath){
		super(passPath);
	}
	@Override
	public Object parseData(String cookie) throws IOException {
		Document doc = Jsoup.connect(getRootURL() + getPath()).timeout(300000)
					.cookie("ACSID", cookie).post();
		if(doc != null)
			return true;
		return false;
	}
}
