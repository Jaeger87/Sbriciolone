package com.makinarium.makinariumanimatronickeysystem.com.makinarium.presetthings;

import android.widget.Button;
import android.widget.ProgressBar;

import com.makinarium.makinariumanimatronickeysystem.FaceSector;

public abstract class AbstractPerformance {

    private Button button;
    private ProgressBar progressBar;
    private FaceSector faceSector;
    private boolean canPerform;
    private int duration;

    public AbstractPerformance(Button button, FaceSector faceSector, ProgressBar progressBar)
    {
        this.button = button;
        this.faceSector = faceSector;
        canPerform = false;
        this.progressBar = progressBar;
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

    public Button getButton()
    {
        return button;
    }

    public FaceSector getFaceSector() {
        return faceSector;
    }

    public int getDuration() {
        return duration;
    }

    protected void setDuration(int duration) {
        this.duration = duration;
    }

    protected void setPerform(boolean canPerform) {this.canPerform = canPerform; }

    public boolean canPerform()
    {
        return canPerform;
    }

    public void deletePerformance()
    {
        duration = 0;
        canPerform = false;

    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    //TODO: Farollo
    public void updateColor()
    {

    }
}
