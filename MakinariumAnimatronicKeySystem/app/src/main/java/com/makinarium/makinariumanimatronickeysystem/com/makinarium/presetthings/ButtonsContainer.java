package com.makinarium.makinariumanimatronickeysystem.com.makinarium.presetthings;

import android.content.Context;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.makinarium.makinariumanimatronickeysystem.Constants;
import com.makinarium.makinariumanimatronickeysystem.FaceSector;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.HashMap;

public class ButtonsContainer<T> {

    private HashMap<Integer,ButtonPerformance<T>> performanceHashMap;
    private HashMap<Integer,PresetPerformance<T>> presetHashMap;

    private int activeColor;
    private int performToRecColor;

    private Gson gson;

    public ButtonsContainer(int activeColor, int performToRecColor)
    {
        performanceHashMap = new HashMap<>();
        presetHashMap = new HashMap<>();
        this.gson = new Gson();
        this.activeColor = activeColor;
        this.performToRecColor = performToRecColor;
    }


    public void addPerformanceButton(int id, Button button, FaceSector sector, ProgressBar progressBar)
    {
        if(performanceHashMap.containsKey(button.getId()))
            setButtonAndProgressBarPerformance(button, progressBar);
        else
            performanceHashMap.put(id, new ButtonPerformance(button,sector, progressBar, activeColor, performToRecColor));
    }

    public void addPresetButton(int id, Button button, FaceSector sector, ProgressBar progressBar)
    {
        if(presetHashMap.containsKey(button.getId()))
            setButtonAndProgressBarPreset(button, progressBar);
        if(sector == FaceSector.PRESET)
            presetHashMap.put(id, new PresetPerformance(button,sector, progressBar, activeColor, performToRecColor));
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


    public void saveMe(Context context)
    {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(context.getFilesDir() + Constants.SaveFileName), "utf-8"))) {
            writer.write(gson.toJson(this));
        } catch (UnsupportedEncodingException e) {

        } catch (FileNotFoundException e) {

        } catch (IOException e) {
            e.printStackTrace();
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public void updateAllColors()
    {
        for(Integer id: performanceHashMap.keySet())
        {
            performanceHashMap.get(id).updateColor();
        }

        for(Integer id: presetHashMap.keySet())
        {
            presetHashMap.get(id).updateColor();
        }
    }

    private void setButtonAndProgressBarPreset(Button button, ProgressBar progressBar)
    {
        presetHashMap.get(button.getId()).setButtonAndProgressBar(button, progressBar);
    }

    private void setButtonAndProgressBarPerformance(Button button, ProgressBar progressBar)
    {
        performanceHashMap.get(button.getId()).setButtonAndProgressBar(button, progressBar);
    }


}
