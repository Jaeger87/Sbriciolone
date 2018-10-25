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
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.Gson;
import com.makinarium.makinariumanimatronickeysystem.com.makinarium.customgraphics.VerticalSeekBar;
import com.makinarium.makinariumanimatronickeysystem.com.makinarium.presetthings.ButtonPerformance;
import com.makinarium.makinariumanimatronickeysystem.com.makinarium.presetthings.ButtonsContainer;
import com.makinarium.makinariumanimatronickeysystem.com.makinarium.presetthings.PerformancePiece;
import com.makinarium.makinariumanimatronickeysystem.com.makinarium.presetthings.PresetPerformance;
import com.makinarium.makinariumanimatronickeysystem.com.makinarium.undo.UndoManager;

import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {


    private static final int stopMillisPerformance = 2;
    private static final String TAG = "MainActivity";
    private Button stopButton;
    private TimerForRecorder timeTask;
    private TextView cronometro;
    private boolean performRegistrationMode = false;
    private boolean presetRegistrationMode = false;
    private boolean mouthActiveController = true;
    private boolean eyesActiveController = true;

    private Switch mouthSwitch;
    private Switch eyesSwitch;

    private ButtonsContainer<byte[]> container;
    private UndoManager<byte[]> undoManager;
    private ButtonPerformance<byte[]> bInRec;
    private PresetPerformance<byte[]> presetInRec;
    private long timePresetRec = 0;
    private HashSet<FaceSector> performanceFilter;

    private long previousPerformancePieceTime = 0;
    private String headMac;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothConnectionService mBluetoothConnectionMouth;
    private BluetoothConnectionService mBluetoothConnectionHead;
    private BluetoothConnectionService mBluetoothConnectionEyes;

    private BluetoothDevice mBTMouthDevice;
    private BluetoothDevice mBTDeviceHead;
    private BluetoothDevice mBTDeviceEyes;


    private double multiplicator = 1;
    private VerticalSeekBar multBar;
    private Button resetM;
    private TextView multText;
    private static final int minValueMult = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter((Constants.incomingMessageIntent)));

        Intent headIntent = getIntent();
        headMac = headIntent.getStringExtra(Intent.EXTRA_TEXT);

        int readyColor = ResourcesCompat.getColor(getResources(), R.color.activePerform, null);
        int toReccolor = ResourcesCompat.getColor(getResources(), R.color.performToRec, null);

        Gson gson = new Gson();

        try (FileInputStream inputStream = new FileInputStream(this.getFilesDir() + Constants.SaveFileName)) {
            String json = IOUtils.toString(inputStream, "UTF-8");
            container = gson.fromJson(json, ButtonsContainer.class);
            initializeAllButtons();
            container.updateAllColors();
        } catch (IOException e) {
            container = new ButtonsContainer<>(readyColor, toReccolor);
            initializeAllButtons();
        }


        container = new ButtonsContainer<>(readyColor, toReccolor);
        initializeAllButtons();

        undoManager = new UndoManager<>(container);

        performanceFilter = new HashSet<>();

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

        multBar = (VerticalSeekBar) findViewById(R.id.seekBar);
        resetM = (Button) findViewById(R.id.resetM);
        resetM.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                resetSBButton();
            }
        });
        multText = (TextView) findViewById(R.id.multText);

        multBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                multiplicator = (progress + minValueMult) / 10.0;
                multText.setText(" " + String.valueOf(multiplicator) + " ");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        connectionBluetooth();
        //testButton();
    }


    private void connectionBluetooth()
    {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
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

    private void initializeAllButtons()
    {
        intitializePresetButton(R.id.preset_01,R.id.presetPB01);
        intitializePresetButton(R.id.preset_02,R.id.presetPB02);
        intitializePresetButton(R.id.preset_03,R.id.presetPB03);
        intitializePresetButton(R.id.preset_04,R.id.presetPB04);
        intitializePresetButton(R.id.preset_05,R.id.presetPB05);
        intitializePresetButton(R.id.preset_06,R.id.presetPB06);
        intitializePresetButton(R.id.preset_07,R.id.presetPB07);
        intitializePresetButton(R.id.preset_08,R.id.presetPB08);
        intitializePresetButton(R.id.preset_09,R.id.presetPB09);
        intitializePresetButton(R.id.preset_10,R.id.presetPB10);
        intitializePresetButton(R.id.preset_11,R.id.presetPB11);
        intitializePresetButton(R.id.preset_12,R.id.presetPB12);

        intitializeButton(R.id.eyebrow_01 , FaceSector.EYEBROWS, R.id.eyebrowPB01);
        intitializeButton(R.id.eyebrow_02 , FaceSector.EYEBROWS, R.id.eyebrowPB02);
        intitializeButton(R.id.eyebrow_03 , FaceSector.EYEBROWS, R.id.eyebrowPB03);
        intitializeButton(R.id.eyebrow_04 , FaceSector.EYEBROWS, R.id.eyebrowPB04);
        intitializeButton(R.id.eyebrow_05 , FaceSector.EYEBROWS, R.id.eyebrowPB05);
        intitializeButton(R.id.eyebrow_06 , FaceSector.EYEBROWS, R.id.eyebrowPB06);
        intitializeButton(R.id.eyebrow_07 , FaceSector.EYEBROWS, R.id.eyebrowPB07);
        intitializeButton(R.id.eyebrow_08 , FaceSector.EYEBROWS, R.id.eyebrowPB08);
        intitializeButton(R.id.eyebrow_09 , FaceSector.EYEBROWS, R.id.eyebrowPB09);
        intitializeButton(R.id.eyebrow_10 , FaceSector.EYEBROWS, R.id.eyebrowPB10);
        intitializeButton(R.id.eyebrow_11 , FaceSector.EYEBROWS, R.id.eyebrowPB11);
        intitializeButton(R.id.eyebrow_12 , FaceSector.EYEBROWS, R.id.eyebrowPB12);

        intitializeButton(R.id.eyelid_01 , FaceSector.EYELIDS, R.id.eyelidPB01);
        intitializeButton(R.id.eyelid_02 , FaceSector.EYELIDS, R.id.eyelidPB02);
        intitializeButton(R.id.eyelid_03 , FaceSector.EYELIDS, R.id.eyelidPB03);
        intitializeButton(R.id.eyelid_04 , FaceSector.EYELIDS, R.id.eyelidPB04);
        intitializeButton(R.id.eyelid_05 , FaceSector.EYELIDS, R.id.eyelidPB05);
        intitializeButton(R.id.eyelid_06 , FaceSector.EYELIDS, R.id.eyelidPB06);
        intitializeButton(R.id.eyelid_07 , FaceSector.EYELIDS, R.id.eyelidPB07);
        intitializeButton(R.id.eyelid_08 , FaceSector.EYELIDS, R.id.eyelidPB08);
        intitializeButton(R.id.eyelid_09 , FaceSector.EYELIDS, R.id.eyelidPB09);
        intitializeButton(R.id.eyelid_10 , FaceSector.EYELIDS, R.id.eyelidPB10);
        intitializeButton(R.id.eyelid_11 , FaceSector.EYELIDS, R.id.eyelidPB11);
        intitializeButton(R.id.eyelid_12 , FaceSector.EYELIDS, R.id.eyelidPB12);


        intitializeButton(R.id.eyes_01 , FaceSector.EYES, R.id.eyesPB01);
        intitializeButton(R.id.eyes_02 , FaceSector.EYES, R.id.eyesPB02);
        intitializeButton(R.id.eyes_03 , FaceSector.EYES, R.id.eyesPB03);
        intitializeButton(R.id.eyes_04 , FaceSector.EYES, R.id.eyesPB04);
        intitializeButton(R.id.eyes_05 , FaceSector.EYES, R.id.eyesPB05);
        intitializeButton(R.id.eyes_06 , FaceSector.EYES, R.id.eyesPB06);
        intitializeButton(R.id.eyes_07 , FaceSector.EYES, R.id.eyesPB07);
        intitializeButton(R.id.eyes_08 , FaceSector.EYES, R.id.eyesPB08);
        intitializeButton(R.id.eyes_09 , FaceSector.EYES, R.id.eyesPB09);
        intitializeButton(R.id.eyes_10 , FaceSector.EYES, R.id.eyesPB10);
        intitializeButton(R.id.eyes_11 , FaceSector.EYES, R.id.eyesPB11);
        intitializeButton(R.id.eyes_12 , FaceSector.EYES, R.id.eyesPB12);


        intitializeButton(R.id.nose_01 , FaceSector.NOSE, R.id.nosePB01);
        intitializeButton(R.id.nose_02 , FaceSector.NOSE, R.id.nosePB02);
        intitializeButton(R.id.nose_03 , FaceSector.NOSE, R.id.nosePB03);
        intitializeButton(R.id.nose_04 , FaceSector.NOSE, R.id.nosePB04);
        intitializeButton(R.id.nose_05 , FaceSector.NOSE, R.id.nosePB05);
        intitializeButton(R.id.nose_06 , FaceSector.NOSE, R.id.nosePB06);
        intitializeButton(R.id.nose_07 , FaceSector.NOSE, R.id.nosePB07);
        intitializeButton(R.id.nose_08 , FaceSector.NOSE, R.id.nosePB08);
        intitializeButton(R.id.nose_09 , FaceSector.NOSE, R.id.nosePB09);
        intitializeButton(R.id.nose_10 , FaceSector.NOSE, R.id.nosePB10);
        intitializeButton(R.id.nose_11 , FaceSector.NOSE, R.id.nosePB11);
        intitializeButton(R.id.nose_12 , FaceSector.NOSE, R.id.nosePB12);

        intitializeButton(R.id.mouth_01 , FaceSector.MOUTH, R.id.mouthPB01);
        intitializeButton(R.id.mouth_02 , FaceSector.MOUTH, R.id.mouthPB02);
        intitializeButton(R.id.mouth_03 , FaceSector.MOUTH, R.id.mouthPB03);
        intitializeButton(R.id.mouth_04 , FaceSector.MOUTH, R.id.mouthPB04);
        intitializeButton(R.id.mouth_05 , FaceSector.MOUTH, R.id.mouthPB05);
        intitializeButton(R.id.mouth_06 , FaceSector.MOUTH, R.id.mouthPB06);
        intitializeButton(R.id.mouth_07 , FaceSector.MOUTH, R.id.mouthPB07);
        intitializeButton(R.id.mouth_08 , FaceSector.MOUTH, R.id.mouthPB08);
        intitializeButton(R.id.mouth_09 , FaceSector.MOUTH, R.id.mouthPB09);
        intitializeButton(R.id.mouth_10 , FaceSector.MOUTH, R.id.mouthPB10);
        intitializeButton(R.id.mouth_11 , FaceSector.MOUTH, R.id.mouthPB11);
        intitializeButton(R.id.mouth_12 , FaceSector.MOUTH, R.id.mouthPB12);
    }

    private void intitializeButton(int id, FaceSector sector, int pbID)
    {
        Button b = (Button) findViewById(id);
        ProgressBar pb = (ProgressBar) findViewById(pbID);
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

        container.addPerformanceButton(id, b, sector, pb);
    }

    private void intitializePresetButton(int id,int pbID)
    {
        Button b = (Button) findViewById(id);
        ProgressBar pb = (ProgressBar) findViewById(pbID);
        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                presetPerformClick(v);
            }
        });

        b.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                presetRecClick(v);
                return true;
            }
        });

        container.addPresetButton(id, b, FaceSector.PRESET, pb);
    }

    private void testButton()//pnly for debug
    {

        byte[] arrayTest = {50,20,70};

        container.getButtonPerformance(R.id.eyes_01).addPerformancePiece(arrayTest,2000);
        container.getButtonPerformance(R.id.eyes_01).addPerformancePiece(arrayTest,1000);
        container.getButtonPerformance(R.id.eyes_02).addPerformancePiece(arrayTest,500);
        container.getButtonPerformance(R.id.eyes_02).addPerformancePiece(arrayTest,800);
        container.getButtonPerformance(R.id.mouth_01).addPerformancePiece(arrayTest,200);
        container.getButtonPerformance(R.id.mouth_01).addPerformancePiece(arrayTest,4000);
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

        unregisterReceiver(mBroadcastReceiver1);
        unregisterReceiver(mReceiver);
        mBluetoothConnectionEyes.stopClient();
        mBluetoothConnectionMouth.stopClient();
        mBluetoothConnectionHead.stopClient();

    }

    public void performclick(View v)
    {

        int id = v.getId();
        ButtonPerformance<byte[]> bp = container.getButtonPerformance(id);
        if(!bp.canPerform())
        {
            Toast.makeText(this, Constants.emptyPerformance, Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, Constants.performing, Toast.LENGTH_SHORT).show();
        container.deactivatesButtonSectorButton(bp.getFaceSector());
        if(presetRegistrationMode)
        {
            presetInRec.setButtonPerformance(container.getButtonPerformance(id));
            timePresetRec = System.currentTimeMillis();
        }
        performanceFilter.add(bp.getFaceSector());
        performanceThread pt = new performanceThread();
        pt.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, bp);

    }

    public void recClick(View v)
    {

        if(presetRegistrationMode)
            return;

        Toast.makeText(this, Constants.RegistrationString, Toast.LENGTH_LONG).show();
        timeTask = new TimerForRecorder();
        timeTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        stopButton.setClickable(true);
        stopButton.setEnabled(true);

        performRegistrationMode = true;
        bInRec = container.getButtonPerformance(v.getId());
        undoManager.addLastEdit(bInRec);
        bInRec.deletePerformance();
        previousPerformancePieceTime = System.currentTimeMillis();
    }


    public void presetRecClick(View v)
    {
        Toast.makeText(this, Constants.RegistrationString, Toast.LENGTH_LONG).show();
        timeTask = new TimerForRecorder();
        timeTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        stopButton.setClickable(true);
        stopButton.setEnabled(true);
        timePresetRec = System.currentTimeMillis();

        presetRegistrationMode = true;
        presetInRec = container.getPresetPerformance(v.getId());
        undoManager.addLastEdit(presetInRec);
        presetInRec.deletePerformance();
        previousPerformancePieceTime = System.currentTimeMillis();
    }

    public void presetPerformClick(View v)
    {
        int id = v.getId();
        PresetPerformance<byte[]> bp = container.getPresetPerformance(id);
        if(!bp.canPerform())
        {
            Toast.makeText(this, Constants.emptyPerformance, Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, Constants.performing, Toast.LENGTH_SHORT).show();
        container.deactivatesButtonSectorButton(bp.getFaceSector());
        preSetThread pt = new preSetThread();
        pt.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, bp);
    }

    public void resetSBButton()
    {
        multBar.setProgress(10 - minValueMult);
    }


    public void stopButton()
    {
        timeTask.cancel(true);
        timeTask = null;
        performRegistrationMode = false;
        presetRegistrationMode = false;
        if(bInRec != null)
            bInRec.updateColor();
        bInRec = null;

        if(presetInRec != null)
            presetInRec.updateColor();
        presetInRec = null;
        container.saveMe(this);

        stopButton.setClickable(false);
        stopButton.setEnabled(false);
        timePresetRec = 0;
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
            String textToShow =  undoManager.undo() ? "undo done" : "There was nothing to undo";
            container.saveMe(this);
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
            Log.i(TAG,text);
            if(text.contains("ALI"))
                return;
            byte[] bytes;

            FaceSector f = FaceSector.fromChar(text.charAt(0));
            if(performanceFilter.contains(f))
                return;


            switch (id){
                case Constants.MouthID:
                    if(!mouthActiveController)
                        return;


                    bytes = text.getBytes(Charset.defaultCharset());


                    if(performRegistrationMode)
                    {

                        if(f == bInRec.getFaceSector()) {
                            addPerforamancePieceToRec(bytes);
                        }
                    }

                    mBluetoothConnectionHead.write(bytes);
                    break;

                case Constants.eyesID:
                    if(!eyesActiveController)
                        return;

                    bytes = text.getBytes(Charset.defaultCharset());

                    if(performRegistrationMode)
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


    public class performanceThread extends AsyncTask<ButtonPerformance<byte[]>, Integer, ButtonPerformance<byte[]>> {

        private ButtonPerformance<byte[]> bpThread;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ButtonPerformance<byte[]> doInBackground(ButtonPerformance<byte[]>... params) {

            bpThread = params[0];
            List<PerformancePiece<byte[]>> performance = bpThread.getPerformance();
            long startTime = System.currentTimeMillis();
            boolean inPerformance = true;
            PerformancePiece<byte[]> currentPiece = performance.get(0);


            int currentIndex = 0;
            long doPerfomance = startTime + (long)(currentPiece.getMillisToAction() / multiplicator);
            while(inPerformance)
            {
                try {
                    Thread.sleep(stopMillisPerformance);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                long currentTime = System.currentTimeMillis();
                long progressTime = currentTime - startTime;
                int percentProgress = (int) ((100 * progressTime) / (int)(bpThread.getDuration() / multiplicator));
                publishProgress(percentProgress);

                if(currentTime > doPerfomance)
                {
                    mBluetoothConnectionHead.write(currentPiece.getAction());
                    currentIndex++;

                    if(currentIndex >= performance.size())
                        inPerformance = false;
                    else
                    {
                        currentPiece = performance.get(currentIndex);
                        doPerfomance = currentTime + (long)(currentPiece.getMillisToAction() / multiplicator);
                    }
                }


            }
            return bpThread;
        }

        @Override
        protected void onProgressUpdate(Integer... values)
        {
            bpThread.getProgressBar().setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(ButtonPerformance<byte[]> bp) {

            container.activatesButtonSectorButton(bp.getFaceSector());
            performanceFilter.remove(bp.getFaceSector());
            bpThread.getProgressBar().setProgress(0);
        }

    }

    public class preSetThread extends AsyncTask<PresetPerformance<byte[]>, Integer, PresetPerformance<byte[]>> {

        private PresetPerformance<byte[]> bpThread;
        @Override
        protected PresetPerformance<byte[]> doInBackground(PresetPerformance<byte[]>... presetPerformances) {
            bpThread = presetPerformances[0];
            int duration = (int)(bpThread.getDuration() / multiplicator);
            List<Integer> buttonToPress = bpThread.getButtonsToPress();
            publishProgress(buttonToPress.toArray(new Integer[buttonToPress.size()]));
            long startTime = System.currentTimeMillis();
            long time = startTime;
            long endTime = time + duration;

            while(time < endTime)
            {
                try {
                    Thread.sleep(stopMillisPerformance);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                time = System.currentTimeMillis();
                long progressTime = time - startTime;
                int percentProgress = (int) ((100 * progressTime) / (int)(bpThread.getDuration() / multiplicator));
                publishProgress(percentProgress);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values)
        {

            if(values.length > 1)
            {
                for(int i = 0; i < values.length; i++)
                {
                    Button b = container.getButtonPerformance(values[i]).getButton();
                    b.performClick();
                }
                return;

            }

            bpThread.getProgressBar().setProgress(values[0]);
        }


        @Override
        protected void onPostExecute(PresetPerformance<byte[]> p) {
            container.activatesButtonSectorButton(bpThread.getFaceSector());
            bpThread.getProgressBar().setProgress(0);
        }

    }

}
