package com.cristaudoenrico.emilio;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class Registration extends AppCompatActivity {

    TextView tw_login_now, error_msg;
    EditText username, name, password;
    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        tw_login_now = findViewById(R.id.tw_login_now);
        tw_login_now.setOnClickListener(view -> {
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
            finish();
        });

        username = findViewById(R.id.inputUsername);
        name = findViewById(R.id.inputName);
        password = findViewById(R.id.inputPassword);
        submit = findViewById(R.id.submit);
        error_msg = findViewById(R.id.error_msg);

        submit.setOnClickListener(view -> {
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            String URL = "http://cristaudoenrico.altervista.org/android/emilio/api/login_registration.php";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, response -> {
                if(response.equals("Registrazione avvenuta con successo!")) {
                    Intent login = new Intent(Registration.this, Login.class);
                    startActivity(login);
                    finish();
                } else if(response.equals("Username esistente.")) {
                    error_msg.setText(response);
                    error_msg.setVisibility(View.VISIBLE);
                } else if(response.equals("Tutti i campi sono obbligatori")) {
                    error_msg.setText(response);
                    error_msg.setVisibility(View.VISIBLE);
                }

            }, error -> {
                Log.i("TEST",""+error);
            }){
                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> param = new HashMap<>();
                    param.put("action", "register");
                    param.put("name", String.valueOf(name.getText()));
                    param.put("username", String.valueOf(username.getText()));
                    param.put("password", String.valueOf(password.getText()));
                    return param;
                }
            };
            queue.add(stringRequest);
        });
    }
}