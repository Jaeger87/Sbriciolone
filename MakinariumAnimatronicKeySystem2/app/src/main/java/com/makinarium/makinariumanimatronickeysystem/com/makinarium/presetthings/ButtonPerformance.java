package com.makinarium.makinariumanimatronickeysystem.com.makinarium.presetthings;

import android.icu.text.RelativeDateTimeFormatter;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.makinarium.makinariumanimatronickeysystem.FaceSector;
import com.makinarium.makinariumanimatronickeysystem.MessageTypes;
import com.makinarium.makinariumanimatronickeysystem.com.makinarium.utilities.AnalogDirection;
import com.makinarium.makinariumanimatronickeysystem.com.makinarium.utilities.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ButtonPerformance extends AbstractPerformance{

    private List<PerformancePiece<byte[]>> performance;

    public ButtonPerformance(int id, Button button, FaceSector faceSector, ProgressBar progressBar, TextView textView, int activeColor, int performToRecColor)
    {
        super(id, button,faceSector,progressBar, textView, activeColor, performToRecColor);
        performance = new ArrayList<>();
    }


    public void deletePerformance()
    {
        super.deletePerformance();
        performance = new ArrayList<>();
    }


    public void addPerformancePiece(byte[] action, int time)
    {
        if(performance.size() == 0)
            time = 0;
        performance.add(new PerformancePiece<>(action,time));
        setPerform(true);
        setDuration(getDuration() + time);
    }

    public void addPerformancePiece(byte[] action, int time, String text)
    {
        if(performance.size() == 0)
            time = 0;
        performance.add(new PerformancePiece<>(action,time, text));
        setPerform(true);
        setDuration(getDuration() + time);
    }

    public List<PerformancePiece<byte[]>> getPerformance()
    {
        return new ArrayList<>(performance);
    }


    public void compressMessage()
    {
        Log.i("ZIP", "inizio");
        Log.i("ZIP", String.valueOf(performance.size()));
        Log.i("duration " , String.valueOf(super.getDuration()));
        int pIndex = 0;
        HashMap<Integer,Boolean> first = new HashMap<>();

        for(PerformancePiece<byte[]> p : performance)
        {
            first.put(p.getChannelPin(), true);
        }
        for(PerformancePiece<byte[]> p : performance)
        {

            Log.i("ZIP", p.toString());
            Log.i("TIME",String.valueOf(p.getMillisToAction()) );
            if (first.get(p.getChannelPin()))
            {
                first.put(p.getChannelPin(), false);
                pIndex++;
                continue;

            }
            if(p.isToErase() || !(p.getType() == MessageTypes.SERVO)) {
                pIndex++;
                continue;
            }
            PerformancePiece<byte[]> y = null;
            PerformancePiece<byte[]> z = null;
            int pChannel = p.getChannelPin();
            int millisFuture = 0;
            AnalogDirection direction = AnalogDirection.UP;
            int timeToAdd = 0;
            //Log.i("ZIP", "AAAAA  " + p.toString());
            for(int i = pIndex + 1; i < performance.size(); i++)
            {
                Log.i("TIzME",String.valueOf(performance.get(i).getMillisToAction()) );
                millisFuture += performance.get(i).getMillisToAction();
               // Log.i("ZIP", "BBBBBAAAAA  " + String.valueOf(millisFuture));
                if(millisFuture > Constants.DELAYTOERASEFORBTE)
                    if(performance.get(i).getMillisToAction() > Constants.LITTLEDELAY)
                        break;
                if(!(performance.get(i).getType() == MessageTypes.SERVO))
                    continue;
                if(performance.get(i).getChannelPin() != pChannel)
                    continue;

                if(performance.get(i).isToErase())
                    continue;

                //Log.i("ZIP", "BBBBBAAAAA  " + p.toString());

                if(y == null)
                {
                    y = performance.get(i);
                    y.eraseThis();
                    //Log.i("ELIMINATO", y.toString());
                    if(p.getAnalogValue() < y.getAnalogValue())
                        direction = AnalogDirection.UP;
                    else
                        direction = AnalogDirection.DOWN;
                }
                else
                {


                    int diff = y.getAnalogValue() - performance.get(i).getAnalogValue();
                    if(direction == AnalogDirection.UP && diff > 0)
                        break;
                    if(direction == AnalogDirection.DOWN && diff < 0)
                        break;
                    z = performance.get(i);
                    z.eraseThis();
                    //Log.i("ELIMINATO", z.toString());
                    //y = z;
                }
            }



            if(z!= null) {
                p.setAnalogStringAndChecksum(z.getAnalogValue(), z.getStringVersion(), z.getCheckSum());
                p.setAction(z.getBytes());
            }
            else if(y != null) {
                p.setAnalogStringAndChecksum(y.getAnalogValue(), y.getStringVersion(), y.getCheckSum());
                p.setAction(y.getBytes());
            }

            pIndex++;
        }




        int timeToAddToOthers = 0;

        //Log.i("MILLIS", "AHUAHSUGFHKDKGFHSGSF \n\n");

        for(PerformancePiece<byte[]> p : performance)
        {
            if(p.isToErase())
            {
                timeToAddToOthers += p.getMillisToAction();
                //Log.i("timezz","-----");
                //Log.i("timezz", String.valueOf(p.getMillisToAction()));
                //Log.i("timezz","-----");
                continue;
            }

            //Log.i("timezz", String.valueOf(timeToAddToOthers));
            //Log.i("timezz", String.valueOf(p.getMillisToAction()));
            p.addMillis(timeToAddToOthers);
            //Log.i("timezz", String.valueOf(p.getMillisToAction()));
            timeToAddToOthers = 0;
        }



        Log.i("ZIP", String.valueOf(timeToAddToOthers));
        performance.removeIf(p -> p.isToErase());
        Log.i("ZIP", String.valueOf(performance.size()));

        /*
        for(PerformancePiece<byte[]> p : performance) {
            Log.i("ZIP", p.toString());
        }
        */

        int duration = 0;
        for(PerformancePiece<byte[]> p : performance)
        {
            duration += p.getMillisToAction();
        }

        super.setDuration(duration);


        for(PerformancePiece<byte[]> p : performance)
        {
            Log.i("MILLIS", String.valueOf(p.getMillisToAction()));
        }

        //Log.i("duration " , String.valueOf(super.getDuration()));
    }


}
