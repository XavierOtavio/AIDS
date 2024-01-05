package com.pdm.aids.Common;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.pdm.aids.Login.LoginActivity;
import com.pdm.aids.R;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        SharedPreferences sharedpreferences = getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
    }
}