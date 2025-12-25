package com.centerm.quickcrypt.client;

import android.util.Log;

import com.centerm.core.DeviceHelper;
import com.centerm.quickcrypt.interfaces.RkiCallback;
import com.centerm.quickcrypt.utils.RkiPrefs;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ApiClient {
    public static final String API_VERSION = "1";
    public static final String ACTION_0 = "0";
    public static final String ACTION_2 = "2";
    public static final String TAG = "ApiClient";

    private final OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(new LoggingInterceptor())
            .build();

    public void requestTerminalCert(String customerCert, String terminalCsr, RkiCallback callback) {

        RequestBody body = new FormBody.Builder()
                .add("Action", ACTION_2)
                .add("APIVersion", API_VERSION)
                .add("APIKey", RkiPrefs.getApiKey())
                .add("APIToken", RkiPrefs.getApiToken())
                .add("SerialNumber", DeviceHelper.me().getSerielNumber())
                .add("CustomerCert", customerCert)
                .add("TerminalCsr", terminalCsr)
                .build();

        Request request = new Request.Builder().url(RkiPrefs.getServerUrl()).post(body).build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "API failed");
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
                callback.onSuccess(responseBody);
            }
        });
    }

    public void requestRkiDataFromServer(String customerCert, String terminalCsr, String tempCert, RkiCallback callback) {

        RequestBody body = new FormBody.Builder()
                .add("Action", ACTION_0)
                .add("APIKey", RkiPrefs.getApiKey())
                .add("APIToken", RkiPrefs.getApiToken())
                .add("APIVersion", API_VERSION)
                .add("SerialNumber", DeviceHelper.me().getSerielNumber())
                .add("CustomerCert", customerCert)
                .add("TerminalCert", terminalCsr)
                .add("TempCert", tempCert)
                .build();

        Request request = new Request.Builder().url(RkiPrefs.getServerUrl()).post(body).build();

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
                Log.e("", "Response: " + responseBody);
                callback.onSuccess(responseBody);
            }
        });
    }
}
