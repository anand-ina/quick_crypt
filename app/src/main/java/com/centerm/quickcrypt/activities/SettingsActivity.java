package com.centerm.quickcrypt.activities;

import android.os.Bundle;
import android.widget.Toast;

import com.centerm.quickcrypt.R;
import com.centerm.quickcrypt.utils.RkiPrefs;
import com.google.android.material.textfield.TextInputEditText;

public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupEdgeToEdge(R.layout.activity_settings, R.id.settings);
        findViewById(R.id.back_arrow_icon).setOnClickListener(v -> finish());
        findViewById(R.id.button_reset).setOnClickListener(v -> finish());
        findViewById(R.id.button_save).setOnClickListener(v -> onSaveClick());
    }

    public void onSaveClick() {
        String url = ((TextInputEditText) findViewById(R.id.etServerUrl)).getText().toString();
        String apiKey = ((TextInputEditText) findViewById(R.id.etApiKey)).getText().toString();
        String token = ((TextInputEditText) findViewById(R.id.etApiToken)).getText().toString();
        String keyIndex = ((TextInputEditText) findViewById(R.id.etKeyIndex)).getText().toString();

        RkiPrefs.save(this, url, apiKey, token, Integer.parseInt(keyIndex));

        Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();

        finish();
    }
}