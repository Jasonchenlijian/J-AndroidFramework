package com.clj.jaf.http.core;

import java.io.IOException;
import java.net.URI;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpResponse;

public interface ResponseHandlerInterface {
    void sendResponseMessage(HttpResponse var1) throws IOException;

    void sendStartMessage();

    void sendFinishMessage();

    void sendProgressMessage(int var1, int var2);

    void sendCancelMessage();

    void sendSuccessMessage(int var1, Header[] var2, byte[] var3);

    void sendFailureMessage(int var1, Header[] var2, byte[] var3, Throwable var4);

    void sendRetryMessage(int var1);

    URI getRequestURI();

    Header[] getRequestHeaders();

    void setRequestURI(URI var1);

    void setRequestHeaders(Header[] var1);

    void setUseSynchronousMode(boolean var1);

    boolean getUseSynchronousMode();
}
