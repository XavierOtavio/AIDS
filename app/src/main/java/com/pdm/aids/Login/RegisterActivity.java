package com.pdm.aids.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.pdm.aids.Common.NetworkChecker;
import com.pdm.aids.Common.OutsystemsAPI;
import com.pdm.aids.R;

import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    private NetworkChecker networkChecker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final Button btnLogin = (Button) findViewById(R.id.button_login);
        btnLogin.setOnClickListener(v -> {
            finish();
        });

        Button btnRegister = (Button) findViewById(R.id.button_register);
        TextInputLayout nameLayout = (TextInputLayout) findViewById(R.id.textInputLayout_Name);
        TextInputLayout nMecLayout = (TextInputLayout) findViewById(R.id.textInputLayout_NMec);
        TextInputLayout passwordLayout = (TextInputLayout) findViewById(R.id.textInputLayout_Password);
        TextInputLayout confirmPasswordLayout = (TextInputLayout) findViewById(R.id.textInputLayout_ConfirmPassword);
        EditText name = (EditText) findViewById(R.id.edit_text_name);
        EditText nMec = (EditText) findViewById(R.id.edit_text_nmechanographic);
        EditText password = (EditText) findViewById(R.id.edit_text_password);
        EditText confirmPassword = (EditText) findViewById(R.id.edit_text_confirm_password);
        LinearLayout layoutWithoutInternet = (LinearLayout) findViewById(R.id.layoutWithoutInternet);
        LinearLayout layoutWithInternet = (LinearLayout) findViewById(R.id.layoutWithInternet);
        TextView txtTitle = (TextView) findViewById(R.id.textView_Title);

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

        btnRegister.setOnClickListener(v -> {
            if (!validateName(nameLayout) | !validateNMec(nMecLayout) | !validatePassword(passwordLayout) | !validateConfirmPassword(passwordLayout, confirmPasswordLayout)) {
                return;
            }
            OutsystemsAPI.registerUser(name.getText().toString(), nMec.getText().toString(), password.getText().toString(), RegisterActivity.this, new OutsystemsAPI.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    try {
                        JSONObject obj = new JSONObject(result);
                        if (!obj.getString("HTTPCode").equals("200")) {
                            Toast.makeText(getApplicationContext(), obj.getString("Message"), Toast.LENGTH_SHORT).show();
                            finish();
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
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
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

    private boolean validateName(TextInputLayout name) {
        String val = name.getEditText().getText().toString().trim();

        if (val.isEmpty()) {
            name.setError("O campo não pode estar vazio");
            return false;
        } else {
            name.setError(null);
            name.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateNMec(TextInputLayout nMec) {
        String val = nMec.getEditText().getText().toString().trim();

        if (val.isEmpty()) {
            nMec.setError("O campo não pode estar vazio");
            return false;
        } else if (val.length() > 9) {
            nMec.setError("O número mecânografico não pode ter mais de 9 digitos");
            return false;
        } else {
            nMec.setError(null);
            nMec.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validatePassword(TextInputLayout password) {
        String val = password.getEditText().getText().toString().trim();

        if (val.isEmpty()) {
            password.setError("O campo não pode estar vazio");
            return false;
        } else if (val.length() < 8) {
            password.setError("A password deve ter pelo menos 8 caracteres");
            return false;
        } else {
            password.setError(null);
            password.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateConfirmPassword(TextInputLayout password, TextInputLayout confirmPassword) {
        String val = confirmPassword.getEditText().getText().toString().trim();
        String val2 = password.getEditText().getText().toString().trim();

        if (val.isEmpty()) {
            confirmPassword.setError("O campo não pode estar vazio");
            return false;
        } else if (!val.equals(val2)) {
            confirmPassword.setError("As passwords não coincidem");
            return false;
        } else {
            confirmPassword.setError(null);
            confirmPassword.setErrorEnabled(false);
            return true;
        }
    }


}