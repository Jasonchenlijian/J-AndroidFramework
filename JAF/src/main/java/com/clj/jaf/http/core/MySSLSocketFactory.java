package com.clj.jaf.http.core;

import android.util.Log;

import org.apache.http.conn.ssl.SSLSocketFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import cz.msebera.android.httpclient.HttpVersion;
import cz.msebera.android.httpclient.conn.scheme.PlainSocketFactory;
import cz.msebera.android.httpclient.conn.scheme.Scheme;
import cz.msebera.android.httpclient.conn.scheme.SchemeRegistry;
import cz.msebera.android.httpclient.conn.scheme.SocketFactory;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.impl.conn.tsccm.ThreadSafeClientConnManager;
import cz.msebera.android.httpclient.params.BasicHttpParams;
import cz.msebera.android.httpclient.params.HttpProtocolParams;

public class MySSLSocketFactory extends SSLSocketFactory {
    private static String TAG = MySSLSocketFactory.class.getSimpleName();
    SSLContext sslContext = SSLContext.getInstance("TLS");

    public MySSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
        super(truststore);
        X509TrustManager tm = new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };
        this.sslContext.init((KeyManager[]) null, new TrustManager[]{tm}, (SecureRandom) null);
    }

    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException {
        return this.sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
    }

    public Socket createSocket() throws IOException {
        return this.sslContext.getSocketFactory().createSocket();
    }

    public void fixHttpsURLConnection() {
        HttpsURLConnection.setDefaultSSLSocketFactory(this.sslContext.getSocketFactory());
    }

    public static KeyStore getKeystoreOfCA(InputStream cert) {
        BufferedInputStream caInput = null;
        Certificate ca = null;

        try {
            CertificateFactory keyStoreType = CertificateFactory.getInstance("X.509");
            caInput = new BufferedInputStream(cert);
            ca = keyStoreType.generateCertificate(caInput);
        } catch (CertificateException var14) {
            Log.w(TAG, var14.getMessage());
        } finally {
            try {
                if (caInput != null) {
                    caInput.close();
                }
            } catch (IOException var12) {
                Log.w(TAG, var12.getMessage());
            }

        }

        String keyStoreType1 = KeyStore.getDefaultType();
        KeyStore keyStore = null;

        try {
            keyStore = KeyStore.getInstance(keyStoreType1);
            keyStore.load((InputStream) null, (char[]) null);
            keyStore.setCertificateEntry("ca", ca);
        } catch (Exception var13) {
            Log.w(TAG, var13.getMessage());
        }

        return keyStore;
    }

    public static KeyStore getKeystore() {
        KeyStore trustStore = null;

        try {
            trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load((InputStream) null, (char[]) null);
        } catch (Throwable var2) {
            Log.w(TAG, var2.getMessage());
        }

        return trustStore;
    }

    public static cz.msebera.android.httpclient.conn.ssl.SSLSocketFactory getFixedSocketFactory() {
        Object socketFactory;
        try {
            socketFactory = new MySSLSocketFactory(getKeystore());
            ((SSLSocketFactory) socketFactory).setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        } catch (Throwable var2) {
            Log.w(TAG, var2.getMessage());
            socketFactory = SSLSocketFactory.getSocketFactory();
        }

        return (cz.msebera.android.httpclient.conn.ssl.SSLSocketFactory) socketFactory;
    }

    public static DefaultHttpClient getNewHttpClient(KeyStore keyStore) {
        try {
            MySSLSocketFactory e = new MySSLSocketFactory(keyStore);
            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", (SocketFactory) e, 443));
            BasicHttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, "UTF-8");
            ThreadSafeClientConnManager ccm = new ThreadSafeClientConnManager(params, registry);
            return new DefaultHttpClient(ccm, params);
        } catch (Exception var5) {
            return new DefaultHttpClient();
        }
    }
}
