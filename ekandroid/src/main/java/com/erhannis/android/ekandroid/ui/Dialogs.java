package com.erhannis.android.ekandroid.ui;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.InputType;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.annotation.RequiresApi;

import com.erhannis.android.ekandroid.Misc;

import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

public class Dialogs {
    /**
     * Shows the user a confirmation dialog.  Blocking.
     * //TODO Currently requires draw overlay.  Have backup method?
     * @param ctx
     * @param title
     * @param msg
     * @return
     */
    public static boolean confirmDialog(Context ctx, String title, String msg) {
        boolean[] result = new boolean[1];
        CountDownLatch cdl = new CountDownLatch(1);
        String msg0 = msg;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    checkDrawOverlayPermission(ctx);
                }

                System.err.println("//TODO Maybe a notification instead");
                AlertDialog ad = new AlertDialog.Builder(ctx)
                        .setMessage(msg0)
                        .setTitle(title)
                        .setCancelable(false)
                        .setNegativeButton("No", (dialog, which) -> {
                            result[0] = false;
                            cdl.countDown();
                        })
                        .setPositiveButton("Yes", (dialog, which) -> {
                            result[0] = true;
                            cdl.countDown();
                        })
                        .create();

                int LAYOUT_FLAG;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
                } else {
                    LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
                }

                //ad.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                ad.getWindow().setType(LAYOUT_FLAG);
                ad.show();
            }
        });
        try {
            cdl.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result[0];
    }

    /**
     * Show the user a notification dialog.  Blocking.
     * //TODO Currently requires draw overlay.  Have backup method?  Parameterize?
     * @param ctx
     * @param title
     * @param msg
     */
    public static void notifyDialog(Context ctx, String title, String msg) {
        //TODO This may not need to be blocking
        CountDownLatch cdl = new CountDownLatch(1);
        String msg0 = msg;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    checkDrawOverlayPermission(ctx);
                }

                System.err.println("//TODO Maybe a notification instead");
                AlertDialog ad = new AlertDialog.Builder(ctx)
                        .setMessage(msg0)
                        .setTitle(title)
                        .setCancelable(true)
                        .setNeutralButton("OK", (dialog, which) -> {
                            cdl.countDown();
                        })
                        .create();

                int LAYOUT_FLAG;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
                } else {
                    LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
                }

                //ad.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                ad.getWindow().setType(LAYOUT_FLAG);
                ad.show();
            }
        });
        try {
            cdl.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void checkDrawOverlayPermission(Context ctx) {
        /** check if we already have permission to draw over other apps */
        if (!Settings.canDrawOverlays(ctx.getApplicationContext())) {
            /** if not construct intent to request permission */
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + ctx.getPackageName()));
            intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
            /** request permission via start activity for result */
            ctx.startActivity(intent);
        }
    }

    /**
     * DO NOT RUN FROM MAIN THREAD.  It would deadlock, but I throw a RuntimeException instead.
     * @param act
     * @param title
     * @param def
     * @return
     */
    public static String stringInputDialog(Activity act, String title, String def) {
        // Partly from https://stackoverflow.com/a/10904665/513038

        //TODO This may not need to be blocking
        String[] result = new String[1];
        CountDownLatch cdl = new CountDownLatch(1);

        if (Misc.isMainThread()) {
            throw new RuntimeException("Do not run stringInputDialog on main thread!");
        }

        Misc.runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(act);
            builder.setTitle(title);

            final EditText input = new EditText(act);
            input.setText(def);
            input.setInputType(InputType.TYPE_CLASS_TEXT); //TODO Permit narrowing
            builder.setView(input);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    result[0] = input.getText().toString();
                    cdl.countDown();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    result[0] = null;
                    cdl.countDown();
                }
            });
            builder.setOnCancelListener(di -> {
                result[0] = null;
                cdl.countDown();
            });

            builder.show();
        });

        try {
            cdl.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result[0];
    }
}
