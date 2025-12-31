package com.centerm.quickcrypt.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.EditText;

import com.centerm.quickcrypt.R;
import com.centerm.quickcrypt.common_ui.CustomSnackBar;
import com.centerm.quickcrypt.utils.RkiPrefs;

public class SettingsActivity extends BaseActivity {

    private EditText etServerUrlEditText, etApiKeyEditText, etApiTokenEditText, etKeyIndexEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupEdgeToEdge(R.layout.activity_settings, R.id.settings);

        etServerUrlEditText = findViewById(R.id.edittext_server_url);
        etApiKeyEditText = findViewById(R.id.edittext_api_key);
        etApiTokenEditText = findViewById(R.id.edittext_api_token);
        etKeyIndexEditText = findViewById(R.id.editext_key_index);

        findViewById(R.id.back_arrow_icon).setOnClickListener(v -> finish());
        findViewById(R.id.button_reset).setOnClickListener(v -> onRestClick());
        findViewById(R.id.button_save).setOnClickListener(v -> onSaveClick());

        initializeValues();
    }

    private void initializeValues() {
        etServerUrlEditText.setText(RkiPrefs.getServerUrl());
        etApiKeyEditText.setText(RkiPrefs.getApiKey());
        etApiTokenEditText.setText(RkiPrefs.getApiToken());
        etKeyIndexEditText.setText(String.valueOf(RkiPrefs.getKeyIndex()));
    }

    private void onRestClick() {
        CustomSnackBar.success(this, getString(R.string.text_settings_reset_successfully));
        RkiPrefs.save( "", "", "", 0);
        findViewById(android.R.id.content).postDelayed(this::finish, 2000);
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

    private String getText(EditText editText) {
        return editText.getText() == null ? "" : editText.getText().toString().trim();
    }
}