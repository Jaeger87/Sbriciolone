package com.makinarium.makinariumanimatronickeysystem.com.makinarium.presetthings;

import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.makinarium.makinariumanimatronickeysystem.FaceSector;

import java.util.ArrayList;
import java.util.List;

public class PresetPerformance extends AbstractPerformance{
    private ButtonPerformance eyebrowns;
    private ButtonPerformance eyelids;
    private ButtonPerformance eyes;
    private ButtonPerformance nose;
    private ButtonPerformance mouth;


    public PresetPerformance(int id, Button button, FaceSector faceSector, ProgressBar progressBar,
                             TextView textView, int activeColor, int performToRecColor)
    {
        super(id, button,faceSector,progressBar, textView, activeColor, performToRecColor);
    }


    public void setButtonPerformance(ButtonPerformance bp)
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


    public List<Integer> getButtonsToPress()
    {
        List result = new ArrayList<>();

        if(eyebrowns != null)
            result.add(eyebrowns.getId());
        if(eyelids != null)
            result.add(eyelids.getId());
        if(eyes != null)
            result.add(eyes.getId());
        if(nose != null)
            result.add(nose.getId());
        if(mouth != null)
            result.add(mouth.getId());

        return result;
    }



}
