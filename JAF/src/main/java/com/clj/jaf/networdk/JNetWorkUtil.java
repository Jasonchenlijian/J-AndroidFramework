package com.clj.jaf.networdk;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.clj.jaf.app.JApplication;

public class JNetWorkUtil {

    public JNetWorkUtil() {
    }

    public static boolean isNetworkAvailable() {
        ConnectivityManager mgr = (ConnectivityManager)
                JApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] info = mgr.getAllNetworkInfo();
        if (info != null) {
            for (int i = 0; i < info.length; ++i) {
                if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isNetworkConnected() {
        ConnectivityManager mConnectivityManager = (ConnectivityManager)
                JApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        return mNetworkInfo != null && mNetworkInfo.isAvailable();
    }

    public static boolean isWifiConnected() {
        ConnectivityManager mConnectivityManager = (ConnectivityManager)
                JApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWiFiNetworkInfo = mConnectivityManager.getNetworkInfo(1);
        return mWiFiNetworkInfo != null && mWiFiNetworkInfo.isAvailable();
    }

    public static boolean isMobileConnected() {
        ConnectivityManager mConnectivityManager = (ConnectivityManager)
                JApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mMobileNetworkInfo = mConnectivityManager.getNetworkInfo(0);
        return mMobileNetworkInfo != null && mMobileNetworkInfo.isAvailable();
    }

    public static int getConnectedType() {
        ConnectivityManager mConnectivityManager = (ConnectivityManager)
                JApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        return mNetworkInfo != null && mNetworkInfo.isAvailable() ? mNetworkInfo.getType() : -1;
    }

    public static JNetWorkUtil.netType getAPNType() {
        ConnectivityManager connMgr = (ConnectivityManager)
                JApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null) {
            return JNetWorkUtil.netType.noneNet;
        } else {
            int nType = networkInfo.getType();
            return nType == 0 ?
                    (networkInfo.getExtraInfo().toLowerCase().equals("cmnet") ?
                            JNetWorkUtil.netType.CMNET : JNetWorkUtil.netType.CMWAP) : (nType == 1 ?
                    JNetWorkUtil.netType.wifi : JNetWorkUtil.netType.noneNet);
        }
    }

    public static enum netType {
        wifi,
        CMNET,
        CMWAP,
        noneNet;

        private netType() {
        }
    }
}
