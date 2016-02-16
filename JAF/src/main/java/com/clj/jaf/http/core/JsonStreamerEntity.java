package com.clj.jaf.http.core;

import android.util.Base64OutputStream;
import android.util.Log;

import com.clj.jaf.http.JAsyncHttpClient;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.message.BasicHeader;

class JsonStreamerEntity implements HttpEntity {
    private static final String TAG = JsonStreamerEntity.class.getSimpleName();
    private static final UnsupportedOperationException ERR_UNSUPPORTED = new UnsupportedOperationException("Unsupported operation in this implementation.");
    private static final int BUFFER_SIZE = 4096;
    private final byte[] mBuffer = new byte[4096];
    private static final StringBuilder BUILDER = new StringBuilder(128);
    private static final byte[] JSON_TRUE = "true".getBytes();
    private static final byte[] JSON_FALSE = "false".getBytes();
    private static final byte[] JSON_NULL = "null".getBytes();
    private static final byte[] STREAM_NAME = escape("name");
    private static final byte[] STREAM_TYPE = escape("type");
    private static final byte[] STREAM_CONTENTS = escape("contents");
    private static final byte[] STREAM_ELAPSED = escape("_elapsed");
    private static final Header HEADER_JSON_CONTENT = new BasicHeader("Content-Type", "application/json");
    private static final Header HEADER_GZIP_ENCODING = new BasicHeader("Content-Encoding", "gzip");
    private final Map<String, Object> jsonParams = new HashMap();
    private final Header contentEncoding;
    private final ResponseHandlerInterface progressHandler;

    public JsonStreamerEntity(ResponseHandlerInterface progressHandler, boolean useGZipCompression) {
        this.progressHandler = progressHandler;
        this.contentEncoding = useGZipCompression ? HEADER_GZIP_ENCODING : null;
    }

    public void addPart(String key, Object value) {
        this.jsonParams.put(key, value);
    }

    public boolean isRepeatable() {
        return false;
    }

    public boolean isChunked() {
        return false;
    }

    public boolean isStreaming() {
        return false;
    }

    public long getContentLength() {
        return -1L;
    }

    public Header getContentEncoding() {
        return this.contentEncoding;
    }

    public Header getContentType() {
        return HEADER_JSON_CONTENT;
    }

    public void consumeContent() throws IOException, UnsupportedOperationException {
    }

    public InputStream getContent() throws IOException, UnsupportedOperationException {
        throw ERR_UNSUPPORTED;
    }

    public void writeTo(OutputStream out) throws IOException {
        if (out == null) {
            throw new IllegalStateException("Output stream cannot be null.");
        } else {
            long now = System.currentTimeMillis();
            Object os = this.contentEncoding != null ? new GZIPOutputStream(out, 4096) : out;
            ((OutputStream) os).write(123);
            Set keys = this.jsonParams.keySet();
            Iterator var8 = keys.iterator();

            while (true) {
                String elapsedTime;
                Object value;
                do {
                    if (!var8.hasNext()) {
                        ((OutputStream) os).write(STREAM_ELAPSED);
                        ((OutputStream) os).write(58);
                        long elapsedTime1 = System.currentTimeMillis() - now;
                        ((OutputStream) os).write((elapsedTime1 + "}").getBytes());
                        Log.i(TAG, "Uploaded JSON in " + Math.floor((double) (elapsedTime1 / 1000L)) + " seconds");
                        ((OutputStream) os).flush();
                        JAsyncHttpClient.silentCloseOutputStream((OutputStream) os);
                        return;
                    }

                    elapsedTime = (String) var8.next();
                    value = this.jsonParams.get(elapsedTime);
                } while (value == null);

                ((OutputStream) os).write(escape(elapsedTime));
                ((OutputStream) os).write(58);
                boolean isFileWrapper = value instanceof RequestParams.FileWrapper;
                if (!isFileWrapper && !(value instanceof RequestParams.StreamWrapper)) {
                    if (value instanceof Boolean) {
                        ((OutputStream) os).write((Boolean) value ? JSON_TRUE : JSON_FALSE);
                    } else if (value instanceof Long) {
                        ((OutputStream) os).write(String.valueOf(((Number) value).longValue()).getBytes());
                    } else if (value instanceof Double) {
                        ((OutputStream) os).write(String.valueOf(((Number) value).doubleValue()).getBytes());
                    } else if (value instanceof Float) {
                        ((OutputStream) os).write(String.valueOf(((Number) value).floatValue()).getBytes());
                    } else if (value instanceof Integer) {
                        ((OutputStream) os).write(String.valueOf(((Number) value).intValue()).getBytes());
                    } else {
                        ((OutputStream) os).write(value.toString().getBytes());
                    }
                } else {
                    ((OutputStream) os).write(123);
                    if (isFileWrapper) {
                        this.writeToFromFile((OutputStream) os, (RequestParams.FileWrapper) value);
                    } else {
                        this.writeToFromStream((OutputStream) os, (RequestParams.StreamWrapper) value);
                    }

                    ((OutputStream) os).write(125);
                }

                ((OutputStream) os).write(44);
            }
        }
    }

    private void writeToFromStream(OutputStream os, RequestParams.StreamWrapper entry) throws IOException {
        this.writeMetaData(os, entry.name, entry.contentType);
        Base64OutputStream bos = new Base64OutputStream(os, 18);

        int bytesRead;
        while ((bytesRead = entry.inputStream.read(this.mBuffer)) != -1) {
            bos.write(this.mBuffer, 0, bytesRead);
        }

        JAsyncHttpClient.silentCloseOutputStream(bos);
        this.endMetaData(os);
        if (entry.autoClose) {
            JAsyncHttpClient.silentCloseInputStream(entry.inputStream);
        }

    }

    private void writeToFromFile(OutputStream os, RequestParams.FileWrapper wrapper) throws IOException {
        this.writeMetaData(os, wrapper.file.getName(), wrapper.contentType);
        int bytesWritten = 0;
        int totalSize = (int) wrapper.file.length();
        FileInputStream in = new FileInputStream(wrapper.file);
        Base64OutputStream bos = new Base64OutputStream(os, 18);

        int bytesRead;
        while ((bytesRead = in.read(this.mBuffer)) != -1) {
            bos.write(this.mBuffer, 0, bytesRead);
            bytesWritten += bytesRead;
            this.progressHandler.sendProgressMessage(bytesWritten, totalSize);
        }

        JAsyncHttpClient.silentCloseOutputStream(bos);
        this.endMetaData(os);
        JAsyncHttpClient.silentCloseInputStream(in);
    }

    private void writeMetaData(OutputStream os, String name, String contentType) throws IOException {
        os.write(STREAM_NAME);
        os.write(58);
        os.write(escape(name));
        os.write(44);
        os.write(STREAM_TYPE);
        os.write(58);
        os.write(escape(contentType));
        os.write(44);
        os.write(STREAM_CONTENTS);
        os.write(58);
        os.write(34);
    }

    private void endMetaData(OutputStream os) throws IOException {
        os.write(34);
    }

    static byte[] escape(String string) {
        if (string == null) {
            return JSON_NULL;
        } else {
            BUILDER.append('\"');
            int length = string.length();
            int pos = -1;

            while (true) {
                while (true) {
                    ++pos;
                    if (pos >= length) {
                        BUILDER.append('\"');

                        byte[] var9;
                        try {
                            var9 = BUILDER.toString().getBytes();
                        } finally {
                            BUILDER.setLength(0);
                        }

                        return var9;
                    }

                    char ch = string.charAt(pos);
                    switch (ch) {
                        case '\b':
                            BUILDER.append("\\b");
                            continue;
                        case '\t':
                            BUILDER.append("\\t");
                            continue;
                        case '\n':
                            BUILDER.append("\\n");
                            continue;
                        case '\f':
                            BUILDER.append("\\f");
                            continue;
                        case '\r':
                            BUILDER.append("\\r");
                            continue;
                        case '\"':
                            BUILDER.append("\\\"");
                            continue;
                        case '\\':
                            BUILDER.append("\\\\");
                            continue;
                    }

                    if (ch >= 0 && ch <= 31 || ch >= 127 && ch <= 159 || ch >= 8192 && ch <= 8447) {
                        String intString = Integer.toHexString(ch);
                        BUILDER.append("\\u");
                        int intLength = 4 - intString.length();

                        for (int zero = 0; zero < intLength; ++zero) {
                            BUILDER.append('0');
                        }

                        BUILDER.append(intString.toUpperCase(Locale.US));
                    } else {
                        BUILDER.append(ch);
                    }
                }
            }
        }
    }
}
