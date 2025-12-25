package com.centerm.quickcrypt.common_ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.centerm.quickcrypt.R;

public class CustomLoader {

    private AlertDialog dialog;

    public void show(Activity activity) {
        if (activity == null || activity.isFinishing()) return;

        View view = LayoutInflater.from(activity).inflate(R.layout.progress_dialog_view, null, false);

        dialog = new AlertDialog.Builder(activity).setView(view).setCancelable(false).create();
        dialog.show();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            DisplayMetrics metrics = activity.getResources().getDisplayMetrics();
            dialog.getWindow().setLayout((int)(metrics.widthPixels * 0.6), ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    public void dismiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
