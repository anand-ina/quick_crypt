package com.centerm.quickcrypt.rki;

import android.os.Bundle;
import android.util.Log;

import com.centerm.quickcrypt.utils.Print;
import com.pos.sdk.rki.RkiBnrDevice;
import com.pos.sdk.rki.RKIParamsKey;

public class RkiManager {

    private static final String TAG = "RkiManager";

    private final RkiBnrDevice rkiDevice;

    public RkiManager(RkiBnrDevice rkiDevice) {
        this.rkiDevice = rkiDevice;
    }

    public Bundle generateTerminalCsr() {

        Bundle params = new Bundle();
        params.putString(RKIParamsKey.ASYNC_CRYPTO_ALGO, "RSA");
        params.putInt(RKIParamsKey.ASYNC_KEY_LEN, 2048);

        Bundle result = rkiDevice.generateTerminalKey(params);
        int status = result.getInt(RKIParamsKey.STATUS_CODE, -1);

        if (status == 1) {
            Print.p(TAG, "CSR Already generated. Trying with isCheckExist = false");
            params.putBoolean("isCheckExist", true);
            result = rkiDevice.generateTerminalKey(params);
        }

        return result;
    }

    public boolean storeTerminalCertificate(byte[] terminalCert) {

        Bundle result = rkiDevice.storeTerminalCert(terminalCert, new Bundle());

        int status = result.getInt(RKIParamsKey.STATUS_CODE, -1);
        String msg = result.getString(RKIParamsKey.STATUS_MESSAGE);
        System.out.println(msg);

        if (status == 0) {
            Print.p(TAG, "Terminal certificate stored successfully");
            return true;
        }

        if (status == 1) {
            Print.p(TAG, "Terminal certificate already exists");
            return true;
        }

        Print.p(TAG, "Failed to store terminal cert: " + result.getString(RKIParamsKey.STATUS_MESSAGE));

        return false;
    }


    public Bundle generateTempCertificate() {

        Bundle params = new Bundle();
        params.putString(RKIParamsKey.ASYNC_CRYPTO_ALGO, "RSA");
        params.putInt(RKIParamsKey.ASYNC_KEY_LEN, 2048);

        Bundle result = rkiDevice.generateTempCert(params);

        return result;
    }

    public int injectKeys(int dukptAlgo, int keyIndex, byte[] rkiData) {

        int result = rkiDevice.performKeyInject(
                dukptAlgo,
                keyIndex,
                rkiData,
                new Bundle()
        );

        Print.p(TAG, "injectKeys result=" + result);
        return result;
    }
}
