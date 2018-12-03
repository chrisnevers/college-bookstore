package com.chrisnevers.textbooks;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.List;

import static android.view.Gravity.BOTTOM;
import static android.view.Gravity.CENTER;
import static android.view.Gravity.CENTER_HORIZONTAL;
import static android.view.Gravity.CENTER_VERTICAL;
import static android.view.Gravity.LEFT;
import static android.view.Gravity.RIGHT;

public class InventoryActivity extends AppCompatActivity {

    String _isbn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
        final String isbn = getIntent().getStringExtra("isbn");
        _isbn = isbn;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("copies").document(isbn).collection("copies")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        LinearLayout wrapper = (LinearLayout) findViewById(R.id.layout_wrapper);


                        if(task.isSuccessful()){
                            for(DocumentSnapshot document : task.getResult()) {
                                String copy = document.getId();
                                TextView conditionTV = conditionView(document.getData().get("condition").toString());
                                TextView priceTV = priceView(document.getData().get("price").toString());
                                TextView sellerTV = nameView(document.getData().get("seller").toString());

                                LinearLayout text = new LinearLayout(getApplicationContext());
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                params.setMargins(150,10,0,0);
                                text.setLayoutParams(params);
                                text.setOrientation(LinearLayout.VERTICAL);


                                Button btn = new Button(getApplicationContext());
                                btn.setBackgroundColor(getResources().getColor(R.color.primaryComplement));
                                btn.setText("Buy");
                                setOnClick(btn, document.getData().get("seller").toString(), document.getData().get("seller").toString(), document.getData().get("condition").toString(),
                                        document.getData().get("price").toString(), isbn);

                                LinearLayout iconTextWrap = new LinearLayout(getApplicationContext());
                                iconTextWrap.setGravity(CENTER_VERTICAL);
                                iconTextWrap.setPadding(100,0,0,0);

                                ImageView img = new ImageView(getApplicationContext());
                                Bitmap bmap = BitmapFactory.decodeResource(getResources(), R.drawable.book);
                                Bitmap scaled = Bitmap.createScaledBitmap(bmap, 150, 200, true);
                                img.setImageBitmap(scaled);

                                LinearLayout book = new LinearLayout(getApplicationContext());
                                LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
                                params2.setMargins(0,50,0,0);
                                book.setLayoutParams(params2);
                                book.setOrientation(LinearLayout.VERTICAL);
                                book.setGravity(CENTER_VERTICAL);


                                priceTV.append("\n");

                                iconTextWrap.addView(img);
                                text.addView(sellerTV);
                                text.addView(conditionTV);
                                text.addView(priceTV);
                                iconTextWrap.addView(text);
                                book.addView(iconTextWrap);
                                book.addView(btn);


                                wrapper.addView(book);

                            }
                        }
                        else{
                            //LinearLayout wrapper = (LinearLayout) findViewById(R.id.layout_wrapper);
                            TextView tv = new TextView(getApplicationContext());
                            tv.setText("Failed to load inventory");
                            wrapper.addView(tv);

                        }

                    }
                });


    }

    protected TextView priceView(String s) {
        TextView tv = new TextView(getApplicationContext());
        String price = "<b> Price: <b>";
        tv.setTextSize(18);
        tv.setText(Html.fromHtml(price));
        tv.append(s);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT);
        params.weight = 1.0f;
        params.gravity = Gravity.LEFT;
        params.setMargins(0,0,0,0);
        tv.setLayoutParams(params);
        return tv;
    }
    protected TextView conditionView(String s) {
        TextView tv = new TextView(getApplicationContext());
        String condition = "<b> Condition: <b>";
        tv.setTextSize(18);
        tv.setText(Html.fromHtml(condition));
        tv.append(s);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT);
        params.weight = 1.0f;
        params.gravity = Gravity.LEFT;
        params.setMargins(0,0,0,0);
        tv.setLayoutParams(params);
        return tv;
    }
    protected TextView nameView(String s) {
        TextView tv = new TextView(getApplicationContext());
        String name = "<b> Name: <b>";
        tv.setTextSize(18);
        tv.setText(Html.fromHtml(name));
        tv.append(s);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT);
        params.weight = 1.0f;
        params.gravity = Gravity.LEFT;
        params.setMargins(0,0,0,0);
        tv.setLayoutParams(params);
        return tv;
    }

    private void setOnClick(final Button btn, final String name, final String email, final String condition, final String price, final String isbn){
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(Intent.ACTION_SENDTO);
                String message = createEmail(name, condition, price);
                myIntent.setData(Uri.parse("mailto:"));
                myIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
                myIntent.putExtra(Intent.EXTRA_SUBJECT, " Buyer wants to purchase one of your books: " + isbn );
                myIntent.putExtra(Intent.EXTRA_TEXT, message);

               if (myIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(myIntent);
               }

            }
        });
    }

    private String createEmail(final String email, final String condition, final String price){
        String message = "Hello, I saw your posting on College Bookstore. I would like to buy" +
                " your copy of " + _isbn + " for " + price + "\n\nThanks";
        return message;
    }


}

