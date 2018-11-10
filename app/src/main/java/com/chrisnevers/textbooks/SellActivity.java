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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

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
        textbookView = findViewById(R.id.textbookName);
        authorView = findViewById(R.id.authorName);
        isbnView = findViewById(R.id.isbn);
        conditionView = findViewById(R.id.condition);
        priceView = findViewById(R.id.price);
        submit = findViewById(R.id.sellBook);
        db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        sellerEmail = auth.getCurrentUser().getEmail();
    }

    public void sellBook (View v) {
        submit.setEnabled(false);
        Toast.makeText(SellActivity.this,
                "Checking if sold", Toast.LENGTH_SHORT).show();
            String textbookName = textbookView.getText().toString();
            String authorName = authorView.getText().toString();
            String isbn = isbnView.getText().toString();
            Float condition = conditionView.getRating();
            String price = priceView.getText().toString();

            HashMap<String, Object> textbookFields = new HashMap<>();

            textbookFields.put("author", authorName);
            textbookFields.put("name", textbookName);
            Toast.makeText(SellActivity.this,
                    "Made hashmap", Toast.LENGTH_SHORT).show();

            db.collection("textbooks").document(isbn)
                .set(textbookFields)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SellActivity.this,
                            "Success: Uploaded to textbooks db", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(SellActivity.this,
                            "Fail: Uploaded to textbooks db", Toast.LENGTH_LONG).show();
                        }
                    }
                });

        HashMap<String, Object> copiesField = new HashMap<>();

        copiesField.put("condition", Float.toString(condition));
        copiesField.put("price", price);
        copiesField.put("seller", sellerEmail);

        Toast.makeText(SellActivity.this,
                "Made hashmap", Toast.LENGTH_SHORT).show();

        db.collection("copies").document(isbn)
            .collection("copies")
            .add(copiesField)
            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(SellActivity.this,
                                "Success: Uploaded to copies db", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(SellActivity.this,
                                "Fail: Uploaded to copies db", Toast.LENGTH_LONG).show();
                    }
                }
            });
    }
}
