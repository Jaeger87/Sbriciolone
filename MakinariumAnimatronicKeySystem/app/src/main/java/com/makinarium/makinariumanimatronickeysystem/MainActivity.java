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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {


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
    private ButtonsContainer<Integer> presetContainer;
    private ButtonPerformance<byte[]> bInRec;
    private ButtonPerformance<Integer> presetInRec;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter((Constants.incomingMessageIntent)));

        Intent headIntent = getIntent();
        headMac = headIntent.getStringExtra(Intent.EXTRA_TEXT);

        container = new ButtonsContainer<>();
        presetContainer = new ButtonsContainer<>();
        initializeAllButtons();

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



        //connectionBluetooth();
        testButton();
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
        intitializeButton(R.id.eyes_01 , FaceSector.EYES, R.id.eyesPB01);
        intitializeButton(R.id.eyes_02 , FaceSector.EYES, R.id.eyesPB02);
        intitializeButton(R.id.mouth_01 , FaceSector.MOUTH, R.id.mouthPB01);
        intitializeButton(R.id.mouth_02 , FaceSector.MOUTH, R.id.mouthPB02);
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

        container.addButton(id, b, sector, pb);
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

        presetContainer.addButton(id, b, FaceSector.PRESET, pb);
    }


    private void testButton()//pnly for debug
    {

        byte[] arrayTest = {50,20,70};

        container.getButtonPerform(R.id.eyes_01).addPerformancePiece(arrayTest,2000);
        container.getButtonPerform(R.id.eyes_01).addPerformancePiece(arrayTest,1000);
        container.getButtonPerform(R.id.eyes_02).addPerformancePiece(arrayTest,500);
        container.getButtonPerform(R.id.eyes_02).addPerformancePiece(arrayTest,800);
        container.getButtonPerform(R.id.mouth_01).addPerformancePiece(arrayTest,200);
        container.getButtonPerform(R.id.mouth_01).addPerformancePiece(arrayTest,4000);
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
        ButtonPerformance<byte[]> bp = container.getButtonPerform(id);
        if(!bp.canPerform())
        {
            Toast.makeText(this, Constants.emptyPerformance, Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, Constants.performing, Toast.LENGTH_SHORT).show();
        container.deactivatesButtonSectorButton(bp.getFaceSector());
        if(presetRegistrationMode)
        {
            int time = (int)(System.currentTimeMillis() - timePresetRec);
            presetInRec.addPerformancePiece(v.getId(), time);
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
        bInRec = container.getButtonPerform(v.getId());
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
        presetInRec = presetContainer.getButtonPerform(v.getId());
        presetInRec.deletePerformance();
        previousPerformancePieceTime = System.currentTimeMillis();
    }

    public void presetPerformClick(View v)
    {
        int id = v.getId();
        ButtonPerformance<Integer> bp = presetContainer.getButtonPerform(id);
        if(!bp.canPerform())
        {
            Toast.makeText(this, Constants.emptyPerformance, Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, Constants.performing, Toast.LENGTH_SHORT).show();
        container.deactivatesAllButtons();
        presetContainer.deactivatesButtonSectorButton(bp.getFaceSector());
        performanceFilter.remove(FaceSector.EYEBROWS);
        performanceFilter.remove(FaceSector.EYELIDS);
        performanceFilter.remove(FaceSector.EYES);
        performanceFilter.remove(FaceSector.NOSE);
        performanceFilter.remove(FaceSector.MOUTH);
        preSetThread pt = new preSetThread();
        pt.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, bp);
    }


    public void stopButton()
    {
        timeTask.cancel(true);
        timeTask = null;
        performRegistrationMode = false;
        presetRegistrationMode = false;
        bInRec = null;
        presetInRec = null;
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
            if(performanceFilter.contains(f))
                return;


            switch (id){
                case Constants.MouthID:
                    if(!mouthActiveController)
                        return;


                    bytes = text.getBytes(Charset.defaultCharset());

                    if(performRegistrationMode)
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

        public performanceThread()
        {

        }

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
                int percentProgress = (int) ((100 * progressTime) / bpThread.getDuration());
                publishProgress(percentProgress);

                if(currentTime > doPerfomance)
                {
                    //Log.i(TAG, currentPiece.getAction());
                    //mBluetoothConnectionHead.write(currentPiece.getAction());
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


    public class preSetThread extends AsyncTask<ButtonPerformance<Integer>, Integer, ButtonPerformance<Integer>> {


        private ButtonPerformance<Integer> bpThread;

        public preSetThread()
        {

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ButtonPerformance<Integer> doInBackground(ButtonPerformance<Integer>... params) {

            bpThread = params[0];
            List<PerformancePiece<Integer>> performance = bpThread.getPerformance();
            long startTime = System.currentTimeMillis();
            boolean inPerformance = true;
            PerformancePiece<Integer> currentPiece = performance.get(0);




            int currentIndex = 0;
            long doPerfomance = startTime + currentPiece.getMillisToAction();

            long endPerfomance = startTime + container.getButtonPerform(currentPiece.getAction()).getDuration();

            while(inPerformance)
            {
                try {
                    Thread.sleep(4);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                long currentTime = System.currentTimeMillis();
                long progressTime = currentTime - startTime;
                int percentProgress = (int) ((100 * progressTime) / bpThread.getDuration());


                int idToPress = -1;

                if(currentTime > doPerfomance)
                {
                    //Log.i(TAG, currentPiece.getAction());
                    idToPress = currentPiece.getAction();

                    currentIndex++;

                    if(currentIndex >= performance.size())
                        inPerformance = false;
                    else
                    {
                        currentPiece = performance.get(currentIndex);
                        doPerfomance = currentTime + currentPiece.getMillisToAction();

                        long maybeEnd = startTime + container.getButtonPerform(currentPiece.getAction()).getDuration();
                        if(maybeEnd > endPerfomance)
                            endPerfomance = maybeEnd;

                    }
                }

                if(idToPress != -1)
                    publishProgress(percentProgress, idToPress);
                else
                    publishProgress(percentProgress, idToPress);


            }

            if(System.currentTimeMillis() < endPerfomance)
                try {
                Thread.sleep(endPerfomance - System.currentTimeMillis() + 10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }



            return bpThread;
        }


        @Override
        protected void onProgressUpdate(Integer... values)
        {
            bpThread.getProgressBar().setProgress(values[0]);
            if(values.length > 1)
                performclick(container.getButtonPerform(values[1]).getButton());
        }

        @Override
        protected void onPostExecute(ButtonPerformance<Integer> bp) {
            presetContainer.activatesButtonSectorButton(bp.getFaceSector());
            container.activatesAllButtons();
            performanceFilter.remove(FaceSector.EYEBROWS);
            performanceFilter.remove(FaceSector.EYELIDS);
            performanceFilter.remove(FaceSector.EYES);
            performanceFilter.remove(FaceSector.NOSE);
            performanceFilter.remove(FaceSector.MOUTH);
            bpThread.getProgressBar().setProgress(0);
        }

    }

}
