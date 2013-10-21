package com.leggo.parsing;

import java.io.IOException;

import org.jsoup.*;

import javax.xml.*;

public interface WebCommand {

	String getRootURL();
	String getPath();
	Connection getConnection();
	Object parseData() throws IOException;
	
}
