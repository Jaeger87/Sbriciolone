package com.makinarium.makinariumanimatronickeysystem.com.makinarium.presetthings;

import android.widget.Button;
import android.widget.ProgressBar;

import com.makinarium.makinariumanimatronickeysystem.FaceSector;

import java.util.ArrayList;

public class PresetPerformance extends AbstractPerformance{

    private ButtonPerformance<byte[]> eyebrowns;
    private ButtonPerformance<byte[]> eyelids;
    private ButtonPerformance<byte[]> eyes;
    private ButtonPerformance<byte[]> nose;
    private ButtonPerformance<byte[]> mouth;


    public PresetPerformance(Button button, FaceSector faceSector, ProgressBar progressBar)
    {
        super(button,faceSector,progressBar);
    }


    public void setButtonPerformance(ButtonPerformance<byte[]> bp)
    {
        switch (bp.getFaceSector())
        {
            case EYEBROWS:
                eyebrowns = bp;
                break;
            case EYELIDS:
                eyelids = bp;
                break;
            case EYES:
                eyes = bp;
                break;
            case NOSE:
                nose = bp;
                break;
            case MOUTH:
                mouth = bp;
                break;
                default:
                    return;
        }

        setPerform(true);

        if(bp.getDuration() > this.getDuration())
            setDuration(bp.getDuration());
    }


    public void deletePerformance()
    {
        super.deletePerformance();
        eyebrowns = null;
        eyelids = null;
        eyes = null;
        nose = null;
        mouth = null;
    }




}
