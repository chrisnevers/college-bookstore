package com.chrisnevers.textbooks;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class BrowseActivity extends AppCompatActivity {

    TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);
        mTextView = (TextView) findViewById(R.id.browseText);
        mTextView.setText("initializing");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("textbooks")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            StringBuilder s = new StringBuilder();
                            for (DocumentSnapshot document : task.getResult()) {
                                s.append(document.getId());
                                s.append(" : ");
                                s.append(document.getData().toString());
                            }
                            mTextView.setText(s.toString());
                        } else {
                            mTextView.setText("reading failed");
                        }
                    }
                });
    }

}
