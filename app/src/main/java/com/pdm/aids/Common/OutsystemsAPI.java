package com.pdm.aids.Common;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OutsystemsAPI extends AppCompatActivity {

    String apiUrl = "https://personal-8o07igno.outsystemscloud.com/AIDS/rest/RestAPI/";

    public String checkLogin(String username, String password) {
        String url = apiUrl + "CheckLogin?Username=" + username + "&Password=" + password;
        String[] resultCode = {""};

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            resultCode[0] = obj.getString("HTTPCode");
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                resultCode[0] = "500";
            }
        });
        queue.add(stringRequest);
        return resultCode[0];
    }

}
