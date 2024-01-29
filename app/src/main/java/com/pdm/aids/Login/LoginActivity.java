package com.pdm.aids.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.pdm.aids.Common.HomeActivity;
import com.pdm.aids.Common.NetworkChecker;
import com.pdm.aids.Common.OutsystemsAPI;
import com.pdm.aids.R;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    public static final String MyPREFERENCES = "MyPrefs";
    SharedPreferences sharedpreferences;
    private NetworkChecker networkChecker;
    private EditText numMec;
    private EditText password;
    private Button btnLogin;
    private Button btnXavier;
    private Button btnRegister;
    private LinearLayout layoutWithoutInternet;
    private LinearLayout layoutWithInternet;
    private TextView txtTitle;

    public LoginActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //-----------------Initialize Variables-----------------
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        numMec = (EditText) findViewById(R.id.edit_text_nmechanographic);
        password = (EditText) findViewById(R.id.edit_text_password);
        btnLogin = (Button) findViewById(R.id.button_login);
        btnXavier = (Button) findViewById(R.id.button_login_0);
        btnRegister = (Button) findViewById(R.id.button_register);
        layoutWithoutInternet = (LinearLayout) findViewById(R.id.layoutWithoutInternet);
        layoutWithInternet = (LinearLayout) findViewById(R.id.layoutWithInternet);
        txtTitle = (TextView) findViewById(R.id.textView_Title);

        //------------------Internet Connection------------------
        networkChecker = new NetworkChecker(this);
        if (networkChecker.isInternetConnected()) {
               layoutWithoutInternet.setVisibility(LinearLayout.GONE);
               layoutWithInternet.setVisibility(LinearLayout.VISIBLE);
                txtTitle.setVisibility(TextView.VISIBLE);

        } else {
            layoutWithoutInternet.setVisibility(LinearLayout.VISIBLE);
            layoutWithInternet.setVisibility(LinearLayout.GONE);
            txtTitle.setVisibility(TextView.GONE);
        }

        networkChecker.setNetworkCallbackListener(new NetworkChecker.NetworkCallbackListener() {
            @Override
            public void onNetworkAvailable() {
                runOnUiThread(() -> {
                    layoutWithoutInternet.setVisibility(LinearLayout.GONE);
                    layoutWithInternet.setVisibility(LinearLayout.VISIBLE);
                    txtTitle.setVisibility(TextView.VISIBLE);
                });
            }

            @Override
            public void onNetworkUnavailable() {
                runOnUiThread(() -> {
                    layoutWithoutInternet.setVisibility(LinearLayout.VISIBLE);
                    layoutWithInternet.setVisibility(LinearLayout.GONE);
                    txtTitle.setVisibility(TextView.GONE);
                });
            }
        });

        //-----------------Register Button-----------------
        btnRegister.setOnClickListener(v -> {
            disablePage();
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            enablePage();
        });

        //-----------------Xavier Login Button-----------------
        btnXavier.setOnClickListener(v -> {
            disablePage();
            OutsystemsAPI.checkLogin("0", "admin", LoginActivity.this, new OutsystemsAPI.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    try {
                        JSONObject obj = new JSONObject(result);
                        if (obj.getString("HTTPCode").equals("200")) {
                            editor.putString("Name", obj.getString("Name"));
                            editor.putString("Username", obj.getString("Username"));
                            editor.putString("Password", obj.getString("Password"));
                            editor.putString("Id", obj.getString("Id"));
                            editor.apply();

                            OutsystemsAPI.getDataFromAPI(obj.getString("Id"), LoginActivity.this);

                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            enablePage();
                            Toast.makeText(getApplicationContext(), obj.getString("Message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(String error) {
                    try {
                        JSONObject obj = new JSONObject(error);
                        Toast.makeText(getApplicationContext(), obj.getString("Message"), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    enablePage();
                }
            });
        });

        //-----------------Login Button-----------------
        btnLogin.setOnClickListener(v ->
        {
            disablePage();
            if (!validateNMec((TextInputLayout) findViewById(R.id.textInputLayout_NMec)) | !validatePassword((TextInputLayout) findViewById(R.id.textInputLayout_Password))) {
                enablePage();
                return;
            }

            OutsystemsAPI.checkLogin(numMec.getText().toString(), password.getText().toString(), LoginActivity.this, new OutsystemsAPI.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    try {
                        JSONObject obj = new JSONObject(result);
                        if (obj.getString("HTTPCode").equals("200")) {
                            editor.putString("Name", obj.getString("Name"));
                            editor.putString("Username", obj.getString("Username"));
                            editor.putString("Password", obj.getString("Password"));
                            editor.putString("Id", obj.getString("Id"));
                            editor.apply();

                            OutsystemsAPI.getDataFromAPI(obj.getString("Id"), LoginActivity.this);

                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            enablePage();
                            Toast.makeText(getApplicationContext(), obj.getString("Message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(String error) {
                    try {
                        JSONObject obj = new JSONObject(error);
                        Toast.makeText(getApplicationContext(), obj.getString("Message"), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    enablePage();
                }

            });
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        networkChecker.registerNetworkCallback();
    }

    @Override
    protected void onStop() {
        super.onStop();
        networkChecker.unregisterNetworkCallback();
    }

    private boolean validateNMec(TextInputLayout nMec) {
        String val = nMec.getEditText().getText().toString().trim();

        if (val.isEmpty()) {
            nMec.setError("O número mecânografico não pode estar vazio");
            return false;
        } else {
            nMec.setError(null);
            return true;
        }
    }

    private boolean validatePassword(TextInputLayout password) {
        String val = password.getEditText().getText().toString().trim();

        if (val.isEmpty()) {
            password.setError("A password não pode estar vazia");
            return false;
        } else {
            password.setError(null);
            return true;
        }
    }

    private void disablePage() {
        numMec.setEnabled(false);
        password.setEnabled(false);
        btnLogin.setEnabled(false);
        btnRegister.setEnabled(false);
        btnXavier.setEnabled(false);
    }

    private void enablePage() {
        numMec.setEnabled(true);
        password.setEnabled(true);
        btnLogin.setEnabled(true);
        btnRegister.setEnabled(true);
        btnXavier.setEnabled(true);
    }

}