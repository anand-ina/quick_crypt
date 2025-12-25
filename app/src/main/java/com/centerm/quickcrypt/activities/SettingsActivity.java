package com.centerm.quickcrypt.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;

import com.centerm.quickcrypt.R;
import com.centerm.quickcrypt.common_ui.CustomSnackBar;
import com.centerm.quickcrypt.utils.RkiPrefs;
import com.google.android.material.textfield.TextInputEditText;

public class SettingsActivity extends BaseActivity {

    private TextInputEditText etServerUrlEditText, etApiKeyEditText, etApiTokenEditText, etKeyIndexEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupEdgeToEdge(R.layout.activity_settings, R.id.settings);

        etServerUrlEditText = findViewById(R.id.etServerUrl);
        etApiKeyEditText = findViewById(R.id.etApiKey);
        etApiTokenEditText = findViewById(R.id.etApiToken);
        etKeyIndexEditText = findViewById(R.id.etKeyIndex);

        findViewById(R.id.back_arrow_icon).setOnClickListener(v -> finish());
        findViewById(R.id.button_reset).setOnClickListener(v -> finish());
        findViewById(R.id.button_save).setOnClickListener(v -> onSaveClick());
    }

    private void onSaveClick() {
        String url = getText(etServerUrlEditText);
        String apiKey = getText(etApiKeyEditText);
        String token = getText(etApiTokenEditText);
        String keyIndexStr = getText(etKeyIndexEditText);

        if (TextUtils.isEmpty(url)) {
            CustomSnackBar.error(this, getString(R.string.text_server_url_is_required));
            return;
        }

        if (!Patterns.WEB_URL.matcher(url).matches()) {
            CustomSnackBar.error(this, getString(R.string.text_invalid_server_url));
            return;
        }

        if (TextUtils.isEmpty(apiKey)) {
            CustomSnackBar.error(this, getString(R.string.text_api_key_is_required));
            return;
        }

        if (TextUtils.isEmpty(token)) {
            CustomSnackBar.error(this, getString(R.string.text_api_token_is_required));
            return;
        }

        if (TextUtils.isEmpty(keyIndexStr)) {
            CustomSnackBar.error(this, getString(R.string.text_key_index_is_required));
            return;
        }

        int keyIndex;
        try {
            keyIndex = Integer.parseInt(keyIndexStr);
        } catch (NumberFormatException e) {
            CustomSnackBar.error(this, getString(R.string.text_key_index_must_be_a_number));
            return;
        }

        CustomSnackBar.success(this, getString(R.string.text_settings_saved_successfully));

        RkiPrefs.save( url, apiKey, token, keyIndex);

        findViewById(android.R.id.content).postDelayed(this::finish, 2000);
    }

    private String getText(TextInputEditText editText) {
        return editText.getText() == null ? "" : editText.getText().toString().trim();
    }
}