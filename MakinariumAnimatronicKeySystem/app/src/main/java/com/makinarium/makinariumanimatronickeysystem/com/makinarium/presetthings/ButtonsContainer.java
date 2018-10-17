package com.makinarium.makinariumanimatronickeysystem.com.makinarium.presetthings;

import android.widget.Button;
import android.widget.ProgressBar;

import com.makinarium.makinariumanimatronickeysystem.FaceSector;

import java.util.HashMap;

public class ButtonsContainer<T> {

    private HashMap<Integer,ButtonPerformance<T>> performanceHashMap;
    private HashMap<Integer,PresetPerformance<T>> presetHashMap;


    public ButtonsContainer()
    {
        performanceHashMap = new HashMap<>();
        presetHashMap = new HashMap<>();
    }


    public void addPerformanceButton(int id, Button button, FaceSector sector, ProgressBar progressBar)
    {
        performanceHashMap.put(id, new ButtonPerformance(button,sector, progressBar));
    }

    public void addPresetButton(int id, Button button, FaceSector sector, ProgressBar progressBar)
    {
        if(sector == FaceSector.PRESET)
            presetHashMap.put(id, new PresetPerformance(button,sector, progressBar));
    }

    public void deactivatesButtonSectorButton(FaceSector sector)
    {
        disActButtons(sector,false);
    }

    public void activatesButtonSectorButton(FaceSector sector)
    {
        disActButtons(sector,true);
    }

    private void disActButtons(FaceSector sector, boolean whatToDo)
    {

        if(sector == FaceSector.PRESET)
        {
            for (Integer id: presetHashMap.keySet())
                if(whatToDo)
                    presetHashMap.get(id).activatesButton();
                else
                    presetHashMap.get(id).deactivatesButton();
            return;
        }

        for (Integer id: performanceHashMap.keySet()) {
            if(performanceHashMap.get(id).getFaceSector() != sector)
                continue;
            if(whatToDo)
                performanceHashMap.get(id).activatesButton();
            else
                performanceHashMap.get(id).deactivatesButton();

        }
    }


    public ButtonPerformance<T> getButtonPerformance(int id)
    {
        if(performanceHashMap.containsKey(id))
            return performanceHashMap.get(id);
        return null;
    }

    public PresetPerformance<T> getPresetPerformance(int id)
    {
        if(presetHashMap.containsKey(id))
            return presetHashMap.get(id);
        return null;
    }

    public void deactivatesAllButtons()
    {
        for(FaceSector sector : FaceSector.values())
            disActButtons(sector,false);
    }

    public void activatesAllButtons()
    {
        for(FaceSector sector : FaceSector.values())
            disActButtons(sector,true);
    }

}
