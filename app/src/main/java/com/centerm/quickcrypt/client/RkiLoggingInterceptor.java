package com.centerm.quickcrypt.client;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

public class RkiLoggingInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();

        // ---- REQUEST LOG ----
        System.out.println("===== RKI REQUEST =====");
        System.out.println("URL     : " + request.url());
        System.out.println("Method  : " + request.method());
        System.out.println("Headers : " + request.headers());

        if (request.body() != null) {
            Buffer buffer = new Buffer();
            request.body().writeTo(buffer);
            String body = buffer.readString(StandardCharsets.UTF_8);
            System.out.println("Body    : " + body);
        }

        long startTime = System.currentTimeMillis();
        Response response = chain.proceed(request);
        long duration = System.currentTimeMillis() - startTime;

        // ---- RESPONSE LOG ----
        System.out.println("===== RKI RESPONSE =====");
        System.out.println("Code    : " + response.code());
        System.out.println("Time    : " + duration + " ms");

        ResponseBody responseBody = response.body();
        MediaType contentType = responseBody.contentType();
        String responseString = responseBody.string();

        System.out.println("Body    : " + responseString);

        // IMPORTANT: recreate response body
        ResponseBody newBody = ResponseBody.create(responseString, contentType);

        return response.newBuilder().body(newBody).build();
    }
}
