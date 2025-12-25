package com.centerm.core;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.centerm.quickcrypt.rki.RkiManager;
import com.pos.sdk.DeviceManager;
import com.pos.sdk.DevicesFactory;
import com.pos.sdk.callback.ResultCallback;
import com.pos.sdk.sys.SystemDevice;

public class DeviceHelper {

    private static final String TAG = "DeviceHelper";

    private static final DeviceHelper me = new DeviceHelper();

    public DeviceManager deviceManager;

    public static DeviceHelper me() {
        return me;
    }

    public void init(Activity activity) {
        DevicesFactory.create(activity, new ResultCallback<DeviceManager>() {
            @Override
            public void onFinish(DeviceManager devicesManager) {
                Log.d(TAG, "onFinish: ");

                deviceManager = devicesManager;
                new RkiManager(activity, deviceManager.getRKIBnrDevice());
            }

            @Override
            public void onError(int errorCode, String error) {

                Log.d(TAG, "onError: " + errorCode + "," + error);
            }
        });
    }

    public String getSerielNumber() {
        String deviceManager = DeviceHelper.me().deviceManager.getSystemDevice().getSystemInfo(SystemDevice.SystemInfoType.SN);
        return "D1K0600001982";
    }

}


