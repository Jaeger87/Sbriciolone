package com.makinarium.makinariumanimatronickeysystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import com.makinarium.makinariumanimatronickeysystem.com.makinarium.utilities.Constants;
import com.makinarium.makinariumanimatronickeysystem.com.makinarium.utilities.MacAddressFactory;

public class headActivity extends AppCompatActivity {

    private Button continueButton;
    private String headMac;
    private MacAddressFactory macFactory;
    private EditText mouthEdit;
    private EditText eyesEdit;
    private EditText headEdit;


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

        continueButton.setAlpha(.5f);
        continueButton.setEnabled(false);
        continueButton.setClickable(false);

    }


    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        continueButton.setAlpha(1);
        continueButton.setEnabled(true);
        continueButton.setClickable(true);
        // Check which radio button was clicked
        int i = view.getId();
        if (i == R.id.head01button) {
            if (checked)
            {
                headMac = Constants.macHead01BT;
                //continueButton.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
            }

        } else if (i == R.id.head02button) {
            if (checked)
                headMac = Constants.macHead02BT;

        }
    }



    private void ctnBtn(View view){

        Intent mainActivityIntent = new Intent(headActivity.this, MainActivity.class);

        mainActivityIntent.putExtra(Intent.EXTRA_TEXT, headMac);

        startActivity(mainActivityIntent);
    }
}
