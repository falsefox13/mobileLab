package com.example.mobilelab;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        TextView welcomeText = findViewById(R.id.welcome_text);
        if (user != null) {
            final String username = user.getDisplayName();
            if (username != null) {
                welcomeText.setText(String.format("Welcome, %s", username));
            }
        }

        findViewById(R.id.btn_signout).setOnClickListener(v -> {
            auth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
        });
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
