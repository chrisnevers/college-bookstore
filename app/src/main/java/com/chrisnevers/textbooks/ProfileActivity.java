package com.chrisnevers.textbooks;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        setProfileHeaders(user);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("postings")
                .document(user.getEmail())
                .collection("postings")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        LinearLayout wrapper = (LinearLayout) findViewById(R.id.my_books_wrapper);
                        if (task.isSuccessful()) {

                        } else {

                        }
                    }
                });
    }

    protected void setProfileHeaders (FirebaseUser user) {
        String userName = user.getDisplayName();
        String userEmail = user.getEmail();
        TextView nameView = (TextView) findViewById(R.id.user_name);
        TextView emailView = (TextView) findViewById(R.id.email);
        nameView.setText(userName);
        emailView.setText(userEmail);
    }
}
