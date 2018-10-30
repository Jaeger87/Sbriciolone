package com.makinarium.makinariumanimatronickeysystem.com.makinarium.presetthings;

import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.makinarium.makinariumanimatronickeysystem.FaceSector;

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

}
