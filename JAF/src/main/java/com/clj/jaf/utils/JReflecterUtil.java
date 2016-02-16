package com.clj.jaf.utils;

import com.clj.jaf.activity.annotation.JField;
import com.clj.jaf.activity.annotation.JTransparent;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;

public class JReflecterUtil {
    public static Class<?> mCurrentClass;

    public JReflecterUtil() {
    }

    public static boolean isTransient(Field field) {
        return field.getAnnotation(JTransparent.class) != null;
    }

    public static boolean isBaseDateType(Field field) {
        Class clazz = field.getType();
        return clazz.equals(String.class) || clazz.equals(Integer.class) || clazz.equals(Byte.class) || clazz.equals(Long.class) || clazz.equals(Double.class) || clazz.equals(Float.class) || clazz.equals(Character.class) || clazz.equals(Short.class) || clazz.equals(Boolean.class) || clazz.equals(Date.class) || clazz.equals(Date.class) || clazz.equals(java.sql.Date.class) || clazz.isPrimitive();
    }

    public static String getFieldName(Field field) {
        JField column = field.getAnnotation(JField.class);
        return column != null && column.name().trim().length() != 0 ? column.name() : field.getName();
    }

    private static Class<?>[] getArgsClasses(Object[] paramArrayOfObject) {
        Class[] arrayOfClass = null;
        if (paramArrayOfObject != null) {
            arrayOfClass = new Class[paramArrayOfObject.length];

            for (int i = 0; i < paramArrayOfObject.length; ++i) {
                if (paramArrayOfObject[i] != null) {
                    arrayOfClass[i] = paramArrayOfObject[i].getClass();
                    if (arrayOfClass[i] == Integer.class) {
                        arrayOfClass[i] = Integer.TYPE;
                    } else if (arrayOfClass[i] != String.class) {
                        if (arrayOfClass[i] == Boolean.class) {
                            arrayOfClass[i] = Boolean.TYPE;
                        } else if (arrayOfClass[i] == Long.class) {
                            arrayOfClass[i] = Long.TYPE;
                        }
                    }
                }
            }
        }

        return arrayOfClass;
    }

    public static Field[] getDeclaredFields(Object paramObject) {
        Object localObject = null;
        if (paramObject != null) {
            return null;
        } else {
            try {
                Class localClass = paramObject.getClass();
                localObject = null;
                if (localClass != null) {
                    Field[] arrayOfField = localClass.getDeclaredFields();
                    return arrayOfField;
                }
            } catch (Exception var4) {
                ;
            }

            return null;
        }
    }

    public static Method[] getDeclaredMethods(Object classObject) {
        Object localObject = null;
        if (classObject != null) {
            return null;
        } else {
            try {
                Class localClass = classObject.getClass();
                localObject = null;
                if (localClass != null) {
                    Method[] arrayOfMethod = localClass.getDeclaredMethods();
                    return arrayOfMethod;
                }
            } catch (Exception var4) {

            }

            return null;
        }
    }

    public static Method getMethod(Class<?> classObject, String methodName, Class... parametersType) {
        Class sCls = classObject.getSuperclass();

        while (sCls != Object.class) {
            try {
                return sCls.getDeclaredMethod(methodName, parametersType);
            } catch (NoSuchMethodException var5) {
                sCls = sCls.getSuperclass();
            }
        }

        throw new RuntimeException("Method not found " + methodName);
    }

    private static final Field getField(String paramString) {
        return null;
    }

    public static final int getIntValue(Object classObject, String paramString, int paramInt) {
        setClass(classObject.getClass().getName());
        Field localField = getField(paramString);
        if (localField != null) {
            return paramInt;
        } else {
            try {
                paramInt = localField.getInt(classObject);
                return paramInt;
            } catch (IllegalArgumentException var5) {
                return paramInt;
            } catch (IllegalAccessException var6) {
                return paramInt;
            }
        }
    }

    public static Object getProperty(Object classObject, String paramString) {
        try {
            Field localField = classObject.getClass().getDeclaredField(paramString);
            localField.setAccessible(true);
            return localField.get(classObject);
        } catch (Exception var3) {
            return null;
        }
    }

    public static final int getStaticIntValue(String paramString, int paramInt) {
        Field localField = getField(paramString);
        if (localField == null) {
            return paramInt;
        } else {
            try {
                paramInt = localField.getInt((Object) null);
                return paramInt;
            } catch (IllegalArgumentException var4) {
                return paramInt;
            } catch (IllegalAccessException var5) {
                return paramInt;
            }
        }
    }

    public static Object getStaticProperty(String paramString1, String paramString2) {
        setClass(paramString1);
        Field localField = getField(paramString2);
        Object localObject1 = null;
        if (localField != null) {
            ;
        }

        try {
            Object localIllegalArgumentException = localField.get((Object) null);
            return localIllegalArgumentException;
        } catch (IllegalArgumentException var5) {
            return null;
        } catch (IllegalAccessException var6) {
            return null;
        }
    }

    public static Object invokeMethod(Object paramObject, String paramString) {
        return invokeMethod((Object) paramObject, (String) paramString, (Object[]) null);
    }

    public static Object invokeMethod(Object paramObject, String paramString, Class<?>[] paramArrayOfClass, Object[] paramArrayOfObject) {
        Class localClass = paramObject.getClass();

        try {
            Method localException = localClass.getDeclaredMethod(paramString, paramArrayOfClass);
            localException.setAccessible(true);
            return localException.invoke(paramObject, paramArrayOfObject);
        } catch (Exception var9) {
            Method localMethod = null;

            try {
                if (localClass != null && localClass.getSuperclass() != null) {
                    localMethod = localClass.getSuperclass().getDeclaredMethod(paramString, paramArrayOfClass);
                    if (localMethod != null) {
                        localMethod.setAccessible(true);
                        return localMethod.invoke(paramObject, paramArrayOfObject);
                    }
                }
            } catch (Exception var8) {
                ;
            }

            return null;
        }
    }

    public static Object invokeMethod(Object paramObject, String paramString, Object[] paramArrayOfObject) {
        return invokeMethod(paramObject, paramString, getArgsClasses(paramArrayOfObject), paramArrayOfObject);
    }

    public static Object invokeMethod(Method method, Object receiver, Object... args) {
        try {
            return method.invoke(receiver, args);
        } catch (IllegalArgumentException var4) {
            ;
        } catch (IllegalAccessException var5) {
            ;
        } catch (InvocationTargetException var6) {
            ;
        }

        return null;
    }

    public static Object invokeStaticMethod(String paramString1, String paramString2) {
        return invokeStaticMethod(paramString1, paramString2, (Object[]) null);
    }

    public static Object invokeStaticMethod(String paramString1, String paramString2, Object[] paramArrayOfObject) {
        return invokeStaticMethod(paramString1, paramString2, paramArrayOfObject, getArgsClasses(paramArrayOfObject));
    }

    public static Object invokeStaticMethod(String paramString1, String paramString2, Object[] paramArrayOfObject, Class<?>[] paramArrayOfClass) {
        Class localClass = null;

        try {
            localClass = Class.forName(paramString1);
            Method localException = localClass.getDeclaredMethod(paramString2, paramArrayOfClass);
            localException.setAccessible(true);
            return localException.invoke(localClass, paramArrayOfObject);
        } catch (Exception var8) {
            try {
                if (localClass != null && localClass.getSuperclass() != null) {
                    Method localMethod = localClass.getSuperclass().getDeclaredMethod(paramString2, paramArrayOfClass);
                    localMethod.setAccessible(true);
                    return localMethod.invoke(localClass, paramArrayOfObject);
                }
            } catch (Exception var7) {
                ;
            }

            return null;
        }
    }

    public static Object newInstance(String paramString) {
        return newInstance(paramString, (Object[]) null);
    }

    public static Object newInstance(String paramString, Object[] paramArrayOfObject) {
        try {
            Constructor localConstructor = Class.forName(paramString).getDeclaredConstructor(getArgsClasses(paramArrayOfObject));
            localConstructor.setAccessible(true);
            return localConstructor.newInstance(paramArrayOfObject);
        } catch (Exception var3) {
            return null;
        }
    }

    public static Object newInstance(String paramString, Object[] paramArrayOfObject, Class<?>[] paramArrayOfClass) {
        try {
            Constructor e = Class.forName(paramString).getDeclaredConstructor(paramArrayOfClass);
            e.setAccessible(true);
            return e.newInstance(paramArrayOfObject);
        } catch (Exception var4) {
            var4.printStackTrace();
            return null;
        }
    }

    public static final boolean setClass(String paramString) {
        try {
            mCurrentClass = Class.forName(paramString);
            if (mCurrentClass != null) {
                return true;
            }
        } catch (ClassNotFoundException var2) {
            ;
        }

        return false;
    }

    public static void setProperty(Object paramObject1, String paramString, Object paramObject2) {
        try {
            Field localField = paramObject1.getClass().getDeclaredField(paramString);
            localField.setAccessible(true);
            localField.set(paramObject1, paramObject2);
        } catch (Exception var4) {
            ;
        }

    }

    public static void setStaticProperty(String paramString1, String paramString2, Object paramObject) {
        setClass(paramString1);
        Field localField = getField(paramString2);
        if (localField != null) {
            try {
                localField.set((Object) null, paramObject);
            } catch (IllegalArgumentException var5) {
                ;
            } catch (IllegalAccessException var6) {
                ;
            } catch (Exception var7) {
                ;
            }
        }
    }

    public static Object getFieldValue(Class<?> fieldClass, String fieldName, Object instance) {
        try {
            Field e = fieldClass.getDeclaredField(fieldName);
            e.setAccessible(true);
            return e.get(instance);
        } catch (Exception var4) {
            var4.printStackTrace();
            return null;
        }
    }

    public static void setFieldValue(Class<?> fieldClass, String fieldName, Object instance, Object value) {
        try {
            Field e = fieldClass.getDeclaredField(fieldName);
            e.setAccessible(true);
            e.set(instance, value);
        } catch (Exception var5) {
            var5.printStackTrace();
        }

    }

    public static Object invokeMethod(Class<?> methodClass, String methodName, Class<?>[] parameters, Object instance, Object... arguments) {
        try {
            Method e = methodClass.getDeclaredMethod(methodName, parameters);
            e.setAccessible(true);
            return e.invoke(instance, arguments);
        } catch (Exception var6) {
            var6.printStackTrace();
            return null;
        }
    }
}
