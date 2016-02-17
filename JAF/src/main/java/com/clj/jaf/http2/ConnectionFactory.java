package com.clj.jaf.http2;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;

public interface ConnectionFactory {

    HttpURLConnection create(URL url) throws IOException;

    HttpURLConnection create(URL url, Proxy proxy) throws IOException;
}
