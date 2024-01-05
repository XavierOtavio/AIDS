package com.pdm.aids.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pdm.aids.Common.OutsystemsAPI;
import com.pdm.aids.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button btnLogin = (Button) findViewById(R.id.button_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login(view);
            }
        });
    }

    public void login(View view) {
        EditText numMecc = (EditText) findViewById(R.id.edit_text_nmechanographic);
        EditText password = (EditText) findViewById(R.id.edit_text_password);

        String responseCode = new OutsystemsAPI().checkLogin(numMecc.getText().toString(), password.getText().toString());
        Toast.makeText(getApplicationContext(), responseCode, Toast.LENGTH_SHORT).show();
    }


    /*
    public String checkLogin(String username, String password) {
        String url = apiUrl + "CheckLogin?Username=" + username + "&Password=" + password;

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            String message = obj.getString("message").equals("") ? obj.getString("error") : obj.getString("message");
                            Toast.makeText(getApplicationContext(), "R.: " + message, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), "That didn't work!", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(stringRequest);
        return "";
    }

     */

}