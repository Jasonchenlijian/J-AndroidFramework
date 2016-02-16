package com.clj.jaf.http;

import android.content.Context;

import com.clj.jaf.http.core.AsyncHttpRequest;
import com.clj.jaf.http.core.RequestHandle;
import com.clj.jaf.http.core.ResponseHandlerInterface;

import cz.msebera.android.httpclient.client.methods.HttpUriRequest;
import cz.msebera.android.httpclient.conn.scheme.SchemeRegistry;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.protocol.HttpContext;

public class JSyncHttpClient extends JAsyncHttpClient {
    public JSyncHttpClient() {
        super(false, 80, 443);
    }

    public JSyncHttpClient(int httpPort) {
        super(false, httpPort, 443);
    }

    public JSyncHttpClient(int httpPort, int httpsPort) {
        super(false, httpPort, httpsPort);
    }

    public JSyncHttpClient(boolean fixNoHttpResponseException, int httpPort, int httpsPort) {
        super(fixNoHttpResponseException, httpPort, httpsPort);
    }

    public JSyncHttpClient(SchemeRegistry schemeRegistry) {
        super(schemeRegistry);
    }

    protected RequestHandle sendRequest(DefaultHttpClient client, HttpContext httpContext,
                                        HttpUriRequest uriRequest, String contentType,
                                        ResponseHandlerInterface responseHandler,
                                        Context context) {
        if (contentType != null) {
            uriRequest.addHeader("Content-Type", contentType);
        }

        responseHandler.setUseSynchronousMode(true);
        (new AsyncHttpRequest(client, httpContext, uriRequest, responseHandler)).run();
        return new RequestHandle(null);
    }
}
