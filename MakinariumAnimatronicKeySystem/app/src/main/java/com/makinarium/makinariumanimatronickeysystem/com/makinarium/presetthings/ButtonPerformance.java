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

public class ButtonPerformance<T> extends AbstractPerformance{

    private List<PerformancePiece<T>> performance;

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


    public void addPerformancePiece(T action, int time)
    {
        if(performance.size() == 0)
            time = 0;
        performance.add(new PerformancePiece<>(action,time));
        setPerform(true);
        setDuration(getDuration() + time);
    }

    public List<PerformancePiece<T>> getPerformance()
    {
        return new ArrayList<>(performance);
    }


    public void compressMessage()
    {
        int pIndex = 0;
        for(PerformancePiece<T> p : performance)
        {
            if(p.isToErase() || !(p.getType() == MessageTypes.SERVO))
                continue;
            PerformancePiece<T> y = null;
            PerformancePiece<T> z = null;
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

                }
                else
                {
                    y.eraseThis();
                }
            }
        }
    }
}
