package com.makinarium.makinariummasterconstroller.master;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    Button test;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //tvTest = (TextView) findViewById(R.id.textView);

        Intent headIntent = getIntent();


        test = (Button)findViewById(R.id.button);
        test.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                shortclick();
            }
        });

        test.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                longclick();
                return true;
            }
        });

        //tvTest.setText(headIntent.getStringExtra(Intent.EXTRA_TEXT));
        //hideSystemUI();

    }



    public void shortclick()
    {
        Toast.makeText(this, "performo", Toast.LENGTH_SHORT).show();

    }
    public void longclick()
    {
        Toast.makeText(this, "mo registro.", Toast.LENGTH_LONG).show();

    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                          );
    }

    // Shows the system bars by removing all the flags
// except for the ones that make the content appear under the system bars.
    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClickedId = item.getItemId();

        if (itemThatWasClickedId == R.id.action_changehead) {
            Context context = MainActivity.this;
            Intent mainActivityIntent = new Intent(context, headActivity.class);
            startActivity(mainActivityIntent);
            return true;
        }

        if (itemThatWasClickedId == R.id.action_backup) {
            Context context = MainActivity.this;
            String textToShow = "backup clicked";
            Toast.makeText(context, textToShow, Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
