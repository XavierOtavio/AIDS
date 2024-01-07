package com.pdm.aids.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.pdm.aids.Common.HomeActivity;
import com.pdm.aids.Common.OutsystemsAPI;
import com.pdm.aids.R;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    public static final String MyPREFERENCES = "MyPrefs";
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        final Button btnLogin = (Button) findViewById(R.id.button_login);
        final Button btnRegister = (Button) findViewById(R.id.button_register);

        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        btnLogin.setOnClickListener(v -> {
            EditText numMecc = (EditText) findViewById(R.id.edit_text_nmechanographic);
            EditText password = (EditText) findViewById(R.id.edit_text_password);

            if (!validateNMec((TextInputLayout) findViewById(R.id.textInputLayout_NMec)) | !validatePassword((TextInputLayout) findViewById(R.id.textInputLayout_Password))) {
                return;
            }
            OutsystemsAPI.checkLogin(numMecc.getText().toString(), password.getText().toString(), LoginActivity.this, new OutsystemsAPI.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    try {
                        JSONObject obj = new JSONObject(result);
                        if (obj.getString("HTTPCode").equals("200")) {
                            editor.putString("Name", obj.getString("Name"));
                            editor.putString("Username", obj.getString("Username"));
                            editor.putString("Password", obj.getString("Password"));
                            editor.apply();

                            getDataFromAPI(obj.getString("Id"));

                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
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
                }
            });
        });
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

    private void getDataFromAPI(String userId){
        //TODO: Need to implement this. The client must be able to get the user data from the API and stored it in the local database.
        /*
        OutsystemsAPI.getUserData(userId, LoginActivity.this, new OutsystemsAPI.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                try {
                    JSONObject obj = new JSONObject(result);
                    if (obj.getString("HTTPCode").equals("200")) {
                        editor.putString("Name", obj.getString("Name"));
                        editor.putString("Username", obj.getString("Username"));
                        editor.putString("Password", obj.getString("Password"));
                        editor.apply();

                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
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
            }
        });
        */
    }
}