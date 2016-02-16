package com.clj.jaf.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Base64;

import com.clj.jaf.utils.JReflecterUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;


public class JPreferenceConfig {
    private Context mContext;
    private SharedPreferences.Editor edit = null;
    private SharedPreferences mSharedPreferences;
    private String mFilename = JPreferenceConfig.class.getSimpleName();
    private Boolean isLoad = false;
    private static JPreferenceConfig mThisConfig;

    private JPreferenceConfig() {
    }

    public void initConfig(Context context) {
        this.mContext = context;
        this.loadConfig();
    }

    public void initConfig() {
    }

    public void release() {
    }

    public static JPreferenceConfig getInstance() {
        if (mThisConfig == null) {
            mThisConfig = new JPreferenceConfig();
        }

        return mThisConfig;
    }

    private void loadConfig() {
        try {
            this.mSharedPreferences = this.mContext.getSharedPreferences(this.mFilename, 2);
            this.edit = this.mSharedPreferences.edit();
            this.isLoad = true;
        } catch (Exception var2) {
            this.isLoad = false;
        }

    }

    public Boolean isLoadConfig() {
        return this.isLoad;
    }

    public void close() {
    }

    public void setString(String key, String value) {
        this.edit.putString(key, value);
        this.edit.commit();
    }

    public void setInt(String key, int value) {
        this.edit.putInt(key, value);
        this.edit.commit();
    }

    public void setBoolean(String key, Boolean value) {
        this.edit.putBoolean(key, value);
        this.edit.commit();
    }

    public void setByte(String key, byte[] value) {
        this.setString(key, String.valueOf(value));
    }

    public void setShort(String key, short value) {
        this.setString(key, String.valueOf(value));
    }

    public void setLong(String key, long value) {
        this.edit.putLong(key, value);
        this.edit.commit();
    }

    public void setFloat(String key, float value) {
        this.edit.putFloat(key, value);
        this.edit.commit();
    }

    public void setDouble(String key, double value) {
        this.setString(key, String.valueOf(value));
    }

    public void setObject(String key, Object value) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(value);

            String stringBase64 = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
            this.edit.putString(key, stringBase64);
            this.edit.commit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setString(int resID, String value) {
        this.setString(this.mContext.getString(resID), value);
    }

    public void setInt(int resID, int value) {
        this.setInt(this.mContext.getString(resID), value);
    }

    public void setBoolean(int resID, Boolean value) {
        this.setBoolean(this.mContext.getString(resID), value);
    }

    public void setByte(int resID, byte[] value) {
        this.setByte(this.mContext.getString(resID), value);
    }

    public void setShort(int resID, short value) {
        this.setShort(this.mContext.getString(resID), value);
    }

    public void setLong(int resID, long value) {
        this.setLong(this.mContext.getString(resID), value);
    }

    public void setFloat(int resID, float value) {
        this.setFloat(this.mContext.getString(resID), value);
    }

    public void setDouble(int resID, double value) {
        this.setDouble(this.mContext.getString(resID), value);
    }

    public String getString(String key, String defaultValue) {
        return this.mSharedPreferences.getString(key, defaultValue);
    }

    public int getInt(String key, int defaultValue) {
        return this.mSharedPreferences.getInt(key, defaultValue);
    }

    public boolean getBoolean(String key, Boolean defaultValue) {
        return this.mSharedPreferences.getBoolean(key, defaultValue);
    }

    public byte[] getByte(String key, byte[] defaultValue) {
        try {
            return this.getString(key, "").getBytes();
        } catch (Exception var4) {
            return defaultValue;
        }
    }

    public short getShort(String key, Short defaultValue) {
        try {
            return Short.valueOf(this.getString(key, ""));
        } catch (Exception var4) {
            return defaultValue;
        }
    }

    public long getLong(String key, Long defaultValue) {
        return this.mSharedPreferences.getLong(key, defaultValue);
    }

    public float getFloat(String key, Float defaultValue) {
        return this.mSharedPreferences.getFloat(key, defaultValue);
    }

    public double getDouble(String key, Double defaultValue) {
        try {
            return Double.valueOf(this.getString(key, ""));
        } catch (Exception var4) {
            return defaultValue;
        }
    }

    public String getString(int resID, String defaultValue) {
        return this.getString(this.mContext.getString(resID), defaultValue);
    }

    public int getInt(int resID, int defaultValue) {
        return this.getInt(this.mContext.getString(resID), defaultValue);
    }

    public boolean getBoolean(int resID, Boolean defaultValue) {
        return this.getBoolean(this.mContext.getString(resID), defaultValue);
    }

    public byte[] getByte(int resID, byte[] defaultValue) {
        return this.getByte(this.mContext.getString(resID), defaultValue);
    }

    public short getShort(int resID, Short defaultValue) {
        return this.getShort(this.mContext.getString(resID), defaultValue);
    }

    public long getLong(int resID, Long defaultValue) {
        return this.getLong(this.mContext.getString(resID), defaultValue);
    }

    public float getFloat(int resID, Float defaultValue) {
        return this.getFloat(this.mContext.getString(resID), defaultValue);
    }

    public double getDouble(int resID, Double defaultValue) {
        return this.getDouble(this.mContext.getString(resID), defaultValue);
    }

    public Object getObject(String key) {
        try {
            String stringBase64 = this.getString(key, "");
            if (TextUtils.isEmpty(stringBase64))
                return null;

            byte[] base64Bytes = Base64.decode(stringBase64.getBytes(), Base64.DEFAULT);
            ByteArrayInputStream bais = new ByteArrayInputStream(base64Bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            return ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setConfig(Object entity) {
        Class clazz = entity.getClass();
        Field[] fields = clazz.getDeclaredFields();
        Field[] var7 = fields;
        int var6 = fields.length;

        for (int var5 = 0; var5 < var6; ++var5) {
            Field field = var7[var5];
            if (!JReflecterUtil.isTransient(field) && JReflecterUtil.isBaseDateType(field)) {
                String columnName = JReflecterUtil.getFieldName(field);
                field.setAccessible(true);
                this.setValue(field, columnName, entity);
            }
        }

    }

    private void setValue(Field field, String columnName, Object entity) {
        try {
            Class e = field.getType();
            if (e.equals(String.class)) {
                this.setString(columnName, (String) field.get(entity));
            } else if (!e.equals(Integer.class) && !e.equals(Integer.TYPE)) {
                if (!e.equals(Float.class) && !e.equals(Float.TYPE)) {
                    if (!e.equals(Double.class) && !e.equals(Double.TYPE)) {
                        if (!e.equals(Short.class) && !e.equals(Short.class)) {
                            if (!e.equals(Long.class) && !e.equals(Long.TYPE)) {
                                if (e.equals(Boolean.class)) {
                                    this.setBoolean(columnName, (Boolean) field.get(entity));
                                }
                            } else {
                                this.setLong(columnName, (Long) field.get(entity));
                            }
                        } else {
                            this.setShort(columnName, (Short) field.get(entity));
                        }
                    } else {
                        this.setDouble(columnName, (Double) field.get(entity));
                    }
                } else {
                    this.setFloat(columnName, (Float) field.get(entity));
                }
            } else {
                this.setInt(columnName, (Integer) field.get(entity));
            }
        } catch (IllegalArgumentException | IllegalAccessException var5) {
            var5.printStackTrace();
        }

    }

    public <T> T getConfig(Class<T> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        Object entity = null;

        try {
            entity = clazz.newInstance();
            Field[] var7 = fields;
            int var6 = fields.length;

            for (int var5 = 0; var5 < var6; ++var5) {
                Field e = var7[var5];
                e.setAccessible(true);
                if (!JReflecterUtil.isTransient(e) && JReflecterUtil.isBaseDateType(e)) {
                    String columnName = JReflecterUtil.getFieldName(e);
                    e.setAccessible(true);
                    this.getValue(e, columnName, entity);
                }
            }
        } catch (InstantiationException var9) {
            var9.printStackTrace();
        } catch (IllegalAccessException var10) {
            var10.printStackTrace();
        }

        return (T) entity;
    }

    private <T> void getValue(Field field, String columnName, T entity) {
        try {
            Class e = field.getType();
            if (e.equals(String.class)) {
                field.set(entity, this.getString(columnName, ""));
            } else if (!e.equals(Integer.class) && !e.equals(Integer.TYPE)) {
                if (!e.equals(Float.class) && !e.equals(Float.TYPE)) {
                    if (!e.equals(Double.class) && !e.equals(Double.TYPE)) {
                        if (!e.equals(Short.class) && !e.equals(Short.class)) {
                            if (!e.equals(Long.class) && !e.equals(Long.TYPE)) {
                                if (!e.equals(Byte.class) && !e.equals(Byte.TYPE)) {
                                    if (e.equals(Boolean.class)) {
                                        field.set(entity, this.getBoolean(columnName, false));
                                    }
                                } else {
                                    field.set(entity, this.getByte(columnName, new byte[8]));
                                }
                            } else {
                                field.set(entity, this.getLong(columnName, 0L));
                            }
                        } else {
                            field.set(entity, this.getShort(columnName, (short) 0));
                        }
                    } else {
                        field.set(entity, this.getDouble(columnName, 0.0D));
                    }
                } else {
                    field.set(entity, this.getFloat(columnName, 0.0F));
                }
            } else {
                field.set(entity, this.getInt(columnName, 0));
            }
        } catch (IllegalArgumentException | IllegalAccessException var5) {
            var5.printStackTrace();
        }

    }

    public void remove(String key) {
        this.edit.remove(key);
        this.edit.commit();
    }

    public void remove(String... keys) {
        String[] var5 = keys;
        int var4 = keys.length;

        for (int var3 = 0; var3 < var4; ++var3) {
            String key = var5[var3];
            this.remove(key);
        }

    }

    public void removeAll() {
        try {
            Map values = this.mSharedPreferences.getAll();
            Iterator var3 = values.keySet().iterator();

            while (var3.hasNext()) {
                String key = (String) var3.next();
                this.edit.remove(key);
            }
        } catch (Exception var4) {

        }

        this.edit.commit();
    }

    public void clear() {
        this.edit.clear();
        this.edit.commit();
    }

    public void open() {
    }
}
