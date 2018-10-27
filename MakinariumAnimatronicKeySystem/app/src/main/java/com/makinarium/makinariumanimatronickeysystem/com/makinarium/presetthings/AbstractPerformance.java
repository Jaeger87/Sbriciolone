package com.makinarium.makinariumanimatronickeysystem.com.makinarium.presetthings;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.content.res.ResourcesCompat;
import android.widget.Button;
import android.widget.ProgressBar;

import com.makinarium.makinariumanimatronickeysystem.FaceSector;
import com.makinarium.makinariumanimatronickeysystem.R;

public abstract class AbstractPerformance {

    private transient Button button;
    private transient ProgressBar progressBar;
    private FaceSector faceSector;
    private boolean canPerform;
    private int duration;
    private int activeColor;
    private int performToRecColor;
    //TODO Add name
    private String name = "Name";

    public AbstractPerformance(Button button, FaceSector faceSector, ProgressBar progressBar,
                               int activeColor, int performToRecColor)
    {
        this.button = button;
        this.faceSector = faceSector;
        canPerform = false;
        this.progressBar = progressBar;
        this.activeColor = activeColor;
        this.performToRecColor = performToRecColor;
    }

    public void deactivatesButton()
    {
        this.button.setAlpha(.5f);
        this.button.setEnabled(false);
        this.button.setClickable(false);
    }

    public void activatesButton()
    {
        this.button.setAlpha(1);
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

    public void updateColor()
    {
        int id = button.getId();

        if(id == R.id.eyebrow_01 || id == R.id.eyes_01 || id == R.id.eyelid_01 ||
                id == R.id.preset_01 ||id == R.id.mouth_01 || id == R.id.nose_01)
            return;

        if(canPerform)
            button.getBackground().setColorFilter(activeColor, PorterDuff.Mode.SRC_ATOP);
        else
            button.getBackground().setColorFilter(performToRecColor, PorterDuff.Mode.SRC_ATOP);
    }

    public void setButtonAndProgressBar(Button button, ProgressBar progressBar)
    {
        this.button = button;
        this.progressBar = progressBar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
