package com.chrisnevers.textbooks;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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

public class InventoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
        final String isbn = getIntent().getStringExtra("isbn");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("copies").document(isbn).collection("copies")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        LinearLayout wrapper = (LinearLayout) findViewById(R.id.layout_wrapper);

                        TextView Tseller = createTextView("Seller:");
                        TextView Tcondition = createTextView("Condition:");
                        TextView Tprice = createTextView("ChaChing");
                        LinearLayout title = new LinearLayout(getApplicationContext());

                        Tseller.setTextSize(20);
                        Tcondition.setTextSize(20);
                        Tprice.setTextSize(20 );

                        title.addView(Tseller);
                        title.addView(Tcondition);
                        title.addView(Tprice);

                        wrapper.addView(title);

                        if(task.isSuccessful()){
                            for(DocumentSnapshot document : task.getResult()) {
                                String copy = document.getId();
                                TextView conditionTV = createTextView(document.getData().get("condition").toString());
                                TextView priceTV = createTextView(document.getData().get("price").toString());
                                TextView sellerTV = createTextView(document.getData().get("seller").toString());
                                LinearLayout bookL = new LinearLayout(getApplicationContext());

                                Button btn = new Button(getApplicationContext());
                                btn.setText("Buy");
                                setOnClick(btn, document.getData().get("seller").toString());

                                priceTV.append("\n");

                                bookL.addView(sellerTV);
                                bookL.addView(conditionTV);
                                bookL.addView(priceTV);
                                bookL.addView(btn);

                                wrapper.addView(bookL);

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

    protected TextView createTextView(String s) {
        TextView tv = new TextView(getApplicationContext());
        tv.setText(s);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT);
        params.weight = 1.0f;
        params.gravity = Gravity.TOP;
        tv.setLayoutParams(params);
        return tv;
    }
    private void setOnClick(final Button btn, final String email){
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getApplicationContext(), InventoryActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                myIntent.putExtra("email", email);
                startActivity(myIntent);
            }
        });
    }

}

