package com.centerm.quickcrypt.client;

import com.centerm.quickcrypt.interfaces.RkiCallback;
import com.centerm.quickcrypt.utils.AppConstants;
import com.centerm.quickcrypt.utils.Print;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.internal.Util;

public class RkiApiClient {

    private final OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(new RkiLoggingInterceptor())
            .build();

    public void requestTerminalCert(String serialNumber, String customerCertPem, String terminalCsrPem, RkiCallback callback) {

        RequestBody body = new FormBody.Builder()
                .add("Action", "2")
                .add("APIVersion", AppConstants.API_VERSION)
                .add("APIKey", "rCh7aWvxLf4iCWeX")
                .add("APIToken", "OArvtY5Af6AxOKnEThaNCMY72FZiPX9j")
                .add("SerialNumber", serialNumber)
                .add("CustomerCert", customerCertPem)
                .add("TerminalCsr", terminalCsrPem)
                .build();

        Request request = new Request.Builder()
                .url(AppConstants.BASE_URL)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Print.p("", "API failed");
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
                Print.p("", "Response: " + responseBody);
                callback.onSuccess(responseBody);
            }
        });
    }
}
