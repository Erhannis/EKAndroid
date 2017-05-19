package com.erhannis.android.ekandroid;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;

import dalvik.system.DexFile;
import dalvik.system.PathClassLoader;

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

    List<Class> scan() throws IOException, ClassNotFoundException, NoSuchMethodException {
        long timeBegin = System.currentTimeMillis();

        PathClassLoader classLoader = (PathClassLoader) getContext().getClassLoader();
        //PathClassLoader classLoader = (PathClassLoader) Thread.currentThread().getContextClassLoader();//This also works good
        DexFile dexFile = new DexFile(getContext().getPackageCodePath());
        Enumeration<String> classNames = dexFile.entries();
        ArrayList<Class> result = new ArrayList<Class>();
        while (classNames.hasMoreElements()) {
            String className = classNames.nextElement();
            if (isTargetClassName(className)) {
                //Class<?> aClass = Class.forName(className);//java.lang.ExceptionInInitializerError
                try {
                    Class<?> aClass = Class.forName(className, false, classLoader);//tested on 魅蓝Note(M463C)_Android4.4.4 and Mi2s_Android5.1.1
                    //Class<?> aClass = classLoader.loadClass(className);//tested on 魅蓝Note(M463C)_Android4.4.4 and Mi2s_Android5.1.1
                    if (isTargetClass(aClass)) {
                        result.add(aClass);
                    }
                } catch (ClassNotFoundException e) {
                    System.err.println("Couldn't find class " + className);
                }
            }
        }

        long timeEnd = System.currentTimeMillis();
        long timeElapsed = timeEnd - timeBegin;
        Log.d(TAG, "scan() cost " + timeElapsed + "ms");
        return result;
    }

    protected abstract boolean isTargetClassName(String className);

    protected abstract boolean isTargetClass(Class clazz);

    //// Implementations

    /**
     *
     * @param context
     * @param parentClass
     * @param classNameRegex May be null to allow all
     * @return
     * @throws NoSuchMethodException
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static List<Class> getConcreteDescendants(Context context, final Class<?> parentClass, String classNameRegex) throws NoSuchMethodException, IOException, ClassNotFoundException {
        final Pattern classNamePattern;
        if (classNameRegex != null) {
            classNamePattern = Pattern.compile(classNameRegex);
        } else {
            classNamePattern = null;
        }
        return new ClassScanner(context) {

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

            /*
            @Override
            protected void onScanResult(Class clazz) {
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
            }
            */
        }.scan();
    }
}