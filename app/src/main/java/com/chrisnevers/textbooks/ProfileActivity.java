package com.chrisnevers.textbooks;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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
        db.collection("postings/" + user.getEmail() + "/postings")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        LinearLayout wrapper = (LinearLayout) findViewById(R.id.my_books_wrapper);
                        if (task.getResult().isEmpty()) {
                            TextView isbnTV = createTextView("empty");
                            wrapper.addView(isbnTV);
                        } else {
                            for (DocumentSnapshot doc : task.getResult()) {
                                String isbn = doc.getId();
                                TextView isbnTV = createTextView(isbn);
                                wrapper.addView(isbnTV);
                            }
                        }
                    }
                });
    }

    protected TextView createTextView(String s) {
        TextView tv = new TextView(getApplicationContext());
        tv.setText(s);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT);
        params.weight = 1.0f;
        params.gravity = Gravity.TOP;
        tv.setLayoutParams(params);
        return tv;
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
