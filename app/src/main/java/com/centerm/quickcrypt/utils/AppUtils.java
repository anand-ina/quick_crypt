package com.centerm.quickcrypt.utils;

import android.content.Context;
import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class AppUtils {

    String TAG = "AppUtils";

    public String loadAsset(Context context, String fileName) {

        try {
            InputStream is = context.getAssets().open(fileName);
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
}
