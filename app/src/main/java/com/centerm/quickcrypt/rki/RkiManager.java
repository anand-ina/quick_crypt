package com.centerm.quickcrypt.rki;

import static com.centerm.quickcrypt.utils.AppConstants.CUSTOMER_CERT;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.centerm.core.DeviceHelper;
import com.centerm.quickcrypt.client.RkiApiClient;
import com.centerm.quickcrypt.interfaces.RkiCallback;
import com.centerm.quickcrypt.models.TerminalCertResult;
import com.centerm.quickcrypt.utils.AppUtils;
import com.pos.sdk.rki.RKIParamsKey;
import com.pos.sdk.rki.RkiBnrDevice;
import com.pos.sdk.sys.SystemDevice;

import java.util.concurrent.Executors;

public class RkiManager {

    private static final String TAG = "RkiManager";

    private static RkiBnrDevice rkiDevice;
    private static Context context;

    public RkiManager(RkiBnrDevice rkiDevice,Context context) {
        this.rkiDevice = rkiDevice;
        this.context = context;
    }


    public static void startRkiProvisioning() {

        try {
            Bundle result = generateTerminalCsr();

            int status = result.getInt(RKIParamsKey.STATUS_CODE, -1);

            if (status == 0) {

                byte[] csrBytes = result.getByteArray(RKIParamsKey.TERMINAL_CSR);

                if (csrBytes == null) {
                    Log.i(TAG, "CSR byte array is null");
                    return;
                }

                try {

                    String customerCert = new AppUtils().loadAsset(context, CUSTOMER_CERT);

                    Executors.newSingleThreadExecutor().submit(() -> {
                        new RkiApiClient().requestTerminalCert(customerCert, new String(csrBytes),
                                new RkiCallback() {
                                    @Override
                                    public void onSuccess(String response) {
                                        TerminalCertResult terminalCertResult = parseCertificateResult(response);

                                        Log.i(TAG, "ResponseCode : " + terminalCertResult.responseCode);
                                        Log.i(TAG, "ResponseText : " + terminalCertResult.responseText);
                                        Log.i(TAG, "TerminalCrt  :\n" + terminalCertResult.terminalCertPem);

                                        if (terminalCertResult.responseCode == 0) {
                                            storeTerminalCertificate(terminalCertResult.terminalCertPem.getBytes());
                                        }
                                    }

                                    @Override
                                    public void onError(String error) {
                                        Log.i("RKI", error);
                                    }
                                }
                        );
                    });
                } catch (Exception e) {
                    Log.i("RKI", e.toString());
                }

            } else {
                Log.i(TAG, "CSR failed: " + result.getString("statusMessage"));
            }

        } catch (Exception e) {
            Log.i(TAG, e.toString());
        }
    }


    private static Bundle generateTerminalCsr() {

        Bundle params = new Bundle(2);
        Bundle result = rkiDevice.generateTerminalKey(params);
        int status = result.getInt(RKIParamsKey.STATUS_CODE, -1);

        if (status == 1) {
            Log.i(TAG, "CSR Already generated. Trying with isCheckExist = false");
            params.putBoolean("isCheckExist", false);
            result = rkiDevice.generateTerminalKey(params);
        }

        return result;
    }

    private static boolean storeTerminalCertificate(byte[] terminalCert) {
        Bundle result = rkiDevice.storeTerminalCert(terminalCert, new Bundle(0));
        int status = result.getInt(RKIParamsKey.STATUS_CODE, -1);

        if (status == 0 || status == 1) {
            Log.i(TAG, "Terminal certificate " + (status == 0 ? "stored successfully" : "already exists"));
            generateTempCertificate();
            return true;
        }

        Log.i(TAG, "Failed to store terminal cert: " + result.getString(RKIParamsKey.STATUS_MESSAGE));

        return false;
    }

    private static void generateTempCertificate() {

        Bundle result = rkiDevice.generateTempCert(new Bundle());
        int code = result.getInt(RKIParamsKey.STATUS_CODE, -1);

        if (code == 0) {
            byte[] tempCert = result.getByteArray(RKIParamsKey.TEMP_CERT);
            byte[] customCert = result.getByteArray(RKIParamsKey.CUSTOM_CERT);
            byte[] terminalCert = result.getByteArray(RKIParamsKey.TERMINAL_CERT);

            Log.i(TAG, "Generate Temp Certificate Success");
            Log.i(TAG, "Temp Cert : " + new String(tempCert));
            Log.i(TAG, "Custom Cert : " + new String(customCert));
            Log.i(TAG, "Terminal Cert : " + new String(terminalCert));

            if (terminalCert == null || terminalCert.length == 0) {
                Log.e(TAG, "Terminal Cert is empty");
            } else {
                Executors.newSingleThreadExecutor().submit(() -> {

                    String customerCert = new AppUtils().loadAsset(context, CUSTOMER_CERT);


                    new RkiApiClient().requestRkiDataFromServer(customerCert, new String(terminalCert), new String(tempCert),
                            new RkiCallback() {
                                @Override
                                public void onSuccess(String response) {
                                    Log.i(TAG, response);

                                }

                                @Override
                                public void onError(String error) {
                                    Log.i("RKI", error);
                                }
                            }
                    );
                });
            }
        } else {
            Log.e(TAG, result.getString(RKIParamsKey.STATUS_MESSAGE, ""));
        }
    }


    private static TerminalCertResult parseCertificateResult(String resp) {
        TerminalCertResult terminalCertResult = new TerminalCertResult();
        String[] kv = resp.split("&");
        for (String s : kv) {
            String[] tmp = s.split("=", 2);
            switch (tmp[0]) {
                case "ResponseCode":
                    terminalCertResult.responseCode = Integer.parseInt(tmp[1]);
                    break;
                case "ResponseText":
                    terminalCertResult.responseText = tmp[1];
                    break;
                case "TerminalCrt":
                    terminalCertResult.terminalCertPem = tmp[1];
                    break;
                case "RKIData":
                    terminalCertResult.rkiData = tmp[1];
            }
        }
        return terminalCertResult;
    }


    public int injectKeys(int dukptAlgo, int keyIndex, byte[] rkiData) {

        int result = rkiDevice.performKeyInject(
                dukptAlgo,
                keyIndex,
                rkiData,
                new Bundle()
        );

        Log.i(TAG, "injectKeys result=" + result);
        return result;
    }
}
