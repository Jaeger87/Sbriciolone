package com.makinarium.makinariumanimatronickeysystem.com.makinarium.presetthings;

import android.icu.text.RelativeDateTimeFormatter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.makinarium.makinariumanimatronickeysystem.FaceSector;
import com.makinarium.makinariumanimatronickeysystem.MessageTypes;
import com.makinarium.makinariumanimatronickeysystem.com.makinarium.utilities.AnalogDirection;
import com.makinarium.makinariumanimatronickeysystem.com.makinarium.utilities.Constants;

import java.util.ArrayList;
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

    public List<PerformancePiece<byte[]>> getPerformance()
    {
        return new ArrayList<>(performance);
    }


    public void compressMessage()
    {
        int pIndex = 0;
        for(PerformancePiece<byte[]> p : performance)
        {
            if(p.isToErase() || !(p.getType() == MessageTypes.SERVO))
                continue;
            PerformancePiece<byte[]> y = null;
            PerformancePiece<byte[]> z = null;
            int pChannel = p.getChannelPin();
            int millisFuture = 0;
            AnalogDirection direction = AnalogDirection.UP;
            for(int i = pIndex + 1; i < performance.size(); i++)
            {
                millisFuture += performance.get(i).getMillisToAction();
                if(millisFuture > Constants.DELAYTOERASEFORBTE)
                    break;
                if(!(performance.get(i).getType() == MessageTypes.SERVO))
                    continue;
                if(performance.get(i).getChannelPin() != pChannel)
                    continue;
                if(y == null)
                {
                    y = performance.get(i);
                    y.eraseThis();
                    if(p.getAnalogValue() < y.getAnalogValue())
                        direction = AnalogDirection.UP;
                    else
                        direction = AnalogDirection.DOWN;
                }
                else
                {
                    z = performance.get(i);
                    z.eraseThis();
                    int diff = y.getAnalogValue() - z.getAnalogValue();
                    if(direction == AnalogDirection.UP && diff > 0)
                        break;
                    if(direction == AnalogDirection.DOWN && diff < 0)
                        break;
                    y.eraseThis();
                    y = z;
                }
            }

            if(z!= null)
                p.setAnalogValue(z.getAnalogValue());
            else if(y != null)
                p.setAnalogValue(y.getAnalogValue());
            if(z!= null || y != null)
            {
                p.setAction(p.getBytes());
            }
        }

        int timeToAddToOthers = 0;
        for(PerformancePiece<byte[]> p : performance)
        {
            if(p.isToErase())
            {
                timeToAddToOthers += p.getMillisToAction();
                continue;
            }

            p.addMillis(timeToAddToOthers);
        }

        performance.removeIf(p -> p.isToErase());

    }
}
