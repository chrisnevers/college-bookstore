package com.chrisnevers.textbooks;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    TextView etEmail, etPassword;
    Button btLogin;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        etEmail     = (TextView) findViewById(R.id.etEmail);
        etPassword  = (TextView) findViewById(R.id.etPassword);
        btLogin     = (Button) findViewById(R.id.btLogin);
    }

    public void loginUser (View v) {
        String email    = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = auth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            String msg = task.getException().getMessage();
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed: " + msg,
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    public void updateUI (FirebaseUser user) {
        if (user == null) {
            return;
        } else {
            String name = user.getDisplayName();
            String email = user.getEmail();
            Toast.makeText(LoginActivity.this, "Welcome " + name + " : " + email, Toast.LENGTH_LONG).show();
            setContentView(R.layout.activity_browse);
        }
    }

    public void goToSignup (View v) {
        Intent myIntent = new Intent(this, SignupActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(myIntent);
    }
}
