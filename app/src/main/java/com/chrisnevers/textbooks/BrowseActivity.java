package com.chrisnevers.textbooks;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class BrowseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("textbooks")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            LinearLayout wrapper = (LinearLayout) findViewById(R.id.browse_wrapper);
                            for (DocumentSnapshot document : task.getResult()) {
                                LinearLayout bookL = new LinearLayout(getApplicationContext());
                                bookL.setOrientation(LinearLayout.VERTICAL);
                                final String isbn = document.getId();
                                TextView isbnTV = createTextView(isbn);
                                TextView authorTV = createTextView(document.getData().get("author").toString());
                                TextView nameTV = createTextView(document.getData().get("name").toString());
                                Button btn = new Button(getApplicationContext());
                                btn.setText("Buy Now");
                                setOnClick(btn, isbn);
                                bookL.addView(isbnTV);
                                bookL.addView(authorTV);
                                bookL.addView(nameTV);
                                bookL.addView(btn);
                                wrapper.addView(bookL);
                            }
                        } else {
                            LinearLayout wrapper = (LinearLayout) findViewById(R.id.browse_wrapper);
                            TextView tv = new TextView(getApplicationContext());
                            tv.setText("Failed to load books");
                            wrapper.addView(tv);
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


    private void setOnClick(final Button btn, final String str){
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getApplicationContext(), InventoryActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(myIntent);
            }
        });
    }
}
