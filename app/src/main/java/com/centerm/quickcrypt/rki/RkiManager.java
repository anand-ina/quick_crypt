package com.centerm.quickcrypt.rki;


import android.app.Activity;
import android.util.Log;

import com.centerm.quickcrypt.common_ui.CustomLoader;
import com.centerm.quickcrypt.common_ui.CustomSnackBar;
import com.centerm.quickcrypt.interfaces.RkiCallback;
import com.centerm.quickcrypt.models.RkiResult;
import com.centerm.quickcrypt.models.TempCertResult;
import com.centerm.quickcrypt.models.TerminalCertResult;
import com.centerm.quickcrypt.utils.AppUtils;
import com.centerm.quickcrypt.utils.RkiPrefs;
import com.pos.sdk.rki.RKIParamsKey;
import com.pos.sdk.rki.RkiBnrDevice;

public class RkiManager {

    private static final String TAG = "RkiManager";

    private final Activity activity;
    private final RkiDeviceService deviceService;
    private final RkiApiService apiService;

    private final CustomLoader customLoader = new CustomLoader();

    public RkiManager(Activity activity, RkiBnrDevice device) {
        this.activity = activity;
        this.deviceService = new RkiDeviceService(device);
        this.apiService = new RkiApiService();
    }

    public void startRkiProvisioning() {

        Log.i(TAG, "RKI Provisioning started");
        customLoader.show(activity);

        RkiResult csrResult = deviceService.generateCsr(true);

        if (csrResult.isAlreadyExists()) {
            Log.w(TAG, "CSR already exists, regenerating");
            csrResult = deviceService.generateCsr(false);
        }

        if (!csrResult.isSuccess()) {
            showError("CSR generation failed: " + csrResult.message);
            return;
        }

        Log.i(TAG, "CSR generated successfully");

        byte[] csr = csrResult.raw.getByteArray(RKIParamsKey.TERMINAL_CSR);
        String customerCert = new AppUtils().loadAsset(activity.getApplicationContext());

        Log.i(TAG, "Requesting terminal certificate from server");

        apiService.requestTerminalCert(customerCert, new String(csr), new RkiCallback() {
            @Override
            public void onSuccess(String response) {
                handleTerminalCert(response);
            }

            @Override
            public void onError(String error) {
                showError(error);
            }
        });
    }

    private void handleTerminalCert(String response) {

        TerminalCertResult cert = AppUtils.parseTerminalCert(response);

        if (!cert.isSuccess() || cert.terminalCertPem == null || cert.terminalCertPem.isEmpty()) {
            showError(cert.responseText);
            return;
        }

        Log.i(TAG, "Started storing terminal certificate on device");
        RkiResult store = deviceService.storeTerminalCert(cert.terminalCertPem.getBytes());

        if (!store.isSuccess() && !store.isAlreadyExists()) {
            showError( store.message);
            return;
        }
        Log.i(TAG, "Terminal certificate stored successfully");

        generateTempCert();
    }

    private void generateTempCert() {

        Log.i(TAG, "Generateting temporary certificate");

        TempCertResult temp = new TempCertResult();
        RkiResult result = deviceService.generateTempCert(temp);

        if (!result.isSuccess()) {
            showError(result.message);
            return;
        }

        Log.i(TAG, "Temporary certificate generated successfully");

        String customerCert = new AppUtils().loadAsset(activity.getApplicationContext());

        apiService.requestRkiData(customerCert, new String(temp.terminalCert), new String(temp.tempCert), new RkiCallback() {
                    @Override
                    public void onSuccess(String response) {

                        TerminalCertResult rkiDataResponse = AppUtils.parseTerminalCert(response);

                        if (!rkiDataResponse.isSuccess()) {
                            showError("RKI request failed");
                            return;
                        }

                        if (rkiDataResponse.rkiData == null || rkiDataResponse.rkiData.isEmpty()) {
                            showError("RKI data is empty");
                            return;
                        }

                        Log.i(TAG, "RKI data received");

                        int keySet  = 0;
                        int keyIndex = RkiPrefs.getKeyIndex();

                        int status = deviceService.injectKeys(keySet, keyIndex, rkiDataResponse.rkiData.getBytes());

                        if (status == 0) {
                            showSuccess("Keys successfully injected at index: " + keyIndex);
                        } else {
                            showError("RKI key injection failed with status: " + status);
                        }

                    }
                    @Override
                    public void onError(String error) {
                        showError(error);
                    }
                });
    }

    public void checkValidations() {

        String serverUrl = RkiPrefs.getServerUrl();
        String apiKey = RkiPrefs.getApiKey();
        String apiToken = RkiPrefs.getApiToken();
        int keyIndex = RkiPrefs.getKeyIndex();

        if (serverUrl.isEmpty()) {
            showInfo("Server URL is empty");
        } else if (apiKey.isEmpty()) {
            showInfo("Api Key is empty");
        } else if (apiToken.isEmpty()) {
            showInfo("Api Token is empty");
        } else if (keyIndex == -1) {
            showInfo("Api Token is empty");
        } else {
            startRkiProvisioning();
        }

    }

    private void showSuccess(String message) {
        Log.i(TAG, message);
        customLoader.dismiss();
        CustomSnackBar.success(activity, message);
    }

    private void showError(String message) {
        Log.i(TAG, message);
        customLoader.dismiss();
        CustomSnackBar.error(activity, message);
    }
    private void showInfo(String message) {
        Log.i(TAG, message);
        customLoader.dismiss();
        CustomSnackBar.info(activity, message);
    }
}
