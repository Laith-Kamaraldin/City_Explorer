package com.laithkamaraldin.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class CategoryView extends AppCompatActivity {

    ImageView backBtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_view);

        //hooks to the XML file
        backBtn = findViewById(R.id.back_dashboard);

        backBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                CategoryView.super.onBackPressed();
                //this functions checks the stack for history of viewed pages and goes back to the element in the stack before this one.
            }
        });

    }
}
