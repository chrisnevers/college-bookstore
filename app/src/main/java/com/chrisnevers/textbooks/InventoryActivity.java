package com.chrisnevers.textbooks;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

public class InventoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
        String isbn = getIntent().getStringExtra("isbn");
        LinearLayout wrapper = (LinearLayout) findViewById(R.id.layout_wrapper);
        TextView tv = new TextView(this);
        tv.setText(isbn);
        wrapper.addView(tv);
    }
}
