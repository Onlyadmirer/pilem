package com.example.pilem.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.pilem.MainActivity;
import com.example.pilem.R;
import com.example.pilem.data.local.UserSession;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        UserSession userSession = new UserSession(this);
        if (userSession.isLoggedIn()) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
            return;
        }
        
        setContentView(R.layout.activity_login);
    }
}
