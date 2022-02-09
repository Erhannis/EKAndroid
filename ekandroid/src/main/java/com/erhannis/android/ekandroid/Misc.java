package com.erhannis.android.ekandroid;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import java.lang.Runnable;
import java.util.concurrent.SynchronousQueue;
import java.util.function.Consumer;

public class Misc {
    public static Handler startHandlerThread() {
        SynchronousQueue<Handler> result = new SynchronousQueue<>();
        new Thread(() -> {
            Looper.prepare();
            Handler h = new Handler(Looper.myLooper());
            result.add(h);
            Looper.loop();
        }).start();
        try {
            return result.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e); // I don't want to deal with it
        }
    }

    public static boolean isMainThread() {
        return (Looper.myLooper() == Looper.getMainLooper());
    }

    public static void runOnUiThread(final Runnable r) {
        new Handler(Looper.getMainLooper()).post(() -> {
            r.run();
        });
    }

    public static void showToast(final Activity ctx, final String text) {
        ctx.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ctx, text, Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void test() {
        Consumer<String> s = x -> {
            System.out.println("TEST: " + x);
        };
        s.accept("asdf");
    }
}
