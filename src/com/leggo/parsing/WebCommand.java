package com.leggo.parsing;

import java.io.IOException;

import org.jsoup.Connection;

public interface WebCommand {

        String getRootURL();
        String getPath();
        Connection getConnection();
        Object parseData() throws IOException;
        
}