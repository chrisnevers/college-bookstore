package com.chrisnevers.textbooks;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.ThrowOnExtraProperties;

import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.Nullable;

public class SellActivity extends AppCompatActivity {

    TextView textbookView, authorView, isbnView, priceView;
    RatingBar conditionView;
    Button submit;
    String sellerEmail;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell);

        // Initialize layout views
        textbookView = findViewById(R.id.textbookName);
        authorView = findViewById(R.id.authorName);
        isbnView = findViewById(R.id.isbn);
        conditionView = findViewById(R.id.condition);
        priceView = findViewById(R.id.price);
        submit = findViewById(R.id.sellBook);

        // Set up firebase connection
        db = FirebaseFirestore.getInstance();

        // Get current user
        FirebaseAuth auth = FirebaseAuth.getInstance();
        sellerEmail = auth.getCurrentUser().getEmail();
    }

    public void sellBook (View v) {

        // Disable button to prevent multiple postings
        submit.setEnabled(false);

        // Get form data
        final String textbookName = textbookView.getText().toString();
        final String authorName = authorView.getText().toString();
        final String isbn = isbnView.getText().toString();
        final Float condition = conditionView.getRating();
        final String price = priceView.getText().toString();

        // Construct record to put in textbook database
        HashMap<String, Object> textbookFields = new HashMap<>();
        textbookFields.put("author", authorName);
        textbookFields.put("name", textbookName);

        // Store textbook in database
        db.collection("textbooks").document(isbn)
        .set(textbookFields)
        .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(SellActivity.this,
                            "Fail: Uploaded to textbooks db", Toast.LENGTH_LONG).show();
                }
            }
        });

        // Construct record to put in copies database
        HashMap<String, Object> copiesField = new HashMap<>();
        copiesField.put("condition", Float.toString(condition));
        copiesField.put("price", price);
        copiesField.put("seller", sellerEmail);

        // Store the individual copy in database
        db.collection("copies").document(isbn)
        .collection("copies")
        .add(copiesField)
        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {

                    // Store id of document we created
                    String copyId = task.getResult().getId();

                    // Construct record to store in user's postings database
                    HashMap<String, Object> sellerField = new HashMap<>();
                    sellerField.put("id", copyId);

                    // Save the id of the individual copy to the seller's profile
                    db.collection("postings").document(sellerEmail)
                    .collection("postings").document(isbn)
                    .collection("copies").add(sellerField)
                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(SellActivity.this,
                                        "Fail: Uploaded to postings db", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(SellActivity.this,
                            "Fail: Uploaded to copies db", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
