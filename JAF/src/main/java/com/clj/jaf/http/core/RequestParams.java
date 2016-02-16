package com.clj.jaf.http.core;

import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.utils.URLEncodedUtils;
import cz.msebera.android.httpclient.message.BasicNameValuePair;

public class RequestParams {
    public static final String APPLICATION_OCTET_STREAM = "application/octet-stream";
    protected static final String LOG_TAG = "RequestParams";
    protected boolean mIsRepeatable;
    protected boolean mUseJsonStreamer;
    protected boolean mAutoCloseInputStreams;
    protected final ConcurrentHashMap<String, String> mUrlParams;
    protected final ConcurrentHashMap<String, RequestParams.StreamWrapper> mStreamParams;
    protected final ConcurrentHashMap<String, RequestParams.FileWrapper> mFileParams;
    protected final ConcurrentHashMap<String, Object> mUrlParamsWithObjects;
    protected String contentEncoding;

    public void setContentEncoding(String encoding) {
        if (encoding != null) {
            this.contentEncoding = encoding;
        } else {
            Log.d("RequestParams", "setContentEncoding called with null attribute");
        }

    }

    public RequestParams() {
        this((Map) null);
    }

    public RequestParams(Map<String, String> source) {
        this.mUrlParams = new ConcurrentHashMap();
        this.mStreamParams = new ConcurrentHashMap();
        this.mFileParams = new ConcurrentHashMap();
        this.mUrlParamsWithObjects = new ConcurrentHashMap();
        this.contentEncoding = "UTF-8";
        if (source != null) {
            Iterator var3 = source.entrySet().iterator();

            while (var3.hasNext()) {
                Map.Entry entry = (Map.Entry) var3.next();
                this.put((String) entry.getKey(), (String) entry.getValue());
            }
        }

    }

    public RequestParams(final String key, final String value) {
        this((Map) (new HashMap() {
            {
                this.put(key, value);
            }
        }));
    }

    public RequestParams(Object... keysAndValues) {
        this.mUrlParams = new ConcurrentHashMap();
        this.mStreamParams = new ConcurrentHashMap();
        this.mFileParams = new ConcurrentHashMap();
        this.mUrlParamsWithObjects = new ConcurrentHashMap();
        this.contentEncoding = "UTF-8";
        int len = keysAndValues.length;
        if (len % 2 != 0) {
            throw new IllegalArgumentException("Supplied arguments must be even");
        } else {
            for (int i = 0; i < len; i += 2) {
                String key = String.valueOf(keysAndValues[i]);
                String val = String.valueOf(keysAndValues[i + 1]);
                this.put(key, val);
            }

        }
    }

    public void put(String key, String value) {
        if (key != null && value != null) {
            this.mUrlParams.put(key, value);
        }

    }

    public void put(String key, File file) throws FileNotFoundException {
        this.put(key, (File) file, (String) null);
    }

    public void put(String key, File file, String contentType) throws FileNotFoundException {
        if (file != null && file.exists()) {
            if (key != null) {
                this.mFileParams.put(key, new RequestParams.FileWrapper(file, contentType));
            }

        } else {
            throw new FileNotFoundException();
        }
    }

    public void put(String key, InputStream stream) {
        this.put(key, (InputStream) stream, (String) null);
    }

    public void put(String key, InputStream stream, String name) {
        this.put(key, stream, name, (String) null);
    }

    public void put(String key, InputStream stream, String name, String contentType) {
        this.put(key, stream, name, contentType, this.mAutoCloseInputStreams);
    }

    public void put(String key, InputStream stream, String name, String contentType, boolean autoClose) {
        if (key != null && stream != null) {
            this.mStreamParams.put(key, RequestParams.StreamWrapper.newInstance(stream, name, contentType, autoClose));
        }

    }

    public void put(String key, Object value) {
        if (key != null && value != null) {
            this.mUrlParamsWithObjects.put(key, value);
        }

    }

    public void put(String key, int value) {
        if (key != null) {
            this.mUrlParams.put(key, String.valueOf(value));
        }

    }

    public void put(String key, long value) {
        if (key != null) {
            this.mUrlParams.put(key, String.valueOf(value));
        }

    }

    public void add(String key, String value) {
        if (key != null && value != null) {
            Object params = this.mUrlParamsWithObjects.get(key);
            if (params == null) {
                params = new HashSet();
                this.put(key, params);
            }

            if (params instanceof List) {
                ((List) params).add(value);
            } else if (params instanceof Set) {
                ((Set) params).add(value);
            }
        }

    }

    public void remove(String key) {
        this.mUrlParams.remove(key);
        this.mStreamParams.remove(key);
        this.mFileParams.remove(key);
        this.mUrlParamsWithObjects.remove(key);
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        Iterator kv = this.mUrlParams.entrySet().iterator();

        Map.Entry params;
        while (kv.hasNext()) {
            params = (Map.Entry) kv.next();
            if (result.length() > 0) {
                result.append("&");
            }

            result.append((String) params.getKey());
            result.append("=");
            result.append((String) params.getValue());
        }

        kv = this.mStreamParams.entrySet().iterator();

        while (kv.hasNext()) {
            params = (Map.Entry) kv.next();
            if (result.length() > 0) {
                result.append("&");
            }

            result.append((String) params.getKey());
            result.append("=");
            result.append("STREAM");
        }

        kv = this.mFileParams.entrySet().iterator();

        while (kv.hasNext()) {
            params = (Map.Entry) kv.next();
            if (result.length() > 0) {
                result.append("&");
            }

            result.append((String) params.getKey());
            result.append("=");
            result.append("FILE");
        }

        List params1 = this.getParamsList((String) null, this.mUrlParamsWithObjects);
        Iterator var4 = params1.iterator();

        while (var4.hasNext()) {
            BasicNameValuePair kv1 = (BasicNameValuePair) var4.next();
            if (result.length() > 0) {
                result.append("&");
            }

            result.append(kv1.getName());
            result.append("=");
            result.append(kv1.getValue());
        }

        return result.toString();
    }

    public void setHttpEntityIsRepeatable(boolean isRepeatable) {
        this.mIsRepeatable = isRepeatable;
    }

    public void setUseJsonStreamer(boolean useJsonStreamer) {
        this.mUseJsonStreamer = useJsonStreamer;
    }

    public void setAutoCloseInputStreams(boolean flag) {
        this.mAutoCloseInputStreams = flag;
    }

    public HttpEntity getEntity(ResponseHandlerInterface progressHandler) throws IOException {
        return this.mUseJsonStreamer ? this.createJsonStreamerEntity(progressHandler) : (this.mStreamParams.isEmpty() && this.mFileParams.isEmpty() ? this.createFormEntity() : this.createMultipartEntity(progressHandler));
    }

    private HttpEntity createJsonStreamerEntity(ResponseHandlerInterface progressHandler) throws IOException {
        JsonStreamerEntity entity = new JsonStreamerEntity(progressHandler, !this.mFileParams.isEmpty() || !this.mStreamParams.isEmpty());
        Iterator var4 = this.mUrlParams.entrySet().iterator();

        Map.Entry entry;
        while (var4.hasNext()) {
            entry = (Map.Entry) var4.next();
            entity.addPart((String) entry.getKey(), entry.getValue());
        }

        var4 = this.mUrlParamsWithObjects.entrySet().iterator();

        while (var4.hasNext()) {
            entry = (Map.Entry) var4.next();
            entity.addPart((String) entry.getKey(), entry.getValue());
        }

        var4 = this.mFileParams.entrySet().iterator();

        while (var4.hasNext()) {
            entry = (Map.Entry) var4.next();
            entity.addPart((String) entry.getKey(), entry.getValue());
        }

        var4 = this.mStreamParams.entrySet().iterator();

        while (var4.hasNext()) {
            entry = (Map.Entry) var4.next();
            RequestParams.StreamWrapper stream = (RequestParams.StreamWrapper) entry.getValue();
            if (stream.inputStream != null) {
                entity.addPart((String) entry.getKey(), RequestParams.StreamWrapper.newInstance(stream.inputStream, stream.name, stream.contentType, stream.autoClose));
            }
        }

        return entity;
    }

    private HttpEntity createFormEntity() {
        try {
            return new UrlEncodedFormEntity(this.getParamsList(), this.contentEncoding);
        } catch (UnsupportedEncodingException var2) {
            Log.e("RequestParams", "createFormEntity failed", var2);
            return null;
        }
    }

    private HttpEntity createMultipartEntity(ResponseHandlerInterface progressHandler) throws IOException {
        SimpleMultipartEntity entity = new SimpleMultipartEntity(progressHandler);
        entity.setIsRepeatable(this.mIsRepeatable);
        Iterator entry = this.mUrlParams.entrySet().iterator();

        while (entry.hasNext()) {
            Map.Entry params = (Map.Entry) entry.next();
            entity.addPart((String) params.getKey(), (String) params.getValue());
        }

        List params1 = this.getParamsList((String) null, this.mUrlParamsWithObjects);
        Iterator var5 = params1.iterator();

        while (var5.hasNext()) {
            BasicNameValuePair entry1 = (BasicNameValuePair) var5.next();
            entity.addPart(entry1.getName(), entry1.getValue());
        }

        var5 = this.mStreamParams.entrySet().iterator();

        Map.Entry entry2;
        while (var5.hasNext()) {
            entry2 = (Map.Entry) var5.next();
            RequestParams.StreamWrapper fileWrapper = (RequestParams.StreamWrapper) entry2.getValue();
            if (fileWrapper.inputStream != null) {
                entity.addPart((String) entry2.getKey(), fileWrapper.name, fileWrapper.inputStream, fileWrapper.contentType);
            }
        }

        var5 = this.mFileParams.entrySet().iterator();

        while (var5.hasNext()) {
            entry2 = (Map.Entry) var5.next();
            RequestParams.FileWrapper fileWrapper1 = (RequestParams.FileWrapper) entry2.getValue();
            entity.addPart((String) entry2.getKey(), fileWrapper1.file, fileWrapper1.contentType);
        }

        return entity;
    }

    protected List<BasicNameValuePair> getParamsList() {
        LinkedList lparams = new LinkedList();
        Iterator var3 = this.mUrlParams.entrySet().iterator();

        while (var3.hasNext()) {
            Map.Entry entry = (Map.Entry) var3.next();
            lparams.add(new BasicNameValuePair((String) entry.getKey(), (String) entry.getValue()));
        }

        lparams.addAll(this.getParamsList((String) null, this.mUrlParamsWithObjects));
        return lparams;
    }

    private List<BasicNameValuePair> getParamsList(String key, Object value) {
        LinkedList params = new LinkedList();
        if (value instanceof Map) {
            Map set = (Map) value;
            ArrayList nestedValue = new ArrayList(set.keySet());
            Collections.sort(nestedValue);
            Iterator var7 = nestedValue.iterator();

            while (var7.hasNext()) {
                Object nestedValueIndex = var7.next();
                if (nestedValueIndex instanceof String) {
                    Object nestedValue1 = set.get(nestedValueIndex);
                    if (nestedValue1 != null) {
                        params.addAll(this.getParamsList(key == null ? (String) nestedValueIndex : String.format("%s[%s]", new Object[]{key, nestedValueIndex}), nestedValue1));
                    }
                }
            }
        } else {
            int var12;
            int var14;
            if (value instanceof List) {
                List var9 = (List) value;
                var12 = var9.size();

                for (var14 = 0; var14 < var12; ++var14) {
                    params.addAll(this.getParamsList(String.format("%s[%d]", new Object[]{key, Integer.valueOf(var14)}), var9.get(var14)));
                }
            } else if (value instanceof Object[]) {
                Object[] var10 = (Object[]) value;
                var12 = var10.length;

                for (var14 = 0; var14 < var12; ++var14) {
                    params.addAll(this.getParamsList(String.format("%s[%d]", new Object[]{key, Integer.valueOf(var14)}), var10[var14]));
                }
            } else if (value instanceof Set) {
                Set var11 = (Set) value;
                Iterator var15 = var11.iterator();

                while (var15.hasNext()) {
                    Object var13 = var15.next();
                    params.addAll(this.getParamsList(key, var13));
                }
            } else {
                params.add(new BasicNameValuePair(key, value.toString()));
            }
        }

        return params;
    }

    public String getParamString() {
        return URLEncodedUtils.format(this.getParamsList(), this.contentEncoding);
    }

    public static class FileWrapper {
        public final File file;
        public final String contentType;

        public FileWrapper(File file, String contentType) {
            this.file = file;
            this.contentType = contentType;
        }
    }

    public static class StreamWrapper {
        public final InputStream inputStream;
        public final String name;
        public final String contentType;
        public final boolean autoClose;

        public StreamWrapper(InputStream inputStream, String name, String contentType, boolean autoClose) {
            this.inputStream = inputStream;
            this.name = name;
            this.contentType = contentType;
            this.autoClose = autoClose;
        }

        static RequestParams.StreamWrapper newInstance(InputStream inputStream, String name, String contentType, boolean autoClose) {
            return new RequestParams.StreamWrapper(inputStream, name, contentType == null ? "application/octet-stream" : contentType, autoClose);
        }
    }
}
