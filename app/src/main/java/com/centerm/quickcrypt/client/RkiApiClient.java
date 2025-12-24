package com.centerm.quickcrypt.client;

import android.util.Log;

import com.centerm.core.DeviceHelper;
import com.centerm.quickcrypt.interfaces.RkiCallback;
import com.centerm.quickcrypt.utils.AppConstants;
import com.pos.sdk.sys.SystemDevice;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RkiApiClient {

    private final OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(new RkiLoggingInterceptor())
            .build();

    public void requestTerminalCert(String customerCert, String terminalCsr, RkiCallback callback) {

        String serialNumber = DeviceHelper.me().deviceManager.getSystemDevice().getSystemInfo(SystemDevice.SystemInfoType.SN);

        RequestBody body = new FormBody.Builder()
                .add("Action", "2")
                .add("APIVersion", AppConstants.API_VERSION)
                .add("APIKey", "rCh7aWvxLf4iCWeX")
                .add("APIToken", "OArvtY5Af6AxOKnEThaNCMY72FZiPX9j")
                .add("SerialNumber", serialNumber)
                .add("CustomerCert", customerCert)
                .add("TerminalCsr", terminalCsr)
                .build();

        Request request = new Request.Builder()
                .url(AppConstants.BASE_URL)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("", "API failed");
                e.printStackTrace();
                callback.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onError("HTTP error " + response.code());
                    return;
                }

                String responseBody = response.body().string();
                Log.i("", "Response: " + responseBody);
                callback.onSuccess(responseBody);
            }
        });
    }

    public void requestRkiDataFromServer(String customerCert, String terminalCsr, String tempCert, RkiCallback callback) {

        String serialNumber = DeviceHelper.me().deviceManager.getSystemDevice().getSystemInfo(SystemDevice.SystemInfoType.SN);

        RequestBody body = new FormBody.Builder()
                .add("Action", "0")
                .add("APIKey", "rCh7aWvxLf4iCWeX")
                .add("APIToken", "OArvtY5Af6AxOKnEThaNCMY72FZiPX9j")
                .add("APIVersion", AppConstants.API_VERSION)
                .add("SerialNumber", serialNumber)
                .add("CustomerCert", customerCert)
                .add("TerminalCert", terminalCsr)
                .add("TempCert", tempCert)
                .build();

        Request request = new Request.Builder()
                .url(AppConstants.BASE_URL)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("", "API failed");
                e.printStackTrace();
                callback.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onError("HTTP error " + response.code());
                    return;
                }

                String responseBody = response.body().string();
                Log.i("", "Response: " + responseBody);
                callback.onSuccess(responseBody);
            }
        });
    }
}
