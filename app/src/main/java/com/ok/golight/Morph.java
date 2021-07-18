package com.ok.golight;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Morph extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.morph_layout);
        FloatingActionButton fab = findViewById(R.id.fab_mo);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!fab.isExpanded()) {
                    fab.setExpanded(true);
                }
                else {
                    fab.setExpanded(false);
                }
            }
        });
    }
}
