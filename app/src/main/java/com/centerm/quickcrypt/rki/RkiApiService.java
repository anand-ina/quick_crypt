package com.centerm.quickcrypt.rki;

import com.centerm.quickcrypt.client.ApiClient;
import com.centerm.quickcrypt.interfaces.RkiCallback;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RkiApiService {

    private final Executor executor = Executors.newSingleThreadExecutor();
    private final ApiClient api = new ApiClient();

    public void requestTerminalCert(String customerCert, String csr, RkiCallback callback) {
        executor.execute(() ->
                api.requestTerminalCert(customerCert, csr, callback)
        );
    }

    public void requestRkiData(String customerCert, String terminalCert, String tempCert, RkiCallback callback) {
        executor.execute(() ->
                api.requestRkiDataFromServer(
                        customerCert,
                        terminalCert,
                        tempCert,
                        callback
                )
        );
    }
}
