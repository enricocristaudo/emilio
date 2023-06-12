package com.cristaudoenrico.emilio;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    TextView tw_register_now, error_msg;
    EditText username, name, password;
    Button submit;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        tw_register_now = findViewById(R.id.tw_register_now);
        tw_register_now.setOnClickListener(view -> {
            Intent intent = new Intent(this, Registration.class);
            startActivity(intent);
            finish();
        });
        sharedPreferences = getSharedPreferences("emilio", MODE_PRIVATE);
        username = findViewById(R.id.inputUsername);
        password = findViewById(R.id.inputPassword);
        submit = findViewById(R.id.submit);
        error_msg = findViewById(R.id.error_msg);

        if(sharedPreferences.getString("logged", "false").equals("true")) {
            Intent login = new Intent(Login.this, MainActivity.class);
            startActivity(login);
            finish();
        }

        submit.setOnClickListener(view -> {
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            String URL = "http://cristaudoenrico.altervista.org/android/emilio/api/login_registration.php";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, response -> {
                Log.i("TEST", ""+response);
                try {
                    JSONObject obj = new JSONObject(response);
                    String success = obj.getString("success");
                    String message = obj.getString("message");

                    if(success.equals("true")) {
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        String token = obj.getString("token");
                        String username = obj.getString("username");

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("logged", "true");
                        editor.putString("token", token);
                        editor.putString("username", username);
                        editor.apply();
                        Intent login = new Intent(Login.this, MainActivity.class);
                        startActivity(login);
                        finish();
                    } else {
                        error_msg.setText(message);
                        error_msg.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }, error -> {
                Log.i("TEST",""+error);
            }){
                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> param = new HashMap<>();
                    param.put("action", "login");
                    param.put("username", String.valueOf(username.getText()));
                    param.put("password", String.valueOf(password.getText()));
                    return param;
                }
            };
            queue.add(stringRequest);
        });
    }
}