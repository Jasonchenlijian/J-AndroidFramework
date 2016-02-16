package com.clj.jaf.http.core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;

import cz.msebera.android.httpclient.cookie.Cookie;
import cz.msebera.android.httpclient.impl.cookie.BasicClientCookie;

public class SerializableCookie implements Serializable {
    private static final long serialVersionUID = 6374381828722046732L;
    private final transient Cookie mCookie;
    private transient BasicClientCookie mClientCookie;

    public SerializableCookie(Cookie cookie) {
        this.mCookie = cookie;
    }

    public Cookie getCookie() {
        Object bestCookie = this.mCookie;
        if (this.mClientCookie != null) {
            bestCookie = this.mClientCookie;
        }

        return (Cookie) bestCookie;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(this.mCookie.getName());
        out.writeObject(this.mCookie.getValue());
        out.writeObject(this.mCookie.getComment());
        out.writeObject(this.mCookie.getDomain());
        out.writeObject(this.mCookie.getExpiryDate());
        out.writeObject(this.mCookie.getPath());
        out.writeInt(this.mCookie.getVersion());
        out.writeBoolean(this.mCookie.isSecure());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        String name = (String) in.readObject();
        String value = (String) in.readObject();
        this.mClientCookie = new BasicClientCookie(name, value);
        this.mClientCookie.setComment((String) in.readObject());
        this.mClientCookie.setDomain((String) in.readObject());
        this.mClientCookie.setExpiryDate((Date) in.readObject());
        this.mClientCookie.setPath((String) in.readObject());
        this.mClientCookie.setVersion(in.readInt());
        this.mClientCookie.setSecure(in.readBoolean());
    }
}
