package com.makinarium.makinariummasterconstroller.master;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

public class headActivity extends AppCompatActivity {

    private Button continueButton;
    private String headMac;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("PORCO", "IDDIO");
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
                Log.d("HAO", "E mo?");
                continueButton.setEnabled(true);
                continueButton.setClickable(true);
                headMac = Constants.macHead01BT;
            }

        } else if (i == R.id.head02button) {
            if (checked)
            {
                continueButton.setEnabled(true);
                continueButton.setClickable(true);
                headMac = Constants.macHead02BT;
            }

        }
    }



    private void ctnBtn(View view){
        Log.d("CIAO", "DAJE");

        Intent mainActivityIntent = new Intent(headActivity.this, MainActivity.class);

        mainActivityIntent.putExtra(Intent.EXTRA_TEXT, headMac);

        startActivity(mainActivityIntent);
    }
}
