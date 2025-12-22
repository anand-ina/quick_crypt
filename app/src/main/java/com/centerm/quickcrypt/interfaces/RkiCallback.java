package com.centerm.quickcrypt.interfaces;

public interface RkiCallback {
    void onSuccess(String response);
    void onError(String error);
}
