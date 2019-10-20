package com.example.mobilelab;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {
    private TextInputLayout emailField;
    private TextInputLayout nameField;
    private TextInputLayout phoneField;
    private TextInputLayout passField;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);

        auth = FirebaseAuth.getInstance();

        initFields();

        findViewById(R.id.btn_sign_up).setOnClickListener(v -> {
            final String email = Objects.requireNonNull(emailField.getEditText()).getText().toString();
            final String name = Objects.requireNonNull(nameField.getEditText()).getText().toString();
            final String phone = Objects.requireNonNull(phoneField.getEditText()).getText().toString();
            final String pass = Objects.requireNonNull(passField.getEditText()).getText().toString();
            signUp(email, name, phone, pass);
        });

        findViewById(R.id.link_login).setOnClickListener(v -> startActivity(new Intent(this, LoginActivity.class)));
    }

    private void signUp(final String email, final String name, final String phone, final String pass) {
        if (!validate(email, name, phone, pass)) {
            return;
        }

        auth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        onCreateSuccess(name);
                    } else {
                        onCreateError();
                    }
                });
    }

    private void onCreateSuccess(final String name) {
        FirebaseUser user = auth.getCurrentUser();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name).build();
        if (user != null) {
            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(this, MainActivity.class));
                        }
                    });
        }
    }

    private void onCreateError() {
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(R.string.sign_up_failed);
        alertDialog.setMessage(getString(R.string.sign_up_explanation));
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                (dialog, which) -> dialog.dismiss());
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    private void initFields() {
        emailField = findViewById(R.id.email_wrapper);
        nameField = findViewById(R.id.name_wrapper);
        phoneField = findViewById(R.id.phone_wrapper);
        passField = findViewById(R.id.pass_wrapper);
    }

    public boolean validate(final String email, final String name, final String phone, final String password) {
        boolean valid = true;
        if (Utils.validateEmail(email)) {
            emailField.setError(null);
        } else {
            emailField.setError(getString(R.string.email_error));
            valid = false;
        }

        if (Utils.validatePassword(password)) {
            passField.setError(null);
        } else {
            passField.setError(getString(R.string.password_error));
            valid = false;
        }

        if (Utils.validatePhone(phone)) {
            phoneField.setError(null);
        } else {
            phoneField.setError(getString(R.string.phone_error));
            valid = false;
        }

        if (Utils.validateName(name)) {
            nameField.setError(null);
        } else {
            nameField.setError(getString(R.string.name_error));
            valid = false;
        }

        return valid;
    }
}
