package com.makinarium.makinariummasterconstroller.master;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView tvTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvTest = (TextView) findViewById(R.id.textView);

        Intent headIntent = getIntent();

        tvTest.setText(headIntent.getStringExtra(Intent.EXTRA_TEXT));

    }
}
