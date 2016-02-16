package com.clj.jaf.http.core;

import android.util.Log;

import com.clj.jaf.http.JAsyncHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.message.BasicHeader;

class SimpleMultipartEntity implements HttpEntity {
    private static final String TAG = SimpleMultipartEntity.class.getSimpleName();
    private static final String STR_CR_LF = "\r\n";
    private static final byte[] CR_LF = "\r\n".getBytes();
    private static final byte[] TRANSFER_ENCODING_BINARY = "Content-Transfer-Encoding: binary\r\n".getBytes();
    private static final char[] MULTIPART_CHARS = "-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private final String mBoundary;
    private final byte[] mBoundaryLine;
    private final byte[] mBoundaryEnd;
    private boolean mIsRepeatable;
    private final List<FilePart> mFileParts = new ArrayList();
    private final ByteArrayOutputStream mOut = new ByteArrayOutputStream();
    private final ResponseHandlerInterface mProgressHandler;
    private int mBytesWritten;
    private int mTotalSize;

    public SimpleMultipartEntity(ResponseHandlerInterface progressHandler) {
        StringBuilder buf = new StringBuilder();
        Random rand = new Random();

        for (int i = 0; i < 30; ++i) {
            buf.append(MULTIPART_CHARS[rand.nextInt(MULTIPART_CHARS.length)]);
        }

        this.mBoundary = buf.toString();
        this.mBoundaryLine = ("--" + this.mBoundary + "\r\n").getBytes();
        this.mBoundaryEnd = ("--" + this.mBoundary + "--" + "\r\n").getBytes();
        this.mProgressHandler = progressHandler;
    }

    public void addPart(String key, String value, String contentType) {
        try {
            this.mOut.write(this.mBoundaryLine);
            this.mOut.write(this.createContentDisposition(key));
            this.mOut.write(this.createContentType(contentType));
            this.mOut.write(CR_LF);
            this.mOut.write(value.getBytes());
            this.mOut.write(CR_LF);
        } catch (IOException var5) {
            Log.e(TAG, "addPart ByteArrayOutputStream exception" + var5.getMessage());
        }

    }

    public void addPart(String key, String value) {
        this.addPart(key, value, "text/plain; charset=UTF-8");
    }

    public void addPart(String key, File file) {
        this.addPart(key, (File) file, (String) null);
    }

    public void addPart(String key, File file, String type) {
        this.mFileParts.add(new SimpleMultipartEntity.FilePart(key, file, this.normalizeContentType(type)));
    }

    public void addPart(String key, String streamName, InputStream inputStream, String type) throws IOException {
        this.mOut.write(this.mBoundaryLine);
        this.mOut.write(this.createContentDisposition(key, streamName));
        this.mOut.write(this.createContentType(type));
        this.mOut.write(TRANSFER_ENCODING_BINARY);
        this.mOut.write(CR_LF);
        byte[] tmp = new byte[4096];

        int l;
        while ((l = inputStream.read(tmp)) != -1) {
            this.mOut.write(tmp, 0, l);
        }

        this.mOut.write(CR_LF);
        this.mOut.flush();
        JAsyncHttpClient.silentCloseOutputStream(this.mOut);
    }

    private String normalizeContentType(String type) {
        return type == null ? "application/octet-stream" : type;
    }

    private byte[] createContentType(String type) {
        String result = "Content-Type: " + this.normalizeContentType(type) + "\r\n";
        return result.getBytes();
    }

    private byte[] createContentDisposition(String key) {
        return ("Content-Disposition: form-data; name=\"" + key + "\"" + "\r\n").getBytes();
    }

    private byte[] createContentDisposition(String key, String fileName) {
        return ("Content-Disposition: form-data; name=\"" + key + "\"; filename=\"" + fileName + "\"" + "\r\n").getBytes();
    }

    private void updateProgress(int count) {
        this.mBytesWritten += count;
        this.mProgressHandler.sendProgressMessage(this.mBytesWritten, this.mTotalSize);
    }

    public long getContentLength() {
        long contentLen = (long) this.mOut.size();

        long len;
        for (Iterator var4 = this.mFileParts.iterator(); var4.hasNext(); contentLen += len) {
            SimpleMultipartEntity.FilePart filePart = (SimpleMultipartEntity.FilePart) var4.next();
            len = filePart.getTotalLength();
            if (len < 0L) {
                return -1L;
            }
        }

        contentLen += (long) this.mBoundaryEnd.length;
        return contentLen;
    }

    public Header getContentType() {
        return new BasicHeader("Content-Type", "multipart/form-data; boundary=" + this.mBoundary);
    }

    public boolean isChunked() {
        return false;
    }

    public void setIsRepeatable(boolean isRepeatable) {
        this.mIsRepeatable = isRepeatable;
    }

    public boolean isRepeatable() {
        return this.mIsRepeatable;
    }

    public boolean isStreaming() {
        return false;
    }

    public void writeTo(OutputStream outstream) throws IOException {
        this.mBytesWritten = 0;
        this.mTotalSize = (int) this.getContentLength();
        this.mOut.writeTo(outstream);
        this.updateProgress(this.mOut.size());
        Iterator var3 = this.mFileParts.iterator();

        while (var3.hasNext()) {
            SimpleMultipartEntity.FilePart filePart = (SimpleMultipartEntity.FilePart) var3.next();
            filePart.writeTo(outstream);
        }

        outstream.write(this.mBoundaryEnd);
        this.updateProgress(this.mBoundaryEnd.length);
    }

    public Header getContentEncoding() {
        return null;
    }

    public void consumeContent() throws IOException, UnsupportedOperationException {
        if (this.isStreaming()) {
            throw new UnsupportedOperationException("Streaming entity does not implement #consumeContent()");
        }
    }

    public InputStream getContent() throws IOException, UnsupportedOperationException {
        throw new UnsupportedOperationException("getContent() is not supported. Use writeTo() instead.");
    }

    private class FilePart {
        public File file;
        public byte[] header;

        public FilePart(String key, File file, String type) {
            this.header = this.createHeader(key, file.getName(), type);
            this.file = file;
        }

        private byte[] createHeader(String key, String filename, String type) {
            ByteArrayOutputStream headerStream = new ByteArrayOutputStream();

            try {
                headerStream.write(SimpleMultipartEntity.this.mBoundaryLine);
                headerStream.write(SimpleMultipartEntity.this.createContentDisposition(key, filename));
                headerStream.write(SimpleMultipartEntity.this.createContentType(type));
                headerStream.write(SimpleMultipartEntity.TRANSFER_ENCODING_BINARY);
                headerStream.write(SimpleMultipartEntity.CR_LF);
            } catch (IOException var6) {
                Log.e(SimpleMultipartEntity.TAG, "createHeader ByteArrayOutputStream exception" + var6.getMessage());
            }

            return headerStream.toByteArray();
        }

        public long getTotalLength() {
            long streamLength = this.file.length() + (long) SimpleMultipartEntity.CR_LF.length;
            return (long) this.header.length + streamLength;
        }

        public void writeTo(OutputStream out) throws IOException {
            out.write(this.header);
            SimpleMultipartEntity.this.updateProgress(this.header.length);
            FileInputStream inputStream = new FileInputStream(this.file);
            byte[] tmp = new byte[4096];

            int bytesRead;
            while ((bytesRead = inputStream.read(tmp)) != -1) {
                out.write(tmp, 0, bytesRead);
                SimpleMultipartEntity.this.updateProgress(bytesRead);
            }

            out.write(SimpleMultipartEntity.CR_LF);
            SimpleMultipartEntity.this.updateProgress(SimpleMultipartEntity.CR_LF.length);
            out.flush();
            JAsyncHttpClient.silentCloseInputStream(inputStream);
        }
    }
}
