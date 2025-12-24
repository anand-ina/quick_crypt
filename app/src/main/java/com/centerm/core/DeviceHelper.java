package com.centerm.core;

import android.content.Context;
import android.util.Log;

import com.centerm.quickcrypt.rki.RkiManager;
import com.pos.sdk.DeviceManager;
import com.pos.sdk.DevicesFactory;
import com.pos.sdk.callback.ResultCallback;

public class DeviceHelper {

    private static final String TAG = "DeviceHelper";

    private static final DeviceHelper me = new DeviceHelper();

    public DeviceManager deviceManager;

    public static DeviceHelper me() {
        return me;
    }

    public void init(Context context) {
        DevicesFactory.create(context, new ResultCallback<DeviceManager>() {
            @Override
            public void onFinish(DeviceManager devicesManager) {
                Log.d(TAG, "onFinish: ");

                deviceManager = devicesManager;
                new RkiManager(deviceManager.getRKIBnrDevice(), context);
            }

            @Override
            public void onError(int errorCode, String error) {

                Log.d(TAG, "onError: " + errorCode + "," + error);
            }
        });
    }

}


