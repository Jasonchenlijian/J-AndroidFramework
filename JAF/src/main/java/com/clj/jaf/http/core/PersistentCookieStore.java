package com.clj.jaf.http.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cz.msebera.android.httpclient.client.CookieStore;
import cz.msebera.android.httpclient.cookie.Cookie;

public class PersistentCookieStore implements CookieStore {
    private static final String LOG_TAG = "PersistentCookieStore";
    private static final String COOKIE_PREFS = "CookiePrefsFile";
    private static final String COOKIE_NAME_STORE = "names";
    private static final String COOKIE_NAME_PREFIX = "cookie_";
    private boolean mOmitNonPersistentCookies = false;
    private final ConcurrentHashMap<String, Cookie> mCookies;
    private final SharedPreferences mCookiePrefs;

    public PersistentCookieStore(Context context) {
        this.mCookiePrefs = context.getSharedPreferences("CookiePrefsFile", 0);
        this.mCookies = new ConcurrentHashMap();
        String storedCookieNames = this.mCookiePrefs.getString("names", (String) null);
        if (storedCookieNames != null) {
            String[] cookieNames = TextUtils.split(storedCookieNames, ",");
            String[] var7 = cookieNames;
            int var6 = cookieNames.length;

            for (int var5 = 0; var5 < var6; ++var5) {
                String name = var7[var5];
                String encodedCookie = this.mCookiePrefs.getString("cookie_" + name, (String) null);
                if (encodedCookie != null) {
                    Cookie decodedCookie = this.decodeCookie(encodedCookie);
                    if (decodedCookie != null) {
                        this.mCookies.put(name, decodedCookie);
                    }
                }
            }

            this.clearExpired(new Date());
        }

    }

    public void addCookie(Cookie cookie) {
        if (!this.mOmitNonPersistentCookies || cookie.isPersistent()) {
            String name = cookie.getName() + cookie.getDomain();
            if (!cookie.isExpired(new Date())) {
                this.mCookies.put(name, cookie);
            } else {
                this.mCookies.remove(name);
            }

            SharedPreferences.Editor prefsWriter = this.mCookiePrefs.edit();
            prefsWriter.putString("names", TextUtils.join(",", this.mCookies.keySet()));
            prefsWriter.putString("cookie_" + name, this.encodeCookie(new SerializableCookie(cookie)));
            prefsWriter.commit();
        }
    }

    public void clear() {
        SharedPreferences.Editor prefsWriter = this.mCookiePrefs.edit();
        Iterator var3 = this.mCookies.keySet().iterator();

        while (var3.hasNext()) {
            String name = (String) var3.next();
            prefsWriter.remove("cookie_" + name);
        }

        prefsWriter.remove("names");
        prefsWriter.commit();
        this.mCookies.clear();
    }

    public boolean clearExpired(Date date) {
        boolean clearedAny = false;
        SharedPreferences.Editor prefsWriter = this.mCookiePrefs.edit();
        Iterator var5 = this.mCookies.entrySet().iterator();

        while (var5.hasNext()) {
            Map.Entry entry = (Map.Entry) var5.next();
            String name = (String) entry.getKey();
            Cookie cookie = (Cookie) entry.getValue();
            if (cookie.isExpired(date)) {
                this.mCookies.remove(name);
                prefsWriter.remove("cookie_" + name);
                clearedAny = true;
            }
        }

        if (clearedAny) {
            prefsWriter.putString("names", TextUtils.join(",", this.mCookies.keySet()));
        }

        prefsWriter.commit();
        return clearedAny;
    }

    public List<Cookie> getCookies() {
        return new ArrayList(this.mCookies.values());
    }

    public void setOmitNonPersistentCookies(boolean omitNonPersistentCookies) {
        this.mOmitNonPersistentCookies = omitNonPersistentCookies;
    }

    public void deleteCookie(Cookie cookie) {
        String name = cookie.getName();
        this.mCookies.remove(name);
        SharedPreferences.Editor prefsWriter = this.mCookiePrefs.edit();
        prefsWriter.remove("cookie_" + name);
        prefsWriter.commit();
    }

    protected String encodeCookie(SerializableCookie cookie) {
        if (cookie == null) {
            return null;
        } else {
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            try {
                ObjectOutputStream e = new ObjectOutputStream(os);
                e.writeObject(cookie);
            } catch (Exception var4) {
                return null;
            }

            return this.byteArrayToHexString(os.toByteArray());
        }
    }

    protected Cookie decodeCookie(String cookieString) {
        byte[] bytes = this.hexStringToByteArray(cookieString);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        Cookie cookie = null;

        try {
            ObjectInputStream exception = new ObjectInputStream(byteArrayInputStream);
            cookie = ((SerializableCookie) exception.readObject()).getCookie();
        } catch (Exception var6) {
            Log.d("PersistentCookieStore", "decodeCookie failed" + var6.getMessage());
        }

        return cookie;
    }

    protected String byteArrayToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        byte[] var6 = bytes;
        int var5 = bytes.length;

        for (int var4 = 0; var4 < var5; ++var4) {
            byte element = var6[var4];
            int v = element & 255;
            if (v < 16) {
                sb.append('0');
            }

            sb.append(Integer.toHexString(v));
        }

        return sb.toString().toUpperCase(Locale.US);
    }

    protected byte[] hexStringToByteArray(String hexString) {
        int len = hexString.length();
        byte[] data = new byte[len / 2];

        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4) + Character.digit(hexString.charAt(i + 1), 16));
        }

        return data;
    }
}
