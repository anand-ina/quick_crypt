package com.centerm.quickcrypt.models;

import android.os.Bundle;

import com.pos.sdk.rki.RKIParamsKey;

public class RkiResult {
    public final int status;
    public final String message;
    public final Bundle raw;

    public RkiResult(Bundle bundle) {
        this.raw = bundle;
        this.status = bundle.getInt(RKIParamsKey.STATUS_CODE, -1);
        this.message = bundle.getString(RKIParamsKey.STATUS_MESSAGE, "");
    }

    public boolean isSuccess() {
        return status == 0;
    }

    public boolean isAlreadyExists() {
        return status == 1;
    }
}
