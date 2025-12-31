package com.centerm.quickcrypt.utils;

import android.content.Context;
import android.content.SharedPreferences;

public final class RkiPrefs {

    private static final String PREF_NAME = "rki_prefs";

    private static final String KEY_SERVER_URL = "server_url";
    private static final String KEY_API_KEY    = "api_key";
    private static final String KEY_API_TOKEN  = "api_token";
    private static final String KEY_INDEX      = "key_index";

    private static SharedPreferences prefs;

    private RkiPrefs() {
    }

    public static void init(Context context) {
        if (prefs == null) {
            prefs = context.getApplicationContext()
                    .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        }
    }

    public static void save(String serverUrl, String apiKey, String apiToken, int keyIndex) {
        prefs.edit()
                .putString(KEY_SERVER_URL, serverUrl)
                .putString(KEY_API_KEY, apiKey)
                .putString(KEY_API_TOKEN, apiToken)
                .putInt(KEY_INDEX, keyIndex)
                .apply();
    }

    public static String getServerUrl() {
        return prefs.getString(KEY_SERVER_URL, "");
    }

    public static String getApiKey() {
        return prefs.getString(KEY_API_KEY, "");
    }

    public static String getApiToken() {
        return prefs.getString(KEY_API_TOKEN, "");
    }

    public static int getKeyIndex() {
        return prefs.getInt(KEY_INDEX, 0);
    }
}
