package com.centerm.quickcrypt.activities;

import static com.centerm.quickcrypt.utils.AppConstants.CUSTOMER_CERT;
import static com.centerm.quickcrypt.utils.AppConstants.SUCCESS_RESPONSE;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.centerm.core.DeviceHelper;
import com.centerm.quickcrypt.R;
import com.centerm.quickcrypt.client.RkiApiClient;
import com.centerm.quickcrypt.interfaces.RkiCallback;
import com.centerm.quickcrypt.rki.RkiManager;
import com.centerm.quickcrypt.utils.AppUtils;
import com.centerm.quickcrypt.utils.Print;
import com.centerm.quickcrypt.utils.RkiPrefs;
import com.pos.sdk.DeviceManager;
import com.pos.sdk.rki.RKIParamsKey;
import com.pos.sdk.sys.SystemDevice;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.Executors;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    private static DeviceManager deviceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupEdgeToEdge(R.layout.activity_main, R.id.main);
        initDeviceFactory();
    }

    private void initDeviceFactory() {

        DeviceHelper.me().init(this, new DeviceHelper.DeviceInitCallback() {
            @Override
            public void onSuccess(DeviceManager devicesManager) {
                deviceManager = devicesManager;
                Print.p(TAG, "DeviceManager initialized");
            }

            @Override
            public void onFailure(int code, String message) {
                Print.p(TAG, "DeviceFactory error: " + code + " " + message);
            }
        });

    }

    public void onSettingsClick(View view) {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    public void startRkiProvisioning(View view) {
        try {
            RkiManager rkiManager = new RkiManager(deviceManager.getRKIBnrDevice());
            Bundle result = rkiManager.generateTerminalCsr();
            String serialNumber = deviceManager.getSystemDevice().getSystemInfo(SystemDevice.SystemInfoType.SN);

            int status = result.getInt(RKIParamsKey.STATUS_CODE, -1);
            String msg = result.getString(RKIParamsKey.STATUS_MESSAGE);
            System.out.println(msg);

            if (status == 0) {

                byte[] csrBytes = result.getByteArray(RKIParamsKey.TERMINAL_CSR);

                if (csrBytes == null) {
                    Print.p(TAG, "CSR byte array is null");
                    return;
                }

                try {
                    String terminalCsrPem = new String(csrBytes, StandardCharsets.UTF_8);
                    String customerCertPem = new AppUtils().loadAsset(this, CUSTOMER_CERT);


                    Executors.newSingleThreadExecutor().submit(() -> {

                        new RkiApiClient().requestTerminalCert(serialNumber, customerCertPem, terminalCsrPem,
                                new RkiCallback() {

                                    @Override
                                    public void onSuccess(String response) {
                                        Map<String, String> res = AppUtils.parseFormEncoded(response);

                                        String responseCode = res.get("ResponseCode");
                                        String responseText = res.get("ResponseText");
                                        String terminalCrt  = res.get("TerminalCrt");

                                        Print.p(TAG, "ResponseCode : " + responseCode);
                                        Print.p(TAG, "ResponseText : " + responseText);
                                        Print.p(TAG, "TerminalCrt  :\n" + terminalCrt);

                                        if(responseCode.equals(SUCCESS_RESPONSE)){
                                            rkiManager.storeTerminalCertificate(terminalCrt.getBytes(StandardCharsets.UTF_8));
                                        }
                                    }

                                    @Override
                                    public void onError(String error) {
                                        Print.p("RKI", error);
                                    }
                                }
                        );
                    });
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            } else {
                Print.p(TAG, "CSR failed: " + result.getString("statusMessage"));
            }

        } catch (Exception e) {
            Print.p(TAG, e.toString());
        }
    }


    public void showAboutDialog(View view) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_about);
        dialog.setCancelable(true);

        TextView textViewSerialNumber = dialog.findViewById(R.id.tv_serial_no);
        TextView textViewVersion = dialog.findViewById(R.id.tv_version);
        Button btnClose = dialog.findViewById(R.id.button_close);

        String mDeviceSN = deviceManager.getSystemDevice().getSystemInfo(SystemDevice.SystemInfoType.SN);
        String version = deviceManager.getSystemDevice().getSystemInfo(SystemDevice.SystemInfoType.VERSION_EMV);

        textViewSerialNumber.setText(mDeviceSN);
        textViewVersion.setText(version);

        btnClose.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

}
