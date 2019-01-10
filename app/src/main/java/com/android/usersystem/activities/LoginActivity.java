package com.android.usersystem.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.usersystem.R;
import com.android.usersystem.SharedPrefManager;
import com.android.usersystem.URL;
import com.android.usersystem.VolleySingleton;
import com.android.usersystem.vo.User;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    EditText etUsername, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (SharedPrefManager.getInstance(this).isLogged()) {
            finish();
            startActivity(new Intent(getApplicationContext(), UserActivity.class));

            return;
        }
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);

        findViewById(R.id.bLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogin();
            }
        });

        findViewById(R.id.tvRegister).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });

    }

    private void userLogin() {
        final String username = etUsername.getText().toString();
        final String password = etPassword.getText().toString();

        if (TextUtils.isEmpty(username)) {
            etUsername.setError("Įveskite vardą");
            etUsername.requestFocus();

            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Įveskite slaptažodį");
            etPassword.requestFocus();

            return;
        }

        System.out.println(username);
        System.out.println(password);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL.URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = null;
                            try {
                                obj = new JSONObject(response);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if (!obj.getBoolean("error")) {
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                                JSONObject jsonUser = obj.getJSONObject("user");;

                                User user = new User(
                                        jsonUser.optInt("id"),
                                        jsonUser.optString("username"),
                                        jsonUser.optString("email")
                                );

                                SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);

                                finish();
                                startActivity(new Intent(getApplicationContext(), UserActivity.class));


                            } else {
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);

                return params;
            }
        };

        VolleySingleton.getInstance(this).addRequestQueue(stringRequest);

    }
}
