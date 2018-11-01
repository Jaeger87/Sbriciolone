package com.makinarium.makinariumanimatronickeysystem;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.makinarium.makinariumanimatronickeysystem.com.makinarium.utilities.Constants;

public class CheckConnectionsThread extends Thread {

    private static final long timeToDeclareDeath = 10000;

    private Context mContext;

    private long lastTimeMouthAlive;
    private long lastTimeEyesAlive;
    private long lastTimeHeadAlive;
    private boolean mouthStatus;
    private boolean eyesStatus;
    private boolean headStatus;

    private boolean threadAlive = true;


    public CheckConnectionsThread(Context mContext) {
        this.mContext = mContext;
    }

    public void run(){

        while(threadAlive)
        {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            long currentTime = System.currentTimeMillis();

            checkAndSend(currentTime,lastTimeMouthAlive,Constants.mouthStatus,mouthStatus);
            checkAndSend(currentTime,lastTimeEyesAlive,Constants.eyesStatus,eyesStatus);
            checkAndSend(currentTime,lastTimeHeadAlive,Constants.headStatus,headStatus);
        }

    }


    private void sendNews(String who, boolean status)
    {
        Intent incomingMessageIntent = new Intent(Constants.deathBT);
        incomingMessageIntent.putExtra(Constants.changeStatus, who);
        incomingMessageIntent.putExtra(Constants.valueStatus, status);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(incomingMessageIntent);
    }


    private void checkAndSend(long currentTime, long lastTime, String who, boolean status)
    {
        if(currentTime - lastTime > timeToDeclareDeath)
        {
            if(status)
            {

                status = false;
                sendNews(who, status);
                updateStatus(who, status);
            }

        }
        else
        {
            if(!status)
            {
                status = true;
                sendNews(who, status);
                updateStatus(who, status);
            }
        }
    }


    private void updateStatus(String who, boolean status)
    {
        if(who.equals(Constants.mouthStatus))
            mouthStatus = status;
        if(who.equals(Constants.eyesStatus))
            eyesStatus = status;
        if(who.equals(Constants.headStatus))
            headStatus = status;
    }


    public void setLastTimeMouthAlive(long lastTimeMouthAlive) {
        this.lastTimeMouthAlive = lastTimeMouthAlive;
    }

    public void setLastTimeEyesAlive(long lastTimeEyesAlive) {
        this.lastTimeEyesAlive = lastTimeEyesAlive;
    }

    public void setLastTimeHeadAlive(long lastTimeHeadAlive) {
        this.lastTimeHeadAlive = lastTimeHeadAlive;
    }

    public void stopChecking()
    {
        threadAlive = false;
    }

}
