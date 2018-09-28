package com.makinarium.makinariumanimatronickeysystem;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.makinarium.makinariumanimatronickeysystem.com.makinarium.presetthings.ButtonPerformance;
import com.makinarium.makinariumanimatronickeysystem.com.makinarium.presetthings.ButtonsContainer;
import com.makinarium.makinariumanimatronickeysystem.com.makinarium.presetthings.PerformancePiece;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "MainActivity";
    private Button stopButton;
    private TimerForRecorder timeTask;
    private TextView cronometro;
    private boolean registrationMode = false;
    private boolean performanceMode = false;
    private boolean mouthActiveController = true;
    private boolean eyesActiveController = true;

    private Switch mouthSwitch;
    private Switch eyesSwitch;


    private ButtonsContainer container;
    private ButtonPerformance bInRec;
    private ButtonPerformance bInPerf;
    private long previousPerformancePieceTime = 0;
    private String headMac;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothConnectionService mBluetoothConnectionMouth;
    private BluetoothConnectionService mBluetoothConnectionHead;
    private BluetoothConnectionService mBluetoothConnectionEyes;

    private BluetoothDevice mBTMouthDevice;
    private BluetoothDevice mBTDeviceHead;
    private BluetoothDevice mBTDeviceEyes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter((Constants.incomingMessageIntent)));

        Intent headIntent = getIntent();
        headMac = headIntent.getStringExtra(Intent.EXTRA_TEXT);

        container = new ButtonsContainer();



        ButtonPerformance bp = container.getButtonPerform(R.id.eyes_01);

        cronometro = (TextView)findViewById(R.id.cronometro);
        stopButton = (Button)findViewById(R.id.stopButton);
        stopButton.setClickable(false);
        stopButton.setEnabled(false);
        stopButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                stopButton();
            }
        });


        mouthSwitch = (Switch)findViewById(R.id.mouthSwitch);
        eyesSwitch = (Switch)findViewById(R.id.eyesSwitch);

        mouthSwitch.setChecked(true);
        eyesSwitch.setChecked(true);

        mouthSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                onCheckMouth(buttonView,isChecked);
            }
        });

        eyesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                onCheckEyes(buttonView,isChecked);
            }
        });



        //connectionBluetooth();
    }


    private void connectionBluetooth()
    {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        for(BluetoothDevice bt : pairedDevices)
        {
            if (bt.getAddress().equals(Constants.macEyesBT)) {
                mBTDeviceEyes = bt;
                mBluetoothConnectionEyes = new BluetoothConnectionService(MainActivity.this, Constants.eyesID);
                startBTConnection(mBTDeviceEyes, mBluetoothConnectionEyes);
            }


            if (bt.getAddress().equals(Constants.macMouthBT)) {
                mBTMouthDevice = bt;
                mBluetoothConnectionMouth = new BluetoothConnectionService(MainActivity.this, Constants.MouthID);
                startBTConnection(mBTMouthDevice, mBluetoothConnectionMouth);
            }

            if (bt.getAddress().equals(headMac)) {
                Log.d(TAG, bt.getName());

                mBTDeviceHead = bt;
                mBluetoothConnectionHead = new BluetoothConnectionService(MainActivity.this, Constants.HeadID);
                startBTConnection(mBTDeviceHead, mBluetoothConnectionHead);
            }


        }
    }

    private void initializaAllButtons()
    {


        intitializeButton(R.id.eyes_01 , FaceSector.EYES);
        intitializeButton(R.id.eyes_02 , FaceSector.EYES);
        intitializeButton(R.id.mouth_01 , FaceSector.MOUTH);
        intitializeButton(R.id.mouth_02 , FaceSector.MOUTH);
    }

    private void intitializeButton(int id, FaceSector sector)
    {
        Button b = (Button) findViewById(id);
        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                performclick(v);
            }
        });

        b.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                recClick(v);
                return true;
            }
        });

        container.addButton(id, b, sector);
        return;
    }

    public void startBTConnection(BluetoothDevice device, BluetoothConnectionService connection)
    {
        connection.startClient(device);
    }

    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (action.equals(mBluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, mBluetoothAdapter.ERROR);

                switch(state){
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "onReceive: STATE OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING ON");
                        break;
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(timeTask != null)
            timeTask.cancel(true);

    }

    public void performclick(View v)
    {

        int id = v.getId();
        ButtonPerformance bp = container.getButtonPerform(id);
        if(!bp.canPerform())
        {
            Toast.makeText(this, Constants.emptyPerformance, Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, Constants.performing, Toast.LENGTH_SHORT).show();
        container.deactivatesButtonSectorButton(bp.getFaceSector());
        performanceMode = true;
        bInPerf = bp;
        performanceThread pt = new performanceThread();
        pt.execute(bp);

    }

    public void recClick(View v)
    {

        Toast.makeText(this, Constants.RegistrationString, Toast.LENGTH_LONG).show();
        timeTask = new TimerForRecorder();
        timeTask.execute();
        stopButton.setClickable(true);
        stopButton.setEnabled(true);
        registrationMode = true;
        bInRec = container.getButtonPerform(v.getId());
        bInRec.deletePerformance();
        previousPerformancePieceTime = System.currentTimeMillis();
    }


    public void stopButton()
    {
        timeTask.cancel(true);
        timeTask = null;
        registrationMode = false;
        bInRec = null;
        stopButton.setClickable(false);
        stopButton.setEnabled(false);
        Toast.makeText(this, Constants.stopRecording, Toast.LENGTH_LONG).show();
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

        if (itemThatWasClickedId == R.id.action_undo) {
            Context context = MainActivity.this;
            String textToShow = "undo clicked";
            Toast.makeText(context, textToShow, Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public class TimerForRecorder extends AsyncTask<String, Integer, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i(TAG, "ok");
        }

        @Override
        protected String doInBackground(String... strings) {
            long startTime = System.currentTimeMillis();
            while(!isCancelled())
            {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                long timePassed = System.currentTimeMillis() - startTime;
                timePassed /= 10;
                int centesimi = (int) timePassed % 10;
                timePassed /= 10;
                int decimi = (int) timePassed % 10;
                timePassed /= 10;
                int seconds = (int) timePassed % 60;
                int minutes = (int) timePassed / 60;
                publishProgress(minutes, seconds, decimi, centesimi);

            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values)
        {
            cronometro.setText(" " + printNumberForTimer(values[0]) + ":" + printNumberForTimer(values[1]) + ":" + values[2] + values[3] );
        }

        private String printNumberForTimer(int n)
        {
            if(n > 9)
                return ""+n;
            return "0"+n;
        }

        @Override
        protected void onPostExecute(String result) {


        }

    }


    private void addPerforamancePieceToRec(byte[] action)
    {
        int time = (int) (System.currentTimeMillis() - previousPerformancePieceTime);
        previousPerformancePieceTime = System.currentTimeMillis();

        bInRec.addPerformancePiece(action, time);
    }


    public void onCheckEyes(CompoundButton buttonView, boolean isChecked) {
        if(isChecked) {
            eyesActiveController = true;
            Toast.makeText(this, Constants.controllerActived, Toast.LENGTH_LONG).show();
        } else {
            eyesActiveController = false;
            Toast.makeText(this, Constants.controllerDisactived, Toast.LENGTH_LONG).show();
        }
    }

    public void onCheckMouth(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            mouthActiveController = true;
            Toast.makeText(this, Constants.controllerActived, Toast.LENGTH_LONG).show();
        } else {
            mouthActiveController = false;
            Toast.makeText(this, Constants.controllerDisactived, Toast.LENGTH_LONG).show();
        }
    }



    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int id = intent.getIntExtra(Constants.intentIDProp, 0);
            String text = intent.getStringExtra("Message");

            if(text.contains("ALI"))
                return;
            byte[] bytes;

            FaceSector f = FaceSector.fromChar(text.charAt(0));
            if(performanceMode)
                if(bInPerf.getFaceSector() == f)
                    return;


            switch (id){
                case Constants.MouthID:
                    if(!mouthActiveController)
                        return;


                    bytes = text.getBytes(Charset.defaultCharset());

                    if(registrationMode)
                    {

                        if(f == bInRec.getFaceSector())
                            addPerforamancePieceToRec(bytes);
                    }

                    mBluetoothConnectionHead.write(bytes);
                    break;

                case Constants.eyesID:
                    if(!eyesActiveController)
                        return;

                    bytes = text.getBytes(Charset.defaultCharset());

                    if(registrationMode)
                    {

                        if(f == bInRec.getFaceSector())
                            addPerforamancePieceToRec(bytes);
                    }

                    mBluetoothConnectionHead.write(bytes);
                    break;
                case Constants.HeadID:
                    //IMPOSSIBLE
                    break;
                default:
                    break;
            }


        }
    };



    public class performanceThread extends AsyncTask<ButtonPerformance, Integer, ButtonPerformance> {


        public performanceThread()
        {

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ButtonPerformance doInBackground(ButtonPerformance... params) {

            ButtonPerformance bp = params[0];
            List<PerformancePiece<byte[]>> performance = bp.getPerformance();
            long startTime = System.currentTimeMillis();
            boolean inPerformance = true;
            PerformancePiece<byte[]> currentPiece = performance.get(0);




            int currentIndex = 0;
            long doPerfomance = startTime + currentPiece.getMillisToAction();
            while(inPerformance)
            {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                long currentTime = System.currentTimeMillis();
                long progressTime = currentTime - startTime;
                int percentProgress = (int) ((100 * progressTime) / bp.getDuration());
                publishProgress(percentProgress);

                if(currentTime > doPerfomance)
                {
                    //Log.i(TAG, currentPiece.getAction());
                    mBluetoothConnectionHead.write(currentPiece.getAction());
                    currentIndex++;

                    if(currentIndex >= performance.size())
                        inPerformance = false;
                    else
                    {
                        currentPiece = performance.get(currentIndex);
                        doPerfomance = currentTime + currentPiece.getMillisToAction();
                    }
                }


            }
            return bp;
        }


        @Override
        protected void onProgressUpdate(Integer... values)
        {
            ProgressBar pb = findViewById(R.id.progressBar2);
            pb.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(ButtonPerformance bp) {
            container.activatesButtonSectorButton(bp.getFaceSector());
            performanceMode = false;
            bInPerf = null;
            ProgressBar pb = findViewById(R.id.progressBar2);
            pb.setProgress(0);
        }

    }

}
