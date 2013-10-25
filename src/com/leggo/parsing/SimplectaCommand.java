package com.leggo.parsing;

import java.io.IOException;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

public abstract class SimplectaCommand implements WebCommand {

	protected String rootURL = "http://www.simplecta.com/";
	protected String path;

	public SimplectaCommand(String path) {
		this.path = path;
	}

	@Override
	public String getRootURL() {
		return rootURL;
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public Connection getConnection() {
		Connection conn = Jsoup.connect(getRootURL() + getPath());
		return conn;
	}

	/**
	 *
	 */
	@Override
	public abstract Object parseData(String cookie) throws IOException;

}
