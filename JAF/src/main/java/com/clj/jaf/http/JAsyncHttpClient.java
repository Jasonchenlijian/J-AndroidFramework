package com.clj.jaf.http;

import android.content.Context;
import android.os.Looper;
import android.util.Log;

import com.clj.jaf.http.core.AsyncHttpRequest;
import com.clj.jaf.http.core.MyRedirectHandler;
import com.clj.jaf.http.core.MySSLSocketFactory;
import com.clj.jaf.http.core.PreemtiveAuthorizationHttpRequestInterceptor;
import com.clj.jaf.http.core.RangeFileAsyncHttpResponseHandler;
import com.clj.jaf.http.core.RequestHandle;
import com.clj.jaf.http.core.RequestParams;
import com.clj.jaf.http.core.ResponseHandlerInterface;
import com.clj.jaf.http.core.RetryHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.GZIPInputStream;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HeaderElement;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpException;
import cz.msebera.android.httpclient.HttpHost;
import cz.msebera.android.httpclient.HttpRequest;
import cz.msebera.android.httpclient.HttpRequestInterceptor;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.HttpResponseInterceptor;
import cz.msebera.android.httpclient.HttpVersion;
import cz.msebera.android.httpclient.auth.AuthScope;
import cz.msebera.android.httpclient.auth.AuthState;
import cz.msebera.android.httpclient.auth.Credentials;
import cz.msebera.android.httpclient.auth.UsernamePasswordCredentials;
import cz.msebera.android.httpclient.client.CookieStore;
import cz.msebera.android.httpclient.client.CredentialsProvider;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.RedirectHandler;
import cz.msebera.android.httpclient.client.methods.HttpDelete;
import cz.msebera.android.httpclient.client.methods.HttpEntityEnclosingRequestBase;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpHead;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.client.methods.HttpPut;
import cz.msebera.android.httpclient.client.methods.HttpUriRequest;
import cz.msebera.android.httpclient.conn.params.ConnManagerParams;
import cz.msebera.android.httpclient.conn.params.ConnPerRouteBean;
import cz.msebera.android.httpclient.conn.scheme.PlainSocketFactory;
import cz.msebera.android.httpclient.conn.scheme.Scheme;
import cz.msebera.android.httpclient.conn.scheme.SchemeRegistry;
import cz.msebera.android.httpclient.conn.ssl.SSLSocketFactory;
import cz.msebera.android.httpclient.entity.HttpEntityWrapper;
import cz.msebera.android.httpclient.impl.auth.BasicScheme;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.impl.conn.tsccm.ThreadSafeClientConnManager;
import cz.msebera.android.httpclient.params.BasicHttpParams;
import cz.msebera.android.httpclient.params.HttpConnectionParams;
import cz.msebera.android.httpclient.params.HttpParams;
import cz.msebera.android.httpclient.params.HttpProtocolParams;
import cz.msebera.android.httpclient.protocol.BasicHttpContext;
import cz.msebera.android.httpclient.protocol.HttpContext;
import cz.msebera.android.httpclient.protocol.SyncBasicHttpContext;

public class JAsyncHttpClient {
    public static final String TAG = JAsyncHttpClient.class.getSimpleName();
    public static final int DEFAULT_MAX_CONNECTIONS = 10;
    public static final int DEFAULT_SOCKET_TIMEOUT = 10000;
    public static final int DEFAULT_MAX_RETRIES = 5;
    public static final int DEFAULT_RETRY_SLEEP_TIME_MILLIS = 1500;
    public static final int DEFAULT_SOCKET_BUFFER_SIZE = 8192;
    public static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
    public static final String ENCODING_GZIP = "gzip";
    private int mMaxConnections;
    private int mTimeout;
    private final DefaultHttpClient mHttpClient;
    private final HttpContext mHttpContext;
    private ExecutorService mThreadPool;
    private final Map<Context, List<RequestHandle>> mRequestMap;
    private final Map<String, String> mClientHeaderMap;
    private boolean isUrlEncodingEnabled;

    public JAsyncHttpClient() {
        this(false, 80, 443);
    }

    public JAsyncHttpClient(int httpPort) {
        this(false, httpPort, 443);
    }

    public JAsyncHttpClient(int httpPort, int httpsPort) {
        this(false, httpPort, httpsPort);
    }

    public JAsyncHttpClient(boolean fixNoHttpResponseException, int httpPort, int httpsPort) {
        this(getDefaultSchemeRegistry(fixNoHttpResponseException, httpPort, httpsPort));
    }

    private static SchemeRegistry getDefaultSchemeRegistry(boolean fixNoHttpResponseException, int httpPort, int httpsPort) {
        if (fixNoHttpResponseException) {
            Log.i(TAG, "Beware! Using the fix is insecure, as it doesn\'t verify SSL certificates.");
        }

        if (httpPort < 1) {
            httpPort = 80;
            Log.i(TAG, "Invalid HTTP port number specified, defaulting to 80");
        }

        if (httpsPort < 1) {
            httpsPort = 443;
            Log.i(TAG, "Invalid HTTPS port number specified, defaulting to 443");
        }

        SSLSocketFactory sslSocketFactory;
        if (fixNoHttpResponseException) {
            sslSocketFactory = MySSLSocketFactory.getFixedSocketFactory();
        } else {
            sslSocketFactory = SSLSocketFactory.getSocketFactory();
        }

        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), httpPort));
        schemeRegistry.register(new Scheme("https", sslSocketFactory, httpsPort));
        return schemeRegistry;
    }

    public JAsyncHttpClient(SchemeRegistry schemeRegistry) {
        this.mMaxConnections = 10;
        this.mTimeout = 10000;
        this.isUrlEncodingEnabled = true;
        BasicHttpParams httpParams = new BasicHttpParams();
        ConnManagerParams.setTimeout(httpParams, (long) this.mTimeout);
        ConnManagerParams.setMaxConnectionsPerRoute(httpParams, new ConnPerRouteBean(this.mMaxConnections));
        ConnManagerParams.setMaxTotalConnections(httpParams, 10);
        HttpConnectionParams.setSoTimeout(httpParams, this.mTimeout);
        HttpConnectionParams.setConnectionTimeout(httpParams, this.mTimeout);
        HttpConnectionParams.setTcpNoDelay(httpParams, true);
        HttpConnectionParams.setSocketBufferSize(httpParams, 8192);
        HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
        ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(httpParams, schemeRegistry);
        this.mThreadPool = this.getDefaultThreadPool();
        this.mRequestMap = new WeakHashMap();
        this.mClientHeaderMap = new HashMap();
        this.mHttpContext = new SyncBasicHttpContext(new BasicHttpContext());
        this.mHttpClient = new DefaultHttpClient(cm, httpParams);
        this.mHttpClient.addRequestInterceptor(new HttpRequestInterceptor() {
            public void process(HttpRequest request, HttpContext context) {
                if (!request.containsHeader("Accept-Encoding")) {
                    request.addHeader("Accept-Encoding", "gzip");
                }

                String header;
                for (Iterator var4 = JAsyncHttpClient.this.mClientHeaderMap.keySet().iterator(); var4.hasNext(); request.addHeader(header, (String) JAsyncHttpClient.this.mClientHeaderMap.get(header))) {
                    header = (String) var4.next();
                    if (request.containsHeader(header)) {
                        Header overwritten = request.getFirstHeader(header);
                        Log.i(JAsyncHttpClient.TAG, String.format("Headers were overwritten! (%s | %s) overwrites (%s | %s)", new Object[]{header, JAsyncHttpClient.this.mClientHeaderMap.get(header), overwritten.getName(), overwritten.getValue()}));
                    }
                }

            }
        });
        this.mHttpClient.addResponseInterceptor(new HttpResponseInterceptor() {
            public void process(HttpResponse response, HttpContext context) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    Header encoding = entity.getContentEncoding();
                    if (encoding != null) {
                        HeaderElement[] var8;
                        int var7 = (var8 = encoding.getElements()).length;

                        for (int var6 = 0; var6 < var7; ++var6) {
                            HeaderElement element = var8[var6];
                            if (element.getName().equalsIgnoreCase("gzip")) {
                                response.setEntity(new JAsyncHttpClient.InflatingEntity(entity));
                                break;
                            }
                        }
                    }

                }
            }
        });
        this.mHttpClient.addRequestInterceptor(new HttpRequestInterceptor() {
            public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
                AuthState authState = (AuthState) context.getAttribute("http.auth.target-scope");
                CredentialsProvider credsProvider = (CredentialsProvider) context.getAttribute("http.auth.credentials-provider");
                HttpHost targetHost = (HttpHost) context.getAttribute("http.target_host");
                if (authState.getAuthScheme() == null) {
                    AuthScope authScope = new AuthScope(targetHost.getHostName(), targetHost.getPort());
                    Credentials creds = credsProvider.getCredentials(authScope);
                    if (creds != null) {
                        authState.setAuthScheme(new BasicScheme());
                        authState.setCredentials(creds);
                    }
                }

            }
        }, 0);
        this.mHttpClient.setHttpRequestRetryHandler(new RetryHandler(5, 1500));
    }

    public static void allowRetryExceptionClass(Class<?> cls) {
        if (cls != null) {
            RetryHandler.addClassToWhitelist(cls);
        }
    }

    public static void blockRetryExceptionClass(Class<?> cls) {
        if (cls != null) {
            RetryHandler.addClassToBlacklist(cls);
        }
    }

    public HttpClient getHttpClient() {
        return this.mHttpClient;
    }

    public HttpContext getHttpContext() {
        return this.mHttpContext;
    }

    public void setCookieStore(CookieStore cookieStore) {
        this.mHttpContext.setAttribute("http.cookie-store", cookieStore);
    }

    public void setThreadPool(ExecutorService mThreadPool) {
        this.mThreadPool = mThreadPool;
    }

    public ExecutorService getThreadPool() {
        return this.mThreadPool;
    }

    protected ExecutorService getDefaultThreadPool() {
        return Executors.newCachedThreadPool();
    }

    public void setEnableRedirects(boolean enableRedirects, boolean enableRelativeRedirects, boolean enableCircularRedirects) {
        this.mHttpClient.getParams().setBooleanParameter("http.protocol.reject-relative-redirect", !enableRelativeRedirects);
        this.mHttpClient.getParams().setBooleanParameter("http.protocol.allow-circular-redirects", enableCircularRedirects);
        this.mHttpClient.setRedirectHandler(new MyRedirectHandler(enableRedirects));
    }

    public void setEnableRedirects(boolean enableRedirects, boolean enableRelativeRedirects) {
        this.setEnableRedirects(enableRedirects, enableRelativeRedirects, true);
    }

    public void setEnableRedirects(boolean enableRedirects) {
        this.setEnableRedirects(enableRedirects, enableRedirects, enableRedirects);
    }

    public void setRedirectHandler(RedirectHandler customRedirectHandler) {
        this.mHttpClient.setRedirectHandler(customRedirectHandler);
    }

    public void setUserAgent(String userAgent) {
        HttpProtocolParams.setUserAgent(this.mHttpClient.getParams(), userAgent);
    }

    public int getMaxConnections() {
        return this.mMaxConnections;
    }

    public void setMaxConnections(int maxConnections) {
        if (maxConnections < 1) {
            maxConnections = 10;
        }

        this.mMaxConnections = maxConnections;
        HttpParams httpParams = this.mHttpClient.getParams();
        ConnManagerParams.setMaxConnectionsPerRoute(httpParams, new ConnPerRouteBean(this.mMaxConnections));
    }

    public int getTimeout() {
        return this.mTimeout;
    }

    public void setTimeout(int timeout) {
        if (timeout < 1000) {
            timeout = 10000;
        }

        this.mTimeout = timeout;
        HttpParams httpParams = this.mHttpClient.getParams();
        ConnManagerParams.setTimeout(httpParams, (long) this.mTimeout);
        HttpConnectionParams.setSoTimeout(httpParams, this.mTimeout);
        HttpConnectionParams.setConnectionTimeout(httpParams, this.mTimeout);
    }

    public void setProxy(String hostname, int port) {
        HttpHost proxy = new HttpHost(hostname, port);
        HttpParams httpParams = this.mHttpClient.getParams();
        httpParams.setParameter("http.route.default-proxy", proxy);
    }

    public void setProxy(String hostname, int port, String username, String password) {
        this.mHttpClient.getCredentialsProvider().setCredentials(new AuthScope(hostname, port), new UsernamePasswordCredentials(username, password));
        HttpHost proxy = new HttpHost(hostname, port);
        HttpParams httpParams = this.mHttpClient.getParams();
        httpParams.setParameter("http.route.default-proxy", proxy);
    }

    public void setSSLSocketFactory(SSLSocketFactory sslSocketFactory) {
        this.mHttpClient.getConnectionManager().getSchemeRegistry().register(new Scheme("https", sslSocketFactory, 443));
    }

    public void setMaxRetriesAndTimeout(int retries, int timeout) {
        this.mHttpClient.setHttpRequestRetryHandler(new RetryHandler(retries, timeout));
    }

    public void addHeader(String header, String value) {
        this.mClientHeaderMap.put(header, value);
    }

    public void removeHeader(String header) {
        this.mClientHeaderMap.remove(header);
    }

    public void setBasicAuth(String username, String password) {
        this.setBasicAuth(username, password, false);
    }

    public void setBasicAuth(String username, String password, boolean preemtive) {
        this.setBasicAuth(username, password, null, preemtive);
    }

    public void setBasicAuth(String username, String password, AuthScope scope) {
        this.setBasicAuth(username, password, scope, false);
    }

    public void setBasicAuth(String username, String password, AuthScope scope, boolean preemtive) {
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
        this.mHttpClient.getCredentialsProvider().setCredentials(scope == null ? AuthScope.ANY : scope, credentials);
        this.setAuthenticationPreemptive(preemtive);
    }

    public void setAuthenticationPreemptive(boolean isPreemtive) {
        if (isPreemtive) {
            this.mHttpClient.addRequestInterceptor(new PreemtiveAuthorizationHttpRequestInterceptor(), 0);
        } else {
            this.mHttpClient.removeRequestInterceptorByClass(PreemtiveAuthorizationHttpRequestInterceptor.class);
        }

    }

    public void clearBasicAuth() {
        this.mHttpClient.getCredentialsProvider().clear();
    }

    public void cancelRequests(final Context context, final boolean mayInterruptIfRunning) {
        if (context == null) {
            Log.e(TAG, "Passed null Context to cancelRequests");
        } else {
            Runnable r = new Runnable() {
                public void run() {
                    List requestList = (List) JAsyncHttpClient.this.mRequestMap.get(context);
                    if (requestList != null) {
                        Iterator var3 = requestList.iterator();

                        while (var3.hasNext()) {
                            RequestHandle requestHandle = (RequestHandle) var3.next();
                            requestHandle.cancel(mayInterruptIfRunning);
                        }

                        JAsyncHttpClient.this.mRequestMap.remove(context);
                    }

                }
            };
            if (Looper.myLooper() == Looper.getMainLooper()) {
                (new Thread(r)).start();
            } else {
                r.run();
            }

        }
    }

    public void cancelAllRequests(boolean mayInterruptIfRunning) {
        Iterator var3 = this.mRequestMap.values().iterator();

        while (true) {
            List requestList;
            do {
                if (!var3.hasNext()) {
                    this.mRequestMap.clear();
                    return;
                }

                requestList = (List) var3.next();
            } while (requestList == null);

            Iterator var5 = requestList.iterator();

            while (var5.hasNext()) {
                RequestHandle requestHandle = (RequestHandle) var5.next();
                requestHandle.cancel(mayInterruptIfRunning);
            }
        }
    }

    public RequestHandle head(String url, ResponseHandlerInterface responseHandler) {
        return this.head(null, url, null, responseHandler);
    }

    public RequestHandle head(String url, RequestParams params, ResponseHandlerInterface responseHandler) {
        return this.head(null, url, params, responseHandler);
    }

    public RequestHandle head(Context context, String url, ResponseHandlerInterface responseHandler) {
        return this.head(context, url, null, responseHandler);
    }

    public RequestHandle head(Context context, String url, RequestParams params, ResponseHandlerInterface responseHandler) {
        return this.sendRequest(this.mHttpClient, this.mHttpContext, new HttpHead(getUrlWithQueryString(this.isUrlEncodingEnabled, url, params)), null, responseHandler, context);
    }

    public RequestHandle head(Context context, String url, Header[] headers, RequestParams params, ResponseHandlerInterface responseHandler) {
        HttpHead request = new HttpHead(getUrlWithQueryString(this.isUrlEncodingEnabled, url, params));
        if (headers != null) {
            request.setHeaders(headers);
        }

        return this.sendRequest(this.mHttpClient, this.mHttpContext, request, null, responseHandler, context);
    }

    public RequestHandle get(String url, ResponseHandlerInterface responseHandler) {
        return this.get(null, url, null, responseHandler);
    }

    public RequestHandle get(String url, RequestParams params, ResponseHandlerInterface responseHandler) {
        return this.get(null, url, params, responseHandler);
    }

    public RequestHandle get(Context context, String url, ResponseHandlerInterface responseHandler) {
        return this.get(context, url, null, responseHandler);
    }

    public RequestHandle get(Context context, String url, RequestParams params, ResponseHandlerInterface responseHandler) {
        return this.sendRequest(this.mHttpClient, this.mHttpContext, new HttpGet(getUrlWithQueryString(this.isUrlEncodingEnabled, url, params)), null, responseHandler, context);
    }

    public RequestHandle get(Context context, String url, Header[] headers, RequestParams params, ResponseHandlerInterface responseHandler) {
        HttpGet request = new HttpGet(getUrlWithQueryString(this.isUrlEncodingEnabled, url, params));
        if (headers != null) {
            request.setHeaders(headers);
        }

        return this.sendRequest(this.mHttpClient, this.mHttpContext, request, null, responseHandler, context);
    }

    public RequestHandle post(String url, ResponseHandlerInterface responseHandler) {
        return this.post(null, url, null, responseHandler);
    }

    public RequestHandle post(String url, RequestParams params, ResponseHandlerInterface responseHandler) {
        return this.post(null, url, params, responseHandler);
    }

    public RequestHandle post(Context context, String url, RequestParams params, ResponseHandlerInterface responseHandler) {
        return this.post(context, url, this.paramsToEntity(params, responseHandler), null, responseHandler);
    }

    public RequestHandle post(Context context, String url, HttpEntity entity, String contentType, ResponseHandlerInterface responseHandler) {
        return this.sendRequest(this.mHttpClient, this.mHttpContext, this.addEntityToRequestBase(new HttpPost(URI.create(url).normalize()), entity), contentType, responseHandler, context);
    }

    public RequestHandle post(Context context, String url, Header[] headers, RequestParams params, String contentType, ResponseHandlerInterface responseHandler) {
        HttpPost request = new HttpPost(URI.create(url).normalize());
        if (params != null) {
            request.setEntity(this.paramsToEntity(params, responseHandler));
        }

        if (headers != null) {
            request.setHeaders(headers);
        }

        return this.sendRequest(this.mHttpClient, this.mHttpContext, request, contentType, responseHandler, context);
    }

    public RequestHandle post(Context context, String url, Header[] headers, HttpEntity entity, String contentType, ResponseHandlerInterface responseHandler) {
        HttpEntityEnclosingRequestBase request = this.addEntityToRequestBase(new HttpPost(URI.create(url).normalize()), entity);
        if (headers != null) {
            request.setHeaders(headers);
        }

        return this.sendRequest(this.mHttpClient, this.mHttpContext, request, contentType, responseHandler, context);
    }

    public RequestHandle put(String url, ResponseHandlerInterface responseHandler) {
        return this.put(null, url, null, responseHandler);
    }

    public RequestHandle put(String url, RequestParams params, ResponseHandlerInterface responseHandler) {
        return this.put(null, url, params, responseHandler);
    }

    public RequestHandle put(Context context, String url, RequestParams params, ResponseHandlerInterface responseHandler) {
        return this.put(context, url, this.paramsToEntity(params, responseHandler), null, responseHandler);
    }

    public RequestHandle put(Context context, String url, HttpEntity entity, String contentType, ResponseHandlerInterface responseHandler) {
        return this.sendRequest(this.mHttpClient, this.mHttpContext, this.addEntityToRequestBase(new HttpPut(URI.create(url).normalize()), entity), contentType, responseHandler, context);
    }

    public RequestHandle put(Context context, String url, Header[] headers, HttpEntity entity, String contentType, ResponseHandlerInterface responseHandler) {
        HttpEntityEnclosingRequestBase request = this.addEntityToRequestBase(new HttpPut(URI.create(url).normalize()), entity);
        if (headers != null) {
            request.setHeaders(headers);
        }

        return this.sendRequest(this.mHttpClient, this.mHttpContext, request, contentType, responseHandler, context);
    }

    public RequestHandle delete(String url, ResponseHandlerInterface responseHandler) {
        return this.delete(null, url, responseHandler);
    }

    public RequestHandle delete(Context context, String url, ResponseHandlerInterface responseHandler) {
        HttpDelete delete = new HttpDelete(URI.create(url).normalize());
        return this.sendRequest(this.mHttpClient, this.mHttpContext, delete, null, responseHandler, context);
    }

    public RequestHandle delete(Context context, String url, Header[] headers, ResponseHandlerInterface responseHandler) {
        HttpDelete delete = new HttpDelete(URI.create(url).normalize());
        if (headers != null) {
            delete.setHeaders(headers);
        }

        return this.sendRequest(this.mHttpClient, this.mHttpContext, delete, null, responseHandler, context);
    }

    public RequestHandle delete(Context context, String url, Header[] headers, RequestParams params, ResponseHandlerInterface responseHandler) {
        HttpDelete httpDelete = new HttpDelete(getUrlWithQueryString(this.isUrlEncodingEnabled, url, params));
        if (headers != null) {
            httpDelete.setHeaders(headers);
        }

        return this.sendRequest(this.mHttpClient, this.mHttpContext, httpDelete, null, responseHandler, context);
    }

    protected RequestHandle sendRequest(DefaultHttpClient client, HttpContext mHttpContext, HttpUriRequest uriRequest, String contentType, ResponseHandlerInterface responseHandler, Context context) {
        if (uriRequest == null) {
            throw new IllegalArgumentException("HttpUriRequest must not be null");
        } else if (responseHandler == null) {
            throw new IllegalArgumentException("ResponseHandler must not be null");
        } else if (responseHandler.getUseSynchronousMode()) {
            throw new IllegalArgumentException("Synchronous ResponseHandler used in AsyncHttpClient. You should create your response handler in a looper thread or use SyncHttpClient instead.");
        } else {
            if (contentType != null) {
                uriRequest.setHeader("Content-Type", contentType);
            }

            responseHandler.setRequestHeaders(uriRequest.getAllHeaders());
            responseHandler.setRequestURI(uriRequest.getURI());
            AsyncHttpRequest request = new AsyncHttpRequest(client, mHttpContext, uriRequest, responseHandler);
            this.mThreadPool.submit(request);
            RequestHandle requestHandle = new RequestHandle(request);
            if (context != null) {
                Object requestList = (List) this.mRequestMap.get(context);
                if (requestList == null) {
                    requestList = new LinkedList();
                    this.mRequestMap.put(context, (List<RequestHandle>) requestList);
                }

                if (responseHandler instanceof RangeFileAsyncHttpResponseHandler) {
                    ((RangeFileAsyncHttpResponseHandler) responseHandler).updateRequestHeaders(uriRequest);
                }

                ((List) requestList).add(requestHandle);
                Iterator iterator = ((List) requestList).iterator();

                while (iterator.hasNext()) {
                    if (((RequestHandle) iterator.next()).shouldBeGarbageCollected()) {
                        iterator.remove();
                    }
                }
            }

            return requestHandle;
        }
    }

    public void setURLEncodingEnabled(boolean enabled) {
        this.isUrlEncodingEnabled = enabled;
    }

    public static String getUrlWithQueryString(boolean shouldEncodeUrl, String url, RequestParams params) {
        if (shouldEncodeUrl) {
            url = url.replace(" ", "%20");
        }

        if (params != null) {
            String paramString = params.getParamString().trim();
            if (!paramString.equals("") && !paramString.equals("?")) {
                url = url + (url.contains("?") ? "&" : "?");
                url = url + paramString;
            }
        }

        return url;
    }

    public static void silentCloseInputStream(InputStream is) {
        try {
            if (is != null) {
                is.close();
            }
        } catch (IOException var2) {
            Log.e(TAG, "Cannot close input stream " + var2.getMessage());
        }

    }

    public static void silentCloseOutputStream(OutputStream os) {
        try {
            if (os != null) {
                os.close();
            }
        } catch (IOException var2) {
            Log.w(TAG, "Cannot close output stream " + var2.getMessage());
        }

    }

    private HttpEntity paramsToEntity(RequestParams params, ResponseHandlerInterface responseHandler) {
        HttpEntity entity = null;

        try {
            if (params != null) {
                entity = params.getEntity(responseHandler);
            }
        } catch (Throwable var5) {
            if (responseHandler != null) {
                responseHandler.sendFailureMessage(0, null, null, var5);
            } else {
                var5.printStackTrace();
            }
        }

        return entity;
    }

    public boolean isUrlEncodingEnabled() {
        return this.isUrlEncodingEnabled;
    }

    private HttpEntityEnclosingRequestBase addEntityToRequestBase(HttpEntityEnclosingRequestBase requestBase, HttpEntity entity) {
        if (entity != null) {
            requestBase.setEntity(entity);
        }

        return requestBase;
    }

    private static class InflatingEntity extends HttpEntityWrapper {
        public InflatingEntity(HttpEntity wrapped) {
            super(wrapped);
        }

        public InputStream getContent() throws IOException {
            return new GZIPInputStream(this.wrappedEntity.getContent());
        }

        public long getContentLength() {
            return -1L;
        }
    }
}