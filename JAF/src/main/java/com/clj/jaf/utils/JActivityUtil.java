package com.clj.jaf.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

public class JActivityUtil {
    private static final String TAG = JActivityUtil.class.getSimpleName();
    public static String FIELD_DATA0 = "data0";
    public static String FIELD_DATA1 = "data1";
    public static String FIELD_DATA2 = "data2";
    public static String FIELD_DATA3 = "data3";
    public static String FIELD_DATA4 = "data4";
    public static String FIELD_DATA5 = "data5";

    public JActivityUtil() {
    }

    public static void jumpToActivity(Context context, Intent datatIntent) {
        context.startActivity(datatIntent);
    }

    public static void jumpPostToActivity(final Context context, final Intent datatIntent, final int second) {
        (new AsyncTask<String, Integer, Boolean>() {
            protected Boolean doInBackground(String... params) {
                try {
                    Thread.sleep((long) (second * 1000));
                } catch (Exception var3) {

                }
                return null;
            }

            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                context.startActivity(datatIntent);
            }
        }).execute(new String[]{""});
    }

    public static void jumpToActivity(Context context, Class<? extends Activity> targetClass) {
        Intent datatIntent = new Intent(context, targetClass);
        context.startActivity(datatIntent);
    }

    public static void jumpPostToActivity(final Context context, final Class<? extends Activity> targetClass, final int second) {
        (new AsyncTask<String, Integer, Boolean>() {
            protected Boolean doInBackground(String... params) {
                try {
                    Thread.sleep((long) (second * 1000));
                } catch (Exception var3) {
                    ;
                }
                return null;
            }

            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                Intent datatIntent = new Intent(context, targetClass);
                context.startActivity(datatIntent);
            }
        }).execute(new String[]{""});
    }

    public static void jumpToNewActivity(Context context, Class<? extends Activity> targetClass) {
        Intent datatIntent = new Intent(context, targetClass);
        datatIntent.setFlags(268435456);
        context.startActivity(datatIntent);
    }

    public static void jumpToNewTopActivity(Context context, Class<? extends Activity> targetClass) {
        Intent datatIntent = new Intent(context, targetClass);
        datatIntent.setFlags(335544320);
        context.startActivity(datatIntent);
    }

    public static void jumpPostToNewActivity(final Context context, final Class<? extends Activity> targetClass, final int second) {
        (new AsyncTask<String, Integer, Boolean>() {
            protected Boolean doInBackground(String... params) {
                try {
                    Thread.sleep((long) (second * 1000));
                } catch (Exception var3) {
                    ;
                }
                return null;
            }

            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                Intent datatIntent = new Intent(context, targetClass);
                datatIntent.setFlags(268435456);
                context.startActivity(datatIntent);
            }
        }).execute(new String[]{""});
    }

    public static void jumpPostToNewTopActivity(final Context context, final Class<? extends Activity> targetClass, final int second) {
        (new AsyncTask<String, Integer, Boolean>() {
            protected Boolean doInBackground(String... params) {
                try {
                    Thread.sleep((long) (second * 1000));
                } catch (Exception var3) {

                }

                return null;
            }

            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                Intent datatIntent = new Intent(context, targetClass);
                datatIntent.setFlags(335544320);
                context.startActivity(datatIntent);
            }
        }).execute(new String[]{""});
    }

    public static void jumpToActivity(Context context, Class<? extends Activity> targetClass, String... datas) {
        Intent datatIntent = new Intent(context, targetClass);
        if (datas != null) {
            for (int i = 0; i < datas.length; ++i) {
                datatIntent.putExtra("data" + i, datas[i]);
            }
        }

        context.startActivity(datatIntent);
    }

    public static void jumpPostToActivity(final Context context, final Class<? extends Activity> targetClass, final int second, final String... datas) {
        (new AsyncTask<String, Integer, Boolean>() {
            protected Boolean doInBackground(String... params) {
                try {
                    Thread.sleep((long) (second * 1000));
                } catch (Exception var3) {
                    ;
                }
                return null;
            }

            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                Intent datatIntent = new Intent(context, targetClass);
                if (datas != null) {
                    for (int i = 0; i < datas.length; ++i) {
                        datatIntent.putExtra("data" + i, datas[i]);
                    }
                }

                context.startActivity(datatIntent);
            }
        }).execute(new String[]{""});
    }

    public static void jumpToNewActivity(Context context, Class<? extends Activity> targetClass, String... datas) {
        Intent datatIntent = new Intent(context, targetClass);
        datatIntent.setFlags(268435456);
        if (datas != null) {
            for (int i = 0; i < datas.length; ++i) {
                datatIntent.putExtra("data" + i, datas[i]);
            }
        }

        context.startActivity(datatIntent);
    }

    public static void jumpPostToNewActivity(final Context context, final Class<? extends Activity> targetClass, final int second, final String... datas) {
        (new AsyncTask<String, Integer, Boolean>() {
            protected Boolean doInBackground(String... params) {
                try {
                    Thread.sleep((long) (second * 1000));
                } catch (Exception var3) {
                    ;
                }
                return null;
            }

            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                Intent datatIntent = new Intent(context, targetClass);
                datatIntent.setFlags(268435456);
                if (datas != null) {
                    for (int i = 0; i < datas.length; ++i) {
                        datatIntent.putExtra("data" + i, datas[i]);
                    }
                }

                context.startActivity(datatIntent);
            }
        }).execute(new String[]{""});
    }

    public static void jumpPostToNewTopActivity(final Context context, final Class<? extends Activity> targetClass, final int second, final String... datas) {
        (new AsyncTask<String, Integer, Boolean>() {
            protected Boolean doInBackground(String... params) {
                try {
                    Thread.sleep((long) (second * 1000));
                } catch (Exception var3) {
                    ;
                }
                return null;
            }

            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                Intent datatIntent = new Intent(context, targetClass);
                datatIntent.setFlags(335544320);
                if (datas != null) {
                    for (int i = 0; i < datas.length; ++i) {
                        datatIntent.putExtra("data" + i, datas[i]);
                    }
                }

                context.startActivity(datatIntent);
            }
        }).execute(new String[]{""});
    }

    public static void jumpToActivityForResult(Context context, Class<? extends Activity> targetClass, int resultId, String... datas) {
        Intent datatIntent = new Intent(context, targetClass);
        if (datas != null) {
            for (int i = 0; i < datas.length; ++i) {
                datatIntent.putExtra("data" + i, datas[i]);
            }
        }

        ((Activity) context).startActivityForResult(datatIntent, resultId);
    }

    public static void jumpToActivityForResult(Context context, Class<? extends Activity> targetClass, int resultId) {
        Intent datatIntent = new Intent(context, targetClass);
        ((Activity) context).startActivityForResult(datatIntent, resultId);
    }

    public static void jumpToSystemSMSActivity(Context context, String number) {
        Intent mIntent = new Intent("android.intent.action.VIEW");
        mIntent.putExtra("address", number);
        mIntent.setType("vnd.android-dir/mms-sms");
        context.startActivity(mIntent);
    }

    public static void jumpToActivity(Context context, ComponentName componentName) {
        Intent mIntent = new Intent();
        mIntent.addFlags(268435456);
        mIntent.setComponent(componentName);
        mIntent.setAction("android.intent.action.VIEW");
        context.startActivity(mIntent);
    }

    public static void jumpToActivity(Context context, ComponentName componentName, String... datas) {
        Intent mIntent = new Intent();
        mIntent.addFlags(268435456);
        mIntent.setComponent(componentName);
        mIntent.setAction("android.intent.action.VIEW");
        if (datas != null) {
            for (int i = 0; i < datas.length; ++i) {
                mIntent.putExtra("data" + i, datas[i]);
            }
        }

        context.startActivity(mIntent);
    }

    public static void jumpToHomeActivity(Context context) {
        Intent mHomeIntent = new Intent("android.intent.action.MAIN");
        mHomeIntent.addCategory("android.intent.category.HOME");
        mHomeIntent.addFlags(270532608);
        context.startActivity(mHomeIntent);
    }

    public static void jumpToNetworkSettingActivity(Context context) {
        Intent intent = null;

        try {
            if (JAndroidUtil.isHoneycombOrHigher()) {
                intent = new Intent("android.settings.WIRELESS_SETTINGS");
            } else {
                intent = new Intent();
                ComponentName e = new ComponentName("com.android.settings", "com.android.settings.WirelessSettings");
                intent.setComponent(e);
                intent.setAction("android.intent.action.VIEW");
            }

            context.startActivity(intent);
        } catch (Exception var3) {
            Log.w(TAG, "open network settings failed, please check...");
            var3.printStackTrace();
        }

    }

    public static void jumpToSystemLocPickImageActivity(Activity activity, int requestCode) {
        Intent intent = null;
        intent = new Intent();
        intent.setType("image/*");
        intent.setAction("android.intent.action.GET_CONTENT");
        activity.startActivityForResult(intent, requestCode);
    }

    public static void jumpToSystemCameraPickImageActivity(Activity activity, int requestCode) {
        Intent intent = null;
        intent = new Intent("android.media.action.IMAGE_CAPTURE");
        activity.startActivityForResult(intent, requestCode);
    }

    public static void jumpToSystemCallActivity(Context context, String number) {
        Intent i = new Intent();
        i.setAction("android.intent.action.CALL");
        i.setData(Uri.parse("tel:" + number));
        i.addFlags(268435456);
        context.startActivity(i);
    }

    public static void jumpToSystemInstallApkActivity(Context context, String apkPath) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setDataAndType(Uri.fromFile(new File(apkPath)), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    public static void jumpToSystemDownloadApk(Context context, String url) {
        Intent intent = new Intent("android.intent.action.VIEW");
        Uri data = Uri.parse(Html.fromHtml(url).toString());
        intent.setData(data);
        intent.setPackage("com.google.android.browser");
        intent.addCategory("android.intent.category.BROWSABLE");
        intent.setComponent(new ComponentName("com.android.browser", "com.android.browser.BrowserActivity"));
        context.startActivity(intent);
    }

    public static void jumpToSystemShareText(Context context, String content) {
        Intent sendIntent = new Intent();
        sendIntent.setAction("android.intent.action.SEND");
        sendIntent.putExtra("android.intent.extra.TEXT", content);
        sendIntent.setType("text/plain");
        context.startActivity(sendIntent);
    }

    public static void jumpToSystemShareImage(Context context, String imageUri) {
        Intent shareIntent = new Intent();
        shareIntent.setAction("android.intent.action.SEND");
        shareIntent.putExtra("android.intent.extra.STREAM", imageUri);
        shareIntent.setType("image/*");
        context.startActivity(shareIntent);
    }

    public static void jumpToSystemShareImages(Context context, ArrayList<Uri> imageUris) {
        Intent shareIntent = new Intent();
        shareIntent.setAction("android.intent.action.SEND_MULTIPLE");
        shareIntent.putParcelableArrayListExtra("android.intent.extra.STREAM", imageUris);
        shareIntent.setType("image/*");
        context.startActivity(shareIntent);
    }
}
