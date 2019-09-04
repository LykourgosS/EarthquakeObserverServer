package com.unipi.lykourgoss.earthquakeobserver.server.activities;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by LykourgosS <lpsarantidis@gmail.com>
 * on 27,August,2019.
 */

public class BaseActivity extends AppCompatActivity {

    private static final String LOADING = "Loading";

    protected ProgressDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void showProgressDialog() {
        if (dialog == null) {
            dialog = new ProgressDialog(this);
            dialog.setMessage(LOADING);
            dialog.setIndeterminate(true);
        }
        dialog.show();
    }

    protected void hideProgressDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        hideProgressDialog();
    }
}
