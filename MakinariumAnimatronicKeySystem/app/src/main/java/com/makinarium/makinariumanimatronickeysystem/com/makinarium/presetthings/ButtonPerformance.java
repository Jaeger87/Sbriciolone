package com.makinarium.makinariumanimatronickeysystem.com.makinarium.presetthings;

import android.support.annotation.NonNull;
import android.widget.Button;

import com.makinarium.makinariumanimatronickeysystem.FaceSector;

import java.util.ArrayList;
import java.util.List;

public class ButtonPerformance{

    private Button button;
    private List<PerformancePiece<byte[]>> performance;
    private FaceSector faceSector;
    private boolean canPerform;
    private int duration;

    public ButtonPerformance(Button button, FaceSector faceSector)
    {
        this.button = button;
        this.faceSector = faceSector;
        canPerform = false;
        performance = new ArrayList<>();
    }

    public Button getButton()
    {
        return button;
    }


    public FaceSector getFaceSector() {
        return faceSector;
    }


    public void deactivatesButton()
    {
        this.button.setEnabled(false);
        this.button.setClickable(false);
    }

    public void activatesButton()
    {
        this.button.setEnabled(true);
        this.button.setClickable(true);
    }

    public boolean canPerform()
    {
        return canPerform;
    }

    public void deletePerformance()
    {
        duration = 0;
        canPerform = false;
        performance = new ArrayList<>();
    }

    public int getDuration()
    {
        return duration;
    }

    public void addPerformancePiece(byte[] action, int time)
    {
        performance.add(new PerformancePiece<>(action,time));
        canPerform = true;
        duration+=time;
    }

    public List<PerformancePiece<byte[]>> getPerformance()
    {
        return new ArrayList<>(performance);
    }
}
