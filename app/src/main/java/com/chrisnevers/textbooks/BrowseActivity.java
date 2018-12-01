package com.chrisnevers.textbooks;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

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
                        LinearLayout wrapper = (LinearLayout) findViewById(R.id.browse_wrapper);
                        if (task.isSuccessful()) {
                            for (int i = 0; i < 10; i++) {
                                for (DocumentSnapshot document : task.getResult()) {
                                    LinearLayout bookL = new LinearLayout(getApplicationContext());
                                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT);
                                    params.setMargins(0,0,0,200);
                                    bookL.setLayoutParams(params);
                                    bookL.setOrientation(LinearLayout.VERTICAL);

                                    final String isbn = document.getId();
                                    TextView isbnTV = createISPN(isbn);
                                    TextView authorTV = createAuthorView(document.getData().get("author").toString());
                                    TextView nameTV = createTextView(document.getData().get("name").toString());

                                    Button btn = new Button(getApplicationContext());
                                    btn.setText("Buy Now");
                                    btn.setBackgroundColor(getResources().getColor(R.color.primaryComplement));
                                    ViewGroup.LayoutParams params2 = new ViewGroup.LayoutParams(400, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    btn.setLayoutParams(params2);
                                    setOnClick(btn, isbn);

                                    nameTV.setTextSize(25);
                                    nameTV.setTypeface(Typeface.DEFAULT_BOLD);

                                    bookL.addView(nameTV);
                                    bookL.addView(authorTV);
                                    bookL.addView(isbnTV);
                                    bookL.addView(btn);
                                    wrapper.addView(bookL);
                                }
                            }
                        } else {
                            TextView tv = new TextView(getApplicationContext());
                            tv.setText("Failed to load books");
                            wrapper.addView(tv);
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.browse_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Toast.makeText(this, "Selected Item: " +item.getTitle(), Toast.LENGTH_SHORT).show();
        switch (item.getItemId()) {
            case R.id.goto_profile: {
                Intent myIntent = new Intent(getApplicationContext(), ProfileActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(myIntent);
                return true;
            }
            case R.id.goto_sell: {
                Intent myIntent = new Intent(getApplicationContext(), SellActivity.class)
                                        .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(myIntent);
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    protected TextView createTextView(String s) {
        TextView tv = new TextView(getApplicationContext());
        tv.append(s);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT);
        params.weight = 1.0f;
        params.gravity = Gravity.TOP;
        params.setMargins(0,0,0,10);
        tv.setLayoutParams(params);
        return tv;
    }

    protected TextView createAuthorView(String s) {
        TextView tv = new TextView(getApplicationContext());
        String author = "Author:  ";
        tv.setText(author);
        tv.append(s);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT);
        params.weight = 1.0f;
        params.gravity = Gravity.TOP;
        params.setMargins(0,0,0,10);
        tv.setLayoutParams(params);
        return tv;
    }

    protected TextView createISPN(String s) {
        TextView tv = new TextView(getApplicationContext());
        tv.setText("ISBN:   ");
        tv.append(s);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT);
        params.weight = 1.0f;
        params.gravity = Gravity.TOP;
        params.setMargins(0,0,0,10);
        tv.setLayoutParams(params);
        return tv;
    }


    private void setOnClick(final Button btn, final String isbn){
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getApplicationContext(), InventoryActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                myIntent.putExtra("isbn", isbn);
                startActivity(myIntent);
            }
        });
    }

    protected void sellBook (View v) {
        Intent myIntent = new Intent(getApplicationContext(), SellActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(myIntent);
    }
}
