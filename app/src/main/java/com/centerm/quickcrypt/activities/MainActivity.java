package com.centerm.quickcrypt.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.centerm.quickcrypt.core.DeviceHelper;
import com.centerm.quickcrypt.R;
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
            showPasswordDialog(true);
        });

        findViewById(R.id.button_load_key).setOnClickListener(v -> {
            showPasswordDialog(false);
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

    public void showPasswordDialog(boolean isSettings) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_password);
        dialog.setCancelable(true);

        EditText editTextPassword = dialog.findViewById(R.id.edittext_password);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);
        Button buttonConfirm = dialog.findViewById(R.id.button_confirm);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        buttonConfirm.setOnClickListener(v -> {

            String password = editTextPassword.getText().toString();
            String validPassword = "123456";

            if (!password.equals(validPassword)) {
                editTextPassword.setError(getString(R.string.text_incorrect_password));
                return;
            }

            InputMethodManager inputMethodManager = (InputMethodManager) editTextPassword.getContext().getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(editTextPassword.getWindowToken(), 0);

            dialog.dismiss();

            if (isSettings) {
                startActivity(new Intent(this, SettingsActivity.class));
            } else {
                RkiBnrDevice rkiDevice = DeviceHelper.me().deviceManager.getRKIBnrDevice();
                RkiManager rkiManager = new RkiManager(this, rkiDevice);

                rkiManager.checkValidations();
            }
        });

        dialog.show();
    }

}
