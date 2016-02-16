package com.clj.jaf.http.core;

import org.apache.http.params.HttpParams;

import java.net.URI;
import java.net.URISyntaxException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpHost;
import cz.msebera.android.httpclient.HttpRequest;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.ProtocolException;
import cz.msebera.android.httpclient.client.CircularRedirectException;
import cz.msebera.android.httpclient.client.utils.URIUtils;
import cz.msebera.android.httpclient.impl.client.DefaultRedirectHandler;
import cz.msebera.android.httpclient.impl.client.RedirectLocations;
import cz.msebera.android.httpclient.protocol.HttpContext;

public class MyRedirectHandler extends DefaultRedirectHandler {
    private static final String REDIRECT_LOCATIONS = "http.protocol.redirect-locations";
    private final boolean mEnableRedirects;

    public MyRedirectHandler(boolean allowRedirects) {
        this.mEnableRedirects = allowRedirects;
    }

    public boolean isRedirectRequested(HttpResponse response, HttpContext context) {
        if (!this.mEnableRedirects) {
            return false;
        } else if (response == null) {
            throw new IllegalArgumentException("HTTP response may not be null");
        } else {
            int statusCode = response.getStatusLine().getStatusCode();
            switch (statusCode) {
                case 301:
                case 302:
                case 303:
                case 307:
                    return true;
                case 304:
                case 305:
                case 306:
                default:
                    return false;
            }
        }
    }

    public URI getLocationURI(HttpResponse response, HttpContext context) throws ProtocolException {
        if (response == null) {
            throw new IllegalArgumentException("HTTP response may not be null");
        } else {
            Header locationHeader = response.getFirstHeader("location");
            if (locationHeader == null) {
                throw new ProtocolException("Received redirect response " + response.getStatusLine() + " but no location header");
            } else {
                String location = locationHeader.getValue().replaceAll(" ", "%20");

                URI uri;
                try {
                    uri = new URI(location);
                } catch (URISyntaxException var13) {
                    throw new ProtocolException("Invalid redirect URI: " + location, var13);
                }

                HttpParams params = (HttpParams) response.getParams();
                if (!uri.isAbsolute()) {
                    if (params.isParameterTrue("http.protocol.reject-relative-redirect")) {
                        throw new ProtocolException("Relative redirect location \'" + uri + "\' not allowed");
                    }

                    HttpHost redirectLocations = (HttpHost) context.getAttribute("http.target_host");
                    if (redirectLocations == null) {
                        throw new IllegalStateException("Target host not available in the HTTP context");
                    }

                    HttpRequest redirectURI = (HttpRequest) context.getAttribute("http.request");

                    try {
                        URI ex = new URI(redirectURI.getRequestLine().getUri());
                        URI absoluteRequestURI = URIUtils.rewriteURI(ex, redirectLocations, true);
                        uri = URIUtils.resolve(absoluteRequestURI, uri);
                    } catch (URISyntaxException var12) {
                        throw new ProtocolException(var12.getMessage(), var12);
                    }
                }

                if (params.isParameterFalse("http.protocol.allow-circular-redirects")) {
                    RedirectLocations redirectLocations1 = (RedirectLocations) context.getAttribute("http.protocol.redirect-locations");
                    if (redirectLocations1 == null) {
                        redirectLocations1 = new RedirectLocations();
                        context.setAttribute("http.protocol.redirect-locations", redirectLocations1);
                    }

                    URI redirectURI1;
                    if (uri.getFragment() != null) {
                        try {
                            HttpHost ex1 = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
                            redirectURI1 = URIUtils.rewriteURI(uri, ex1, true);
                        } catch (URISyntaxException var11) {
                            throw new ProtocolException(var11.getMessage(), var11);
                        }
                    } else {
                        redirectURI1 = uri;
                    }

                    if (redirectLocations1.contains(redirectURI1)) {
                        throw new CircularRedirectException("Circular redirect to \'" + redirectURI1 + "\'");
                    }

                    redirectLocations1.add(redirectURI1);
                }

                return uri;
            }
        }
    }
}
