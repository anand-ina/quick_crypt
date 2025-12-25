package com.centerm.quickcrypt.common_ui;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.centerm.quickcrypt.R;
import com.google.android.material.snackbar.Snackbar;

public class CustomSnackBar {

    private static final int DURATION_5_SEC = 5000;

    public static void success(Activity activity, String message) {
        show(activity.findViewById(android.R.id.content), message, R.color.green);
    }

    public static void error(Activity activity, String message) {
        show(activity.findViewById(android.R.id.content), message, R.color.red);
    }

    public static void info(Activity activity, String message) {
        show(activity.findViewById(android.R.id.content), message, R.color.blue);
    }

    private static void show(View view, String message, int colorRes) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE);

        snackbar.setBackgroundTintList(
                ColorStateList.valueOf(
                        ContextCompat.getColor(view.getContext(), colorRes)
                )
        );

        snackbar.setDuration(DURATION_5_SEC);
        snackbar.show();
    }
}
