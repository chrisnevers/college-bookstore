package com.chrisnevers.textbooks;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import static android.view.Gravity.CENTER_VERTICAL;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class BrowseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);
        final SwipeRefreshLayout pullToRefresh = (SwipeRefreshLayout) findViewById(R.id.pullRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullToRefresh.setRefreshing(false);
                pullToRefresh.setEnabled(false);
                Intent myIntent = getIntent().addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(myIntent);
            }
        });

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("textbooks")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        LinearLayout wrapper = (LinearLayout) findViewById(R.id.browse_wrapper);
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {

                                // Create wrapper for one listing, and style appropriately
                                LinearLayout bookWrap = new LinearLayout(getApplicationContext());
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
                                params.setMargins(0, 100, 0, 0);
                                bookWrap.setLayoutParams(params);
                                bookWrap.setOrientation(LinearLayout.VERTICAL);

                                // Vertically align the book img and text
                                LinearLayout iconTextWrap = new LinearLayout(getApplicationContext());
                                iconTextWrap.setGravity(CENTER_VERTICAL);

                                // Get current isbn
                                final String isbn = document.getId();

                                // Create text views
                                TextView isbnTV = createISPN(isbn);
                                TextView authorTV = createAuthorView(document.getData().get("author").toString());
                                TextView nameTV = createTextView(document.getData().get("name").toString());

                                // Create buy now button
                                Button btn = new Button(getApplicationContext());
                                btn.setText("Buy Now");
                                btn.setBackgroundColor(getResources().getColor(R.color.primaryComplement));
                                setOnClick(btn, isbn);

                                // Style book name text
                                nameTV.setTextSize(24);
                                nameTV.setTypeface(Typeface.DEFAULT_BOLD);

                                // Create img
                                ImageView img = new ImageView(getApplicationContext());
                                Bitmap bmap = BitmapFactory.decodeResource(getResources(), R.drawable.book);
                                Bitmap scaled = Bitmap.createScaledBitmap(bmap, 200, 250, true);
                                img.setImageBitmap(scaled);

                                // Wrap all text views in linear layout and add margins against book
                                LinearLayout textWrap = new LinearLayout(getApplicationContext());
                                LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams (WRAP_CONTENT, WRAP_CONTENT);
                                textParams.setMargins(50, 0, 0, 20);
                                textWrap.setOrientation(LinearLayout.VERTICAL);
                                textWrap.setLayoutParams(textParams);

                                // Add all views to the listing wrapper
                                iconTextWrap.addView(img);
                                textWrap.addView(nameTV);
                                textWrap.addView(authorTV);
                                textWrap.addView(isbnTV);
                                iconTextWrap.addView(textWrap);
                                bookWrap.addView(iconTextWrap);
                                bookWrap.addView(btn);

                                // Add listing wrapper to global wrapper
                                wrapper.addView(bookWrap);
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
