package com.chrisnevers.textbooks;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import static android.view.Gravity.CENTER_VERTICAL;

public class InventoryActivity extends AppCompatActivity {

    String _isbn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        /**
         *Receive ISBN Browse section and save it to a string
         * */
        final String isbn = getIntent().getStringExtra("isbn");
        _isbn = isbn;

        /**
         * Connect to Firebase
         */
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        /**
         * Set a collection path that retrieves copies of the of the book by ISBN
         */
        db.collection("copies")
            .document(isbn)
            .collection("copies")
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    LinearLayout wrapper = (LinearLayout) findViewById(R.id.layout_wrapper);

                    if(task.isSuccessful()){
                        for(DocumentSnapshot document : task.getResult()) {

                            /**
                             * Render the available copies of the books
                             */
                            final String condition = document.getData().get("condition").toString();
                            final String price = document.getData().get("price").toString();
                            final String seller = document.getData().get("seller").toString();

                            TextView conditionTV = createTextView("<b> Condition: <b>" + condition);
                            TextView priceTV = createTextView("<b> Price: <b>$" + price);
                            priceTV.append("\n");

                            LinearLayout text = getLinearLayout();

                            Button btn = getButton(condition, price, seller);

                            LinearLayout iconTextWrap = new LinearLayout(getApplicationContext());
                            iconTextWrap.setGravity(CENTER_VERTICAL);
                            iconTextWrap.setPadding(100,0,0,0);

                            ImageView img = getImageView();

                            LinearLayout book = getCopyLayout();

                            iconTextWrap.addView(img);
                            text.addView(conditionTV);
                            text.addView(priceTV);
                            iconTextWrap.addView(text);
                            book.addView(iconTextWrap);
                            book.addView(btn);


                            wrapper.addView(book);

                        }
                    }
                    else{
                        TextView tv = new TextView(getApplicationContext());
                        tv.setText("Failed to load inventory");
                        wrapper.addView(tv);
                    }
                }
            });


    }

    /**
     *Set  the copy of books layout and style then return
     */

    @NonNull
    private LinearLayout getCopyLayout() {
        LinearLayout book = new LinearLayout(getApplicationContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
        params.setMargins(0,50,0,50);
        book.setLayoutParams(params);
        book.setOrientation(LinearLayout.VERTICAL);
        book.setGravity(CENTER_VERTICAL);
        return book;
    }

    /**
     Set the button functionality on click
     */

    @NonNull
    private Button getButton(String condition, String price, String seller) {
        Button btn = new Button(getApplicationContext());
        btn.setBackgroundColor(getResources().getColor(R.color.primaryComplement));
        btn.setText("Buy");
        setOnClick(btn, seller, condition, price);
        return btn;
    }

    /**
     *Get the image of book and set the size and style
     */
    @NonNull
    private ImageView getImageView() {
        ImageView img = new ImageView(getApplicationContext());
        Bitmap bmap = BitmapFactory.decodeResource(getResources(), R.drawable.book);
        Bitmap scaled = Bitmap.createScaledBitmap(bmap, 150, 200, true);
        img.setImageBitmap(scaled);
        return img;
    }

    /**
     *Set the Linear Layout for the text
     */

    @NonNull
    private LinearLayout getLinearLayout() {
        LinearLayout text = new LinearLayout(getApplicationContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(150,10,0,0);
        text.setLayoutParams(params);
        text.setOrientation(LinearLayout.VERTICAL);
        return text;
    }

    /**
     *String is stored into a created textview; String consists book information
     */

    protected TextView createTextView(String s) {
        TextView tv = new TextView(getApplicationContext());
        tv.setTextSize(18);
        tv.setText(Html.fromHtml(s));
        return tv;
    }

    /**
     *Set button to call onClick function when clicked on
     */

    private void setOnClick(final Button btn, final String email, final String condition, final String price){
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createEmail(condition, price, email);
            }
        });
    }

    /**
     *Get the condition, price, and sellers email address on click. Once gathered,
     *open up phone default mail app and insert sellers email address,  a want to buy
     * message and a messege of the books condition isbn,condition, and price all in the
     * correct fields.
     */

    private void createEmail(String condition, String price, String email) {
        Intent myIntent = new Intent(Intent.ACTION_SENDTO);
        String message = createEmailMsg(condition, price);
        myIntent.setData(Uri.parse("mailto:"));

        String header = " Buyer wants to purchase one of your books: " + _isbn;
        myIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        myIntent.putExtra(Intent.EXTRA_SUBJECT, header);
        myIntent.putExtra(Intent.EXTRA_TEXT, message);

        if (myIntent.resolveActivity(getPackageManager()) != null) {
             startActivity(myIntent);
        }
    }

    /**
     *Default message that has books isbn, price and condition.
     */

    private String createEmailMsg(final String condition, final String price){
        String message = "Hello, I saw your posting on College Bookstore. I would like to buy" +
                " your copy of " + _isbn + " for $" + price + "\n\nThanks";
        return message;
    }


}

