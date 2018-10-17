package com.makinarium.makinariumanimatronickeysystem;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

public class headActivity extends AppCompatActivity {

    private Button continueButton;
    private String headMac;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_head);
        continueButton = (Button)findViewById(R.id.continuebutton);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ctnBtn(v);
            }
        });

        continueButton.setEnabled(false);
        continueButton.setClickable(false);

    }


    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        int i = view.getId();
        if (i == R.id.head01button) {
            if (checked) {
                continueButton.setEnabled(true);
                continueButton.setClickable(true);
                headMac = Constants.macHead01BT;
                //continueButton.setBackgroundColor(ContextCompat.getColor(this, R.color.activePerform)); SBAGLIATO
                //continueButton.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);

            }

        } else if (i == R.id.head02button) {
            if (checked)
            {
                continueButton.setEnabled(true);
                continueButton.setClickable(true);
                headMac = Constants.macHead02BT;
                //continueButton.setAlpha(.5f);
            }

        }
    }



    private void ctnBtn(View view){

        Intent mainActivityIntent = new Intent(headActivity.this, MainActivity.class);

        mainActivityIntent.putExtra(Intent.EXTRA_TEXT, headMac);

        startActivity(mainActivityIntent);
    }
}
