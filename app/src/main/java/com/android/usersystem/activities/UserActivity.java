package com.android.usersystem.activities;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import java.util.zip.Inflater;

public class UserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        if (!SharedPrefManager.getInstance(getApplicationContext()).isLogged()) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));

            return;
        }

        final TextView tvWelcome = (TextView) findViewById(R.id.tvWelcome);
        final TextView tvId = (TextView) findViewById(R.id.tvId);
        final TextView tvUsername = (TextView) findViewById(R.id.tvUsername);
        TextView dEditUsername = (TextView) findViewById(R.id.dEditUsername);
        TextView tvEmail = (TextView) findViewById(R.id.tvEmail);


        final User user = SharedPrefManager.getInstance(this).getUser();

        tvWelcome.setText("Labas, " + user.getUsername());
        tvId.setText(String.valueOf(user.getId()));
        tvUsername.setText(user.getUsername());
        tvEmail.setText(user.getEmail());

        dEditUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(UserActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.layout_change_name, null);
                final EditText etChangeName = mView.findViewById(R.id.etChangeName);
                final Button bCancel = mView.findViewById(R.id.bCancel);
                final Button bAccept = mView.findViewById(R.id.bAccept);


                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();

                bCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                bAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String newUsername = etChangeName.getText().toString();
                        if (TextUtils.isEmpty(newUsername)) {
                            etChangeName.setError("Įveskite vardą!");
                            etChangeName.requestFocus();

                            return;
                        } else {
                            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL.URL_CHANGENAME,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            try {
                                                JSONObject obj = new JSONObject(response);
                                                // Refresh user name and welcome message
                                                user.setUsername(newUsername);
                                                tvUsername.setText(user.getUsername());
                                                tvWelcome.setText("Labas, " + user.getUsername());

                                                if (!obj.getBoolean("error")) {
                                                    user.setUsername(newUsername);
                                                    Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                                                    JSONObject jsonUser = obj.getJSONObject("user");

                                                    User user = new User(
                                                            jsonUser.getInt("id"),
                                                            jsonUser.getString("username"),
                                                            jsonUser.getString("email")


                                                    );

                                                    SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);
                                                    dialog.dismiss();


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
                                    params.put("id", tvId.getText().toString());
                                    params.put("newusername", newUsername);

                                    return params;
                                }
                            };

                            VolleySingleton.getInstance(getApplicationContext()).addRequestQueue(stringRequest);
                        }


                    }
                });

                dialog.show();
            }
        });

        findViewById(R.id.bLogout).

                setOnClickListener(new View.OnClickListener() {
                    @Override

                    public void onClick(View v) {
                        SharedPrefManager.getInstance(getApplicationContext()).logout();
                        finish();
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    }
                });
    }
}
