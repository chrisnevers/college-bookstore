package com.chrisnevers.textbooks;

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
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    TextView etUsername, etEmail, etPassword;
    Button btLogin;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();

        etUsername  = (TextView) findViewById(R.id.etUsername);
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
                            Toast.makeText(MainActivity.this, "Authentication failed: " + msg,
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    public void signUpUser (View v) {
        final String name     = etUsername.getText().toString();
        final String email    = etEmail.getText().toString();
        final String password = etPassword.getText().toString();
        Toast.makeText(MainActivity.this, "Name: " + name, Toast.LENGTH_SHORT).show();

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success");
                        final FirebaseUser user = auth.getCurrentUser();
                        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                            .setDisplayName(name).build();

                        user.updateProfile(profileUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                updateUI(user);
                            }
                        });
                    } else {
                        // If sign in fails, display a message to the user.
                        String msg = task.getException().getMessage();
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(MainActivity.this, "Authentication failed: " + msg,
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
            Toast.makeText(MainActivity.this, "Welcome " + name + " : " + email, Toast.LENGTH_LONG).show();
        }
    }

}
