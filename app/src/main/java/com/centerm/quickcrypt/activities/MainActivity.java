package com.centerm.quickcrypt.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.centerm.core.DeviceHelper;
import com.centerm.quickcrypt.R;
import com.centerm.quickcrypt.common_ui.CustomSnackBar;
import com.centerm.quickcrypt.rki.RkiManager;
import com.centerm.quickcrypt.utils.RkiPrefs;
import com.pos.sdk.DeviceManager;
import com.pos.sdk.rki.RkiBnrDevice;
import com.pos.sdk.sys.SystemDevice;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupEdgeToEdge(R.layout.activity_main, R.id.main);
        RkiPrefs.init(this);
        DeviceHelper.me().init(MainActivity.this);

        findViewById(R.id.settings_imageview).setOnClickListener(v -> {
            startActivity(new Intent(this, SettingsActivity.class));
        });

        findViewById(R.id.button_load_key).setOnClickListener(v -> {
            RkiBnrDevice rkiDevice = DeviceHelper.me().deviceManager.getRKIBnrDevice();
            RkiManager rkiManager = new RkiManager(this, rkiDevice);

            rkiManager.checkValidations();
        });

        findViewById(R.id.btn_about).setOnClickListener(v -> {
            showAboutDialog();
        });
    }

    public void showAboutDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_about);
        dialog.setCancelable(true);

        TextView textViewSerialNumber = dialog.findViewById(R.id.tv_serial_no);
        TextView textViewVersion = dialog.findViewById(R.id.tv_version);
        Button btnClose = dialog.findViewById(R.id.button_close);

        DeviceManager deviceManager = DeviceHelper.me().deviceManager;
        String mDeviceSN = deviceManager.getSystemDevice().getSystemInfo(SystemDevice.SystemInfoType.SN);
        String version = deviceManager.getSystemDevice().getSystemInfo(SystemDevice.SystemInfoType.VERSION_EMV);

        textViewSerialNumber.setText(mDeviceSN);
        textViewVersion.setText(version);

        btnClose.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

}
