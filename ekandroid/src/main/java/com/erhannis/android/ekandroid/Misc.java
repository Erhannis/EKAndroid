package com.erhannis.android.ekandroid;

import android.os.Handler;
import android.os.Looper;
import java.lang.Runnable;

public class Misc {
    public static void runOnUiThread(final Runnable r) {
        new Handler(Looper.getMainLooper()).post(() -> {
            r.run();
        });
    }
}
