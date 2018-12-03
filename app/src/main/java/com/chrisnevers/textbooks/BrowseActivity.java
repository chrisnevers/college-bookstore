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
import android.text.Html;
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

        /**
         * Set "Pull to refresh" functionality. This allows the user to pull towards
         * the top of their screen to refresh the content on the page.
         * */
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

        /**
         * Connect to Firebase
         * */
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        /**
         * Find all the textbooks we have in our database
         * */
        db.collection("textbooks")
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    LinearLayout wrapper = (LinearLayout) findViewById(R.id.browse_wrapper);
                    if (task.isSuccessful()) {
                        /**
                         * Create a listing for each book and render it into the activity.
                         * */
                        for (DocumentSnapshot document : task.getResult()) {

                            // Create wrapper for one listing, and style appropriately
                            LinearLayout bookWrap = getBookWrapper();

                            // Vertically align the book img and text
                            LinearLayout iconTextWrap = getVerticallyCenterLayout();

                            // Get current isbn
                            final String isbn = document.getId();
                            final String author = document.getData().get("author").toString();
                            final String txtName = document.getData().get("name").toString();

                            // Create text views
                            TextView isbnTV = createTextView("ISBN:  " + isbn);
                            TextView authorTV = createTextView("Author:  " + author);
                            TextView nameTV = createTextView(txtName);

                            // Create buy now button
                            Button btn = getBuyButton(isbn);

                            // Style book name text
                            nameTV.setTextSize(24);
                            nameTV.setTypeface(Typeface.DEFAULT_BOLD);

                            // Create img
                            ImageView img = getImageView();

                            // Wrap all text views in linear layout and add margins against book
                            LinearLayout textWrap = getTextLayout();

                            // Add all views to the listing wrapper
                            addToTextLayout(isbnTV, authorTV, nameTV, textWrap);
                            addToBookTextLayout(iconTextWrap, img, textWrap);
                            addToListing(bookWrap, iconTextWrap, btn);

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

    private void addToListing(LinearLayout wrapper, LinearLayout imgTextWrapper, Button btn) {
        wrapper.addView(imgTextWrapper);
        wrapper.addView(btn);
    }

    private void addToBookTextLayout(LinearLayout wrapper, ImageView img, LinearLayout textWrapper) {
        wrapper.addView(img);
        wrapper.addView(textWrapper);
    }

    private void addToTextLayout(TextView isbnTV, TextView authorTV, TextView nameTV, LinearLayout wrapper) {
        wrapper.addView(nameTV);
        wrapper.addView(authorTV);
        wrapper.addView(isbnTV);
    }

    /**
     * Gets the image for each book and scales it
     * */
    @NonNull
    private ImageView getImageView() {
        ImageView image = new ImageView(getApplicationContext());
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.book);
        Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 200, 250, true);
        image.setImageBitmap(scaled);
        return image;
    }

    /**
     * Used to wrap all the text in a book listing
     * */
    @NonNull
    private LinearLayout getTextLayout() {
        LinearLayout layout = new LinearLayout(getApplicationContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams (WRAP_CONTENT, WRAP_CONTENT);
        params.setMargins(50, 0, 0, 20);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(params);
        return layout;
    }

    /**
     * Creates the Buy Now button, stlyes and set's onClick functionality
     * */
    @NonNull
    private Button getBuyButton(final String isbn) {
        Button button = new Button(getApplicationContext());
        button.setText("Buy Now");
        button.setBackgroundColor(getResources().getColor(R.color.primaryComplement));
        setOnClick(button, isbn);
        return button;
    }

    @NonNull
    private LinearLayout getVerticallyCenterLayout() {
        LinearLayout layout = new LinearLayout(getApplicationContext());
        layout.setGravity(CENTER_VERTICAL);
        return layout;
    }

    /**
     * Creates the linear layout wrapper for a book listing
     * */
    @NonNull
    private LinearLayout getBookWrapper() {
        LinearLayout layout = new LinearLayout(getApplicationContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
        params.setMargins(0, 100, 0, 0);
        layout.setLayoutParams(params);
        layout.setOrientation(LinearLayout.VERTICAL);
        return layout;
    }

    /**
     * Put the browse_menu on the browse_activity screen. This will give users
     * to buttons in the top right to go to their profile or sell their book.
     * */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.browse_menu, menu);
        return true;
    }

    /**
     * When the user interacts with the menu, go to the appropriate screen.
     * */
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

    /**
     * Given a string, creates a text view and does some styling.
     * */
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

    /**
     * When a user clicks on a certain book it will launch the InventoryActiviy, passing
     * the correct ISBN. Then that activity will load the corresponding information from
     * our database.
     * */
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

    /**
     * This will bring user's to the sell activity
     * */
    protected void sellBook (View v) {
        Intent myIntent = new Intent(getApplicationContext(), SellActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(myIntent);
    }
}
