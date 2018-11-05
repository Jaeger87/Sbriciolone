package com.makinarium.makinariumanimatronickeysystem;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.makinarium.makinariumanimatronickeysystem.com.makinarium.customgraphics.VerticalSeekBar;
import com.makinarium.makinariumanimatronickeysystem.com.makinarium.presetthings.AbstractPerformance;
import com.makinarium.makinariumanimatronickeysystem.com.makinarium.presetthings.ButtonPerformance;
import com.makinarium.makinariumanimatronickeysystem.com.makinarium.presetthings.ButtonsContainer;
import com.makinarium.makinariumanimatronickeysystem.com.makinarium.presetthings.PerformancePiece;
import com.makinarium.makinariumanimatronickeysystem.com.makinarium.presetthings.PresetPerformance;
import com.makinarium.makinariumanimatronickeysystem.com.makinarium.undo.UndoManager;
import com.makinarium.makinariumanimatronickeysystem.com.makinarium.utilities.Constants;
import com.makinarium.makinariumanimatronickeysystem.com.makinarium.utilities.IDFactory;

import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {


    private static final int stopMillisPerformance = 2;
    private static final String TAG = "Makinarium";
    private Button stopButton;
    private TimerForRecorder timeTask;
    private TextView cronometro;
    private boolean performRegistrationMode = false;
    private boolean presetRegistrationMode = false;
    private boolean mouthActiveController = true;
    private boolean eyesActiveController = true;
    private boolean canIChangeNames = false;

    private Switch mouthSwitch;
    private Switch eyesSwitch;
    private Switch nameSwitch;

    private ButtonsContainer<byte[]> container;
    private UndoManager<byte[]> undoManager;
    private ButtonPerformance bInRec;
    private PresetPerformance presetInRec;
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

    private TextView mouthStatus;
    private TextView eyesStatus;
    private TextView headStatus;

    private Gson gson;

    private int readyColor = 0;
    private int presetColor = 0;

    private Executor myExecutor;

    private CheckConnectionsThread checkThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IDFactory.initializeButtons();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter((Constants.incomingMessageIntent)));
        LocalBroadcastManager.getInstance(this).registerReceiver(mDeathOrAlive, new IntentFilter((Constants.deathBT)));


        Intent headIntent = getIntent();
        headMac = headIntent.getStringExtra(Intent.EXTRA_TEXT);

        readyColor = ResourcesCompat.getColor(getResources(), R.color.activePerform, null);

        int toReccolor = ResourcesCompat.getColor(getResources(), R.color.performToRec, null);

        presetColor = ResourcesCompat.getColor(getResources(), R.color.firstcolumn, null);

        gson = new Gson();

        try (FileInputStream inputStream = new FileInputStream(this.getFilesDir() + Constants.SaveFileName)) {
            String json = IOUtils.toString(inputStream, "UTF-8");
            Type containerType = new TypeToken<ButtonsContainer<byte[]>>(){}.getType();
            container = gson.fromJson(json, containerType);
            if(container != null) {
                initializeAllButtons();
                container.updateAllColorsAndNames();

            }
            else
            {
                container = new ButtonsContainer<>(readyColor, toReccolor);
                initializeAllButtons();
            }
        } catch (IOException e) {
            container = new ButtonsContainer<>(readyColor, toReccolor);
            initializeAllButtons();
        }

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
        nameSwitch = (Switch)findViewById(R.id.nameSwitch);

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

        nameSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                onCheckNames(buttonView,isChecked);
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

        mouthStatus = (TextView) findViewById(R.id.mouthStatus);
        eyesStatus = (TextView) findViewById(R.id.eyesStatus);
        headStatus = (TextView) findViewById(R.id.headStatus);

        checkThread = new CheckConnectionsThread(this);

        connectionBluetooth();

        myExecutor = Executors.newFixedThreadPool(7);

        checkThread.start();
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
        initilializePresetButton(R.id.preset_01,R.id.presetPB01, R.id.presetText01);
        initilializePresetButton(R.id.preset_02,R.id.presetPB02, R.id.presetText02);
        initilializePresetButton(R.id.preset_03,R.id.presetPB03, R.id.presetText03);
        initilializePresetButton(R.id.preset_04,R.id.presetPB04, R.id.presetText04);
        initilializePresetButton(R.id.preset_05,R.id.presetPB05, R.id.presetText05);
        initilializePresetButton(R.id.preset_06,R.id.presetPB06, R.id.presetText06);
        initilializePresetButton(R.id.preset_07,R.id.presetPB07, R.id.presetText07);
        initilializePresetButton(R.id.preset_08,R.id.presetPB08, R.id.presetText08);
        initilializePresetButton(R.id.preset_09,R.id.presetPB09, R.id.presetText09);
        initilializePresetButton(R.id.preset_10,R.id.presetPB10, R.id.presetText10);
        initilializePresetButton(R.id.preset_11,R.id.presetPB11, R.id.presetText11);
        initilializePresetButton(R.id.preset_12,R.id.presetPB12, R.id.presetText12);

        initilializeButton(R.id.eyebrow_01 , FaceSector.EYEBROWS, R.id.eyebrowPB01, R.id.eyebrowText01);
        initilializeButton(R.id.eyebrow_02 , FaceSector.EYEBROWS, R.id.eyebrowPB02, R.id.eyebrowText02);
        initilializeButton(R.id.eyebrow_03 , FaceSector.EYEBROWS, R.id.eyebrowPB03, R.id.eyebrowText03);
        initilializeButton(R.id.eyebrow_04 , FaceSector.EYEBROWS, R.id.eyebrowPB04, R.id.eyebrowText04);
        initilializeButton(R.id.eyebrow_05 , FaceSector.EYEBROWS, R.id.eyebrowPB05, R.id.eyebrowText05);
        initilializeButton(R.id.eyebrow_06 , FaceSector.EYEBROWS, R.id.eyebrowPB06, R.id.eyebrowText06);
        initilializeButton(R.id.eyebrow_07 , FaceSector.EYEBROWS, R.id.eyebrowPB07, R.id.eyebrowText07);
        initilializeButton(R.id.eyebrow_08 , FaceSector.EYEBROWS, R.id.eyebrowPB08, R.id.eyebrowText08);
        initilializeButton(R.id.eyebrow_09 , FaceSector.EYEBROWS, R.id.eyebrowPB09, R.id.eyebrowText09);
        initilializeButton(R.id.eyebrow_10 , FaceSector.EYEBROWS, R.id.eyebrowPB10, R.id.eyebrowText10);
        initilializeButton(R.id.eyebrow_11 , FaceSector.EYEBROWS, R.id.eyebrowPB11, R.id.eyebrowText11);
        initilializeButton(R.id.eyebrow_12 , FaceSector.EYEBROWS, R.id.eyebrowPB12, R.id.eyebrowText12);

        initilializeButton(R.id.eyelid_01 , FaceSector.EYELIDS, R.id.eyelidPB01, R.id.eyelidText01);
        initilializeButton(R.id.eyelid_02 , FaceSector.EYELIDS, R.id.eyelidPB02, R.id.eyelidText02);
        initilializeButton(R.id.eyelid_03 , FaceSector.EYELIDS, R.id.eyelidPB03, R.id.eyelidText03);
        initilializeButton(R.id.eyelid_04 , FaceSector.EYELIDS, R.id.eyelidPB04, R.id.eyelidText04);
        initilializeButton(R.id.eyelid_05 , FaceSector.EYELIDS, R.id.eyelidPB05, R.id.eyelidText05);
        initilializeButton(R.id.eyelid_06 , FaceSector.EYELIDS, R.id.eyelidPB06, R.id.eyelidText06);
        initilializeButton(R.id.eyelid_07 , FaceSector.EYELIDS, R.id.eyelidPB07, R.id.eyelidText07);
        initilializeButton(R.id.eyelid_08 , FaceSector.EYELIDS, R.id.eyelidPB08, R.id.eyelidText08);
        initilializeButton(R.id.eyelid_09 , FaceSector.EYELIDS, R.id.eyelidPB09, R.id.eyelidText09);
        initilializeButton(R.id.eyelid_10 , FaceSector.EYELIDS, R.id.eyelidPB10, R.id.eyelidText10);
        initilializeButton(R.id.eyelid_11 , FaceSector.EYELIDS, R.id.eyelidPB11, R.id.eyelidText11);
        initilializeButton(R.id.eyelid_12 , FaceSector.EYELIDS, R.id.eyelidPB12, R.id.eyelidText12);


        initilializeButton(R.id.eyes_01 , FaceSector.EYES, R.id.eyesPB01, R.id.eyesText01);
        initilializeButton(R.id.eyes_02 , FaceSector.EYES, R.id.eyesPB02, R.id.eyesText02);
        initilializeButton(R.id.eyes_03 , FaceSector.EYES, R.id.eyesPB03, R.id.eyesText03);
        initilializeButton(R.id.eyes_04 , FaceSector.EYES, R.id.eyesPB04, R.id.eyesText04);
        initilializeButton(R.id.eyes_05 , FaceSector.EYES, R.id.eyesPB05, R.id.eyesText05);
        initilializeButton(R.id.eyes_06 , FaceSector.EYES, R.id.eyesPB06, R.id.eyesText06);
        initilializeButton(R.id.eyes_07 , FaceSector.EYES, R.id.eyesPB07, R.id.eyesText07);
        initilializeButton(R.id.eyes_08 , FaceSector.EYES, R.id.eyesPB08, R.id.eyesText08);
        initilializeButton(R.id.eyes_09 , FaceSector.EYES, R.id.eyesPB09, R.id.eyesText09);
        initilializeButton(R.id.eyes_10 , FaceSector.EYES, R.id.eyesPB10, R.id.eyesText10);
        initilializeButton(R.id.eyes_11 , FaceSector.EYES, R.id.eyesPB11, R.id.eyesText11);
        initilializeButton(R.id.eyes_12 , FaceSector.EYES, R.id.eyesPB12, R.id.eyesText12);


        initilializeButton(R.id.nose_01 , FaceSector.NOSE, R.id.nosePB01, R.id.noseText01);
        initilializeButton(R.id.nose_02 , FaceSector.NOSE, R.id.nosePB02, R.id.noseText02);
        initilializeButton(R.id.nose_03 , FaceSector.NOSE, R.id.nosePB03, R.id.noseText03);
        initilializeButton(R.id.nose_04 , FaceSector.NOSE, R.id.nosePB04, R.id.noseText04);
        initilializeButton(R.id.nose_05 , FaceSector.NOSE, R.id.nosePB05, R.id.noseText05);
        initilializeButton(R.id.nose_06 , FaceSector.NOSE, R.id.nosePB06, R.id.noseText06);
        initilializeButton(R.id.nose_07 , FaceSector.NOSE, R.id.nosePB07, R.id.noseText07);
        initilializeButton(R.id.nose_08 , FaceSector.NOSE, R.id.nosePB08, R.id.noseText08);
        initilializeButton(R.id.nose_09 , FaceSector.NOSE, R.id.nosePB09, R.id.noseText09);
        initilializeButton(R.id.nose_10 , FaceSector.NOSE, R.id.nosePB10, R.id.noseText10);
        initilializeButton(R.id.nose_11 , FaceSector.NOSE, R.id.nosePB11, R.id.noseText11);
        initilializeButton(R.id.nose_12 , FaceSector.NOSE, R.id.nosePB12, R.id.noseText12);

        initilializeButton(R.id.mouth_01 , FaceSector.MOUTH, R.id.mouthPB01, R.id.mouthText01);
        initilializeButton(R.id.mouth_02 , FaceSector.MOUTH, R.id.mouthPB02, R.id.mouthText02);
        initilializeButton(R.id.mouth_03 , FaceSector.MOUTH, R.id.mouthPB03, R.id.mouthText03);
        initilializeButton(R.id.mouth_04 , FaceSector.MOUTH, R.id.mouthPB04, R.id.mouthText04);
        initilializeButton(R.id.mouth_05 , FaceSector.MOUTH, R.id.mouthPB05, R.id.mouthText05);
        initilializeButton(R.id.mouth_06 , FaceSector.MOUTH, R.id.mouthPB06, R.id.mouthText06);
        initilializeButton(R.id.mouth_07 , FaceSector.MOUTH, R.id.mouthPB07, R.id.mouthText07);
        initilializeButton(R.id.mouth_08 , FaceSector.MOUTH, R.id.mouthPB08, R.id.mouthText08);
        initilializeButton(R.id.mouth_09 , FaceSector.MOUTH, R.id.mouthPB09, R.id.mouthText09);
        initilializeButton(R.id.mouth_10 , FaceSector.MOUTH, R.id.mouthPB10, R.id.mouthText10);
        initilializeButton(R.id.mouth_11 , FaceSector.MOUTH, R.id.mouthPB11, R.id.mouthText11);
        initilializeButton(R.id.mouth_12 , FaceSector.MOUTH, R.id.mouthPB12, R.id.mouthText12);
    }

    private void initilializeButton(int id, FaceSector sector, int pbID, int textID)
    {
        Button b = (Button) findViewById(id);
        ProgressBar pb = (ProgressBar) findViewById(pbID);
        TextView t = (TextView) findViewById(textID);
        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                performClick(v);
            }
        });

        b.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                recClick(v);
                return true;
            }
        });

        container.addPerformanceButton(id, b, sector, pb, t);
    }

    private void initilializePresetButton(int id, int pbID, int textID)
    {
        Button b = (Button) findViewById(id);
        ProgressBar pb = (ProgressBar) findViewById(pbID);
        TextView t = (TextView) findViewById(textID);

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

        container.addPresetButton(id, b, FaceSector.PRESET, pb, t);
    }

    private void testButton()//only for debug
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
        unregisterReceiver(mDeathOrAlive);
        checkThread.stopChecking();
        mBluetoothConnectionEyes.stopClient();
        mBluetoothConnectionMouth.stopClient();
        mBluetoothConnectionHead.stopClient();

    }

    //TODO: Toast con nome, occhio ai preset
    public void performClick(View v)
    {

        int id = v.getId();
        ButtonPerformance bp = container.getButtonPerformance(id);
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
        pt.executeOnExecutor(myExecutor,bp);
        //pt.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, bp);

    }

    public void recClick(View v)
    {

        if(presetRegistrationMode)
            return;

        Toast.makeText(this, Constants.RegistrationString, Toast.LENGTH_LONG).show();
        timeTask = new TimerForRecorder();
        timeTask.executeOnExecutor(myExecutor);
        //timeTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
        timeTask.executeOnExecutor(myExecutor);
        //timeTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
        PresetPerformance bp = container.getPresetPerformance(id);
        if(!bp.canPerform())
        {
            Toast.makeText(this, Constants.emptyPerformance, Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, Constants.performing, Toast.LENGTH_SHORT).show();
        container.deactivatesButtonSectorButton(bp.getFaceSector());
        preSetThread pt = new preSetThread();
        pt.executeOnExecutor(myExecutor, bp);
        //pt.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, bp);
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
        if(bInRec != null) {
            bInRec.updateColor();
            bInRec.compressMessage();
        }
        bInRec = null;

        if(presetInRec != null)
            presetInRec.updateColor();
        presetInRec = null;
        stopButton.setClickable(false);
        stopButton.setEnabled(false);
        timePresetRec = 0;
        Toast.makeText(this, Constants.stopRecording, Toast.LENGTH_LONG).show();
        container.saveMe(this, gson);

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
            container.saveMe(this, gson);
            Toast.makeText(context, textToShow, Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public class TimerForRecorder extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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

    public void onCheckNames(CompoundButton buttonView, boolean isChecked) {
        if(isChecked) {
            canIChangeNames = true;
            Toast.makeText(this, Constants.YesICAN, Toast.LENGTH_LONG).show();
        } else {
            canIChangeNames = false;
            Toast.makeText(this, Constants.NOICAN, Toast.LENGTH_LONG).show();
        }
    }


    BroadcastReceiver mDeathOrAlive = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean status = intent.getBooleanExtra(Constants.valueStatus, true);
            String who = intent.getStringExtra(Constants.changeStatus);

            if(who.equals(Constants.mouthStatus))
            {
                setTextViewStatus(mouthStatus, status);
                return;
            }

            if(who.equals(Constants.eyesStatus))
            {
                setTextViewStatus(eyesStatus,status);
                return;
            }

            if(who.equals(Constants.headStatus))
            {
                setTextViewStatus(headStatus,status);
                return;
            }
        }


        private void setTextViewStatus(TextView tView, boolean status)
        {
            if(status)
            {
                tView.setText(Constants.connectionOK);
                tView.setTextColor(readyColor);
                return;
            }

            tView.setText(Constants.connectionNO);
            tView.setTextColor(presetColor);
        }
    };

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int id = intent.getIntExtra(Constants.intentIDProp, 0);
            String text = intent.getStringExtra("Message");
            long currentTime = System.currentTimeMillis();
            switch (id){
                case Constants.MouthID:
                    checkThread.setLastTimeMouthAlive(currentTime);
                    break;
                case Constants.eyesID:
                    checkThread.setLastTimeEyesAlive(currentTime);
                    break;
                case Constants.HeadID:
                    checkThread.setLastTimeHeadAlive(currentTime);
                    break;
                default:
                    break;
            }

            if(text.contains("ALI"))
                return;
            byte[] bytes;

            FaceSector f = FaceSector.fromChar(text.charAt(0));
            if(performanceFilter.contains(f))
                return;

            Log.i("BT15",text);
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


    public class performanceThread extends AsyncTask<ButtonPerformance, Integer, ButtonPerformance> {

        private ButtonPerformance bpThread;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ButtonPerformance doInBackground(ButtonPerformance... params) {

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
        protected void onPostExecute(ButtonPerformance bp) {

            container.activatesButtonSectorButton(bp.getFaceSector());
            performanceFilter.remove(bp.getFaceSector());
            bpThread.getProgressBar().setProgress(0);
        }

    }

    public class preSetThread extends AsyncTask<PresetPerformance, Integer, PresetPerformance> {

        private PresetPerformance bpThread;
        @Override
        protected PresetPerformance doInBackground(PresetPerformance... presetPerformances) {
            bpThread = presetPerformances[0];
            int duration = (int)(bpThread.getDuration() / multiplicator);
            List<Integer> buttonToPress = bpThread.getButtonsToPress();
            publishProgress(buttonToPress.toArray(new Integer[buttonToPress.size() + 1]));//Trucchetto se ho solo un bottone da premere
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
                for(int i = 0; i < values.length - 1; i++)//Trucchetto se ho solo un bottone da premere
                {
                    Button b = container.getButtonPerformanceFromLogic(values[i]).getButton();
                    b.performClick();
                }
                return;

            }

            bpThread.getProgressBar().setProgress(values[0]);
        }


        @Override
        protected void onPostExecute(PresetPerformance p) {
            container.activatesButtonSectorButton(bpThread.getFaceSector());
            bpThread.getProgressBar().setProgress(0);
        }

    }


    public void changeName(final View v) {

        if(!canIChangeNames)
            return;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change button name");
// I'm using fragment here so I'm using getView() to provide ViewGroup
// but you can provide here any other instance of ViewGroup from your Fragment / Activity
        ViewGroup view = (ViewGroup) findViewById(android.R.id.content);
        View viewInflated = LayoutInflater.from(this).inflate(R.layout.popupname, view, false);
// Set up the input
        final EditText input = (EditText) viewInflated.findViewById(R.id.insertName);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        builder.setView(viewInflated);

// Set up the buttons
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                AbstractPerformance ap = container.getGenericAbstractPerformance(IDFactory.convertTextToButton(v.getId()));
                undoManager.addLastEdit(ap, ap.getName());
                ap.setName(input.getText().toString());
                container.saveMe(getBaseContext() ,gson);
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

}
