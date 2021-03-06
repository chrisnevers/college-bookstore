package com.chrisnevers.textbooks;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class ProfileActivity extends AppCompatActivity {

    FirebaseFirestore db;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        setProfileHeaders(user);

        db = FirebaseFirestore.getInstance();
        db.collection("postings/" + user.getEmail() + "/postings")
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    LinearLayout wrapper = (LinearLayout) findViewById(R.id.my_books_wrapper);
                    if (task.getResult().isEmpty()) {
                        TextView postingInfo = (TextView) findViewById (R.id.postingInfo);
                        postingInfo.setText("You do not have any postings");
                    } else {
                        for (DocumentSnapshot doc : task.getResult()) {
                            addBookView (wrapper, doc);
                        }
                    }
                }
            });
    }

    protected void addBookView (final LinearLayout wrapper, DocumentSnapshot doc) {
        final String isbn = doc.getData().get("isbn").toString();
        final String copyId = doc.getData().get("id").toString();
        final String postingId = doc.getId();
        db.collection("textbooks")
            .document(isbn)
            .get()
            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        LinearLayout wrap = new LinearLayout(getApplicationContext());
                        wrap.setOrientation(LinearLayout.VERTICAL);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params.setMargins(0, 0, 0, 100);
                        wrap.setLayoutParams(params);
                        Map<String, Object> result = task.getResult().getData();
                        String author = result.get("author").toString();
                        TextView authorTV = createTextView("Author: " + author);
                        String txtbook = result.get("name").toString();
                        TextView txtbookTV = createTextView(txtbook);
                        TextView isbnTV = createTextView("ISBN:" + isbn);
                        txtbookTV.setTextSize(20);
                        txtbookTV.setTypeface(Typeface.DEFAULT_BOLD);
                        authorTV.setTextSize(16);
                        isbnTV.setTextSize(16);
                        isbnTV.setPadding(0,0,0, 10);

                        Button delete = createButton(isbn, postingId, copyId);
                        wrap.addView(txtbookTV);

                        wrap.addView(authorTV);
                        wrap.addView(isbnTV);
                        wrap.addView(delete);
                        wrapper.addView(wrap);
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

    protected Button createButton (String isbn, String postingId, String copyId) {
        Button btn = new Button(getApplicationContext());
        btn.setText("Delete Posting");
        btn.setBackgroundColor(getResources().getColor(R.color.primaryComplement));
        setOnClick(btn, isbn, postingId, copyId);
        return btn;
    }

    protected void setOnClick (Button btn, final String isbn, final String postingId,
                               final String copyId) {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.document("copies/" + isbn + "/copies/" + copyId)
                    .delete();
                db.document("postings/" + email + "/postings/" + postingId)
                    .delete();
//                Maybe delete textbook collection if no copies left.
                db.collection("copies/" + isbn + "/copies")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().size() == 0) {
                                db.document("textbooks/" + isbn).delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(getApplicationContext(), "Successfully deleted posting",
                                                Toast.LENGTH_SHORT).show();
                                        refresh ();
                                    }
                                });
                            } else {
                                Toast.makeText(getApplicationContext(), "Successfully deleted posting",
                                        Toast.LENGTH_SHORT).show();
                                refresh ();
                            }
                        }
                    }
                });
            }
        });
    }

    protected void setProfileHeaders (FirebaseUser user) {
        String userName = user.getDisplayName();
        email = user.getEmail();
        TextView nameView = (TextView) findViewById(R.id.user_name);
        TextView emailView = (TextView) findViewById(R.id.email);
        nameView.setText(userName);
        nameView.setTextSize(36);
        nameView.setTypeface(Typeface.DEFAULT_BOLD);
        emailView.setText(email);
        emailView.setTextSize(24);
        emailView.setTypeface(Typeface.DEFAULT_BOLD);
    }

    protected void refresh () {
        finish();
        startActivity(getIntent());
    }
}
