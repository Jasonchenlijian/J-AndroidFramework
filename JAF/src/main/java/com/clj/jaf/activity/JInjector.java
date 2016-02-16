package com.clj.jaf.activity;

import android.app.Activity;
import android.content.res.Resources;

import com.clj.jaf.activity.annotation.JInject;
import com.clj.jaf.activity.annotation.JInjectResource;
import com.clj.jaf.activity.annotation.JInjectView;

import java.lang.reflect.Field;

public class JInjector {
    private static JInjector instance;

    private JInjector() {
    }

    public static JInjector getInstance() {
        if (instance == null) {
            instance = new JInjector();
        }

        return instance;
    }

    public void inJectAll(Activity activity) {
        Field[] fields = activity.getClass().getDeclaredFields();
        if (fields != null && fields.length > 0) {
            Field[] var6 = fields;
            int var5 = fields.length;

            for (int var4 = 0; var4 < var5; ++var4) {
                Field field = var6[var4];
                if (field.isAnnotationPresent(JInjectView.class)) {
                    this.injectView(activity, field);
                } else if (field.isAnnotationPresent(JInjectResource.class)) {
                    this.injectResource(activity, field);
                } else if (field.isAnnotationPresent(JInject.class)) {
                    this.inject(activity, field);
                }
            }
        }

    }

    private void inject(Activity activity, Field field) {
        try {
            field.setAccessible(true);
            field.set(activity, field.getType().newInstance());
        } catch (Exception var4) {
            var4.printStackTrace();
        }

    }

    private void injectView(Activity activity, Field field) {
        if (field.isAnnotationPresent(JInjectView.class)) {
            JInjectView viewInject = (JInjectView) field.getAnnotation(JInjectView.class);
            int viewId = viewInject.id();

            try {
                field.setAccessible(true);
                field.set(activity, activity.findViewById(viewId));
            } catch (Exception var6) {
                var6.printStackTrace();
            }
        }

    }

    private void injectResource(Activity activity, Field field) {
        if (field.isAnnotationPresent(JInjectResource.class)) {
            JInjectResource resourceJect = (JInjectResource) field.getAnnotation(JInjectResource.class);
            int resourceID = resourceJect.id();

            try {
                field.setAccessible(true);
                Resources e = activity.getResources();
                String type = e.getResourceTypeName(resourceID);
                if (type.equalsIgnoreCase("string")) {
                    field.set(activity, activity.getResources().getString(resourceID));
                } else if (type.equalsIgnoreCase("drawable")) {
                    field.set(activity, activity.getResources().getDrawable(resourceID));
                } else if (type.equalsIgnoreCase("layout")) {
                    field.set(activity, activity.getResources().getLayout(resourceID));
                } else if (type.equalsIgnoreCase("array")) {
                    if (field.getType().equals(int[].class)) {
                        field.set(activity, activity.getResources().getIntArray(resourceID));
                    } else if (field.getType().equals(String[].class)) {
                        field.set(activity, activity.getResources().getStringArray(resourceID));
                    } else {
                        field.set(activity, activity.getResources().getStringArray(resourceID));
                    }
                } else if (type.equalsIgnoreCase("color")) {
                    if (field.getType().equals(Integer.TYPE)) {
                        field.set(activity, activity.getResources().getColor(resourceID));
                    } else {
                        field.set(activity, activity.getResources().getColorStateList(resourceID));
                    }
                }
            } catch (Exception var7) {
                var7.printStackTrace();
            }
        }

    }

    public void inject(Activity activity) {
        Field[] fields = activity.getClass().getDeclaredFields();
        if (fields != null && fields.length > 0) {
            Field[] var6 = fields;
            int var5 = fields.length;

            for (int var4 = 0; var4 < var5; ++var4) {
                Field field = var6[var4];
                if (field.isAnnotationPresent(JInject.class)) {
                    this.inject(activity, field);
                }
            }
        }

    }

    public void injectView(Activity activity) {
        Field[] fields = activity.getClass().getDeclaredFields();
        if (fields != null && fields.length > 0) {
            Field[] var6 = fields;
            int var5 = fields.length;

            for (int var4 = 0; var4 < var5; ++var4) {
                Field field = var6[var4];
                if (field.isAnnotationPresent(JInjectView.class)) {
                    this.injectView(activity, field);
                }
            }
        }

    }

    public void injectResource(Activity activity) {
        Field[] fields = activity.getClass().getDeclaredFields();
        if (fields != null && fields.length > 0) {
            Field[] var6 = fields;
            int var5 = fields.length;

            for (int var4 = 0; var4 < var5; ++var4) {
                Field field = var6[var4];
                if (field.isAnnotationPresent(JInjectResource.class)) {
                    this.injectResource(activity, field);
                }
            }
        }

    }
}
