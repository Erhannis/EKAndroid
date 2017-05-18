package com.erhannis.android.ekandroid;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Enumeration;
import java.util.regex.Pattern;

import dalvik.system.DexFile;
import dalvik.system.PathClassLoader;
import java8.util.function.Consumer;

/**
 * http://stackoverflow.com/users/1632448/fantouch
 * http://stackoverflow.com/a/31087947/513038
 */
public abstract class ClassScanner {
    private static final String TAG = "ClassScanner";
    private Context mContext;

    public ClassScanner(Context context) {
        mContext = context;
    }

    public Context getContext() {
        return mContext;
    }

    void scan() throws IOException, ClassNotFoundException, NoSuchMethodException {
        long timeBegin = System.currentTimeMillis();

        PathClassLoader classLoader = (PathClassLoader) getContext().getClassLoader();
        //PathClassLoader classLoader = (PathClassLoader) Thread.currentThread().getContextClassLoader();//This also works good
        DexFile dexFile = new DexFile(getContext().getPackageCodePath());
        Enumeration<String> classNames = dexFile.entries();
        while (classNames.hasMoreElements()) {
            String className = classNames.nextElement();
            if (isTargetClassName(className)) {
                //Class<?> aClass = Class.forName(className);//java.lang.ExceptionInInitializerError
                //Class<?> aClass = Class.forName(className, false, classLoader);//tested on 魅蓝Note(M463C)_Android4.4.4 and Mi2s_Android5.1.1
                Class<?> aClass = classLoader.loadClass(className);//tested on 魅蓝Note(M463C)_Android4.4.4 and Mi2s_Android5.1.1
                if (isTargetClass(aClass)) {
                    onScanResult(aClass);
                }
            }
        }

        long timeEnd = System.currentTimeMillis();
        long timeElapsed = timeEnd - timeBegin;
        Log.d(TAG, "scan() cost " + timeElapsed + "ms");
    }

    protected abstract boolean isTargetClassName(String className);

    protected abstract boolean isTargetClass(Class clazz);

    protected abstract void onScanResult(Class clazz);

    //// Implementations

    public static void getConcreteDescendants(Context context, final Class<?> parentClass, String classNameRegex, final Consumer<Class> callback) throws NoSuchMethodException, IOException, ClassNotFoundException {
        final Pattern classNamePattern;
        if (classNameRegex != null) {
            classNamePattern = Pattern.compile(classNameRegex);
        } else {
            classNamePattern = null;
        }
        new ClassScanner(context) {

            @Override
            protected boolean isTargetClassName(String className) {
                //TODO Could skip "non-static inner classes"
                if (classNamePattern == null) {
                    return true;
                }
                return classNamePattern.matcher(className).matches();
//        return className.startsWith(getContext().getPackageName())//I want classes under my package
//                && !className.contains("$");//I don't need none-static inner classes
            }

            @Override
            protected boolean isTargetClass(Class clazz) {
                return parentClass.isAssignableFrom(clazz)//I want subclasses of AbsFactory
                        && !Modifier.isAbstract(clazz.getModifiers());//I don't want abstract classes
            }

            @Override
            protected void onScanResult(Class clazz) {
                callback.accept(clazz);
                /*
                Constructor constructor = null;
                try {
                    constructor = clazz.getDeclaredConstructor();
                    constructor.setAccessible(true);
                    constructor.newInstance();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                */
            }
        }.scan();
    }
}