
package com.centerm.quickcrypt.rki;

import android.os.Bundle;
import android.util.Log;

import com.centerm.quickcrypt.models.RkiResult;
import com.centerm.quickcrypt.models.TempCertResult;
import com.pos.sdk.rki.RKIParamsKey;
import com.pos.sdk.rki.RkiBnrDevice;

public class RkiDeviceService {

    private static final String TAG = "RkiDeviceService";
    private final RkiBnrDevice device;

    public RkiDeviceService(RkiBnrDevice device) {
        this.device = device;
    }

    public RkiResult generateCsr(boolean checkExist) {
        Bundle params = new Bundle();
        params.putBoolean("isCheckExist", checkExist);
        params.putString("CommonName","D1K0600001982");
        return new RkiResult(device.generateTerminalKey(params));
    }

    public RkiResult storeTerminalCert(byte[] cert) {
        return new RkiResult(device.storeTerminalCert(cert, new Bundle()));
    }

    public RkiResult generateTempCert(TempCertResult out) {
        Bundle result = device.generateTempCert(new Bundle());
        RkiResult r = new RkiResult(result);

        if (r.isSuccess()) {
            out.tempCert = result.getByteArray(RKIParamsKey.TEMP_CERT);
            out.terminalCert = result.getByteArray(RKIParamsKey.TERMINAL_CERT);
            out.customCert = result.getByteArray(RKIParamsKey.CUSTOM_CERT);
        }
        return r;
    }

    public int injectKeys(int algo, int index, byte[] data) {
        return device.performKeyInject(algo, index, data, new Bundle());
    }
}
