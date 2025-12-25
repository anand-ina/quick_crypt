package com.centerm.quickcrypt.utils;

import android.content.Context;
import android.util.Log;

import com.centerm.quickcrypt.models.TerminalCertResult;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class AppUtils {

    String TAG = "AppUtils";
    String CUSTOMER_CERT = "CUSTOMER-CENTERM-TEST.crt";


    public String loadAsset(Context context) {

        try {
            InputStream is = context.getAssets().open(CUSTOMER_CERT);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }

            is.close();

            return baos.toString(StandardCharsets.UTF_8.name());

        } catch (IOException e) {
            Log.i(TAG, e.toString());
            return  "";
        }

    }

    public static TerminalCertResult parseTerminalCert(String resp) {
        TerminalCertResult r = new TerminalCertResult();

        for (String pair : resp.split("&")) {
            String[] kv = pair.split("=", 2);
            if (kv.length != 2) continue;

            switch (kv[0]) {
                case "ResponseCode":
                    r.responseCode = Integer.parseInt(kv[1]);
                    break;
                case "ResponseText":
                    r.responseText = kv[1];
                    break;
                case "TerminalCrt":
                    r.terminalCertPem = kv[1];
                    break;
                case "RKIData":
                    r.rkiData = kv[1];
            }
        }
        return r;
    }

}
