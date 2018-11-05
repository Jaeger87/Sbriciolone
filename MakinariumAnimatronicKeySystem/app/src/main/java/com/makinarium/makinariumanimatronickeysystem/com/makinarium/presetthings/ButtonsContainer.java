package com.makinarium.makinariumanimatronickeysystem.com.makinarium.presetthings;

import android.content.Context;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.makinarium.makinariumanimatronickeysystem.com.makinarium.utilities.Constants;
import com.makinarium.makinariumanimatronickeysystem.FaceSector;
import com.makinarium.makinariumanimatronickeysystem.com.makinarium.utilities.IDFactory;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.HashMap;

public class ButtonsContainer<T> {

    private HashMap<Integer,ButtonPerformance> performanceHashMap;
    private HashMap<Integer,PresetPerformance> presetHashMap;

    private int activeColor;
    private int performToRecColor;

    public ButtonsContainer(int activeColor, int performToRecColor)
    {
        performanceHashMap = new HashMap<>();
        presetHashMap = new HashMap<>();
        this.activeColor = activeColor;
        this.performToRecColor = performToRecColor;
    }


    public void addPerformanceButton(int id, Button button, FaceSector sector, ProgressBar progressBar, TextView textView)
    {

        if(performanceHashMap.containsKey(IDFactory.getLogicID(id)))
            setButtonAndProgressBarPerformance(button, progressBar, textView);
        else
            performanceHashMap.put(IDFactory.getLogicID(id), new ButtonPerformance(IDFactory.getLogicID(id), button,sector, progressBar, textView, activeColor, performToRecColor));
    }

    public void addPresetButton(int id, Button button, FaceSector sector, ProgressBar progressBar, TextView textView)
    {
        if(presetHashMap.containsKey(IDFactory.getLogicID(id)))
        {
            setButtonAndProgressBarPreset(button, progressBar, textView);
            return;
        }
        if(sector == FaceSector.PRESET)
            presetHashMap.put(IDFactory.getLogicID(id), new PresetPerformance(IDFactory.getLogicID(id), button,sector, progressBar, textView, activeColor, performToRecColor));
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


    public ButtonPerformance getButtonPerformance(int id)
    {
        int logicID = IDFactory.getLogicID(id);
        if(performanceHashMap.containsKey(logicID))
            return performanceHashMap.get(logicID);
        return null;
    }

    public PresetPerformance getPresetPerformance(int id)
    {
        int logicID = IDFactory.getLogicID(id);
        if(presetHashMap.containsKey(logicID))
            return presetHashMap.get(logicID);
        return null;
    }


    public ButtonPerformance getButtonPerformanceFromLogic(int logicID)
    {
        if(performanceHashMap.containsKey(logicID))
            return performanceHashMap.get(logicID);
        return null;
    }

    public PresetPerformance getPresetPerformanceFromLogic(int logicID)
    {
        if(presetHashMap.containsKey(logicID))
            return presetHashMap.get(logicID);
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


    public void saveMe(Context context, Gson gson)
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

    public void updateAllColorsAndNames()
    {
        for(Integer id: performanceHashMap.keySet())
        {
            performanceHashMap.get(id).updateColor();
            performanceHashMap.get(id).updateTextName();
        }

        for(Integer id: presetHashMap.keySet())
        {
            presetHashMap.get(id).updateColor();
            presetHashMap.get(id).updateTextName();
        }
    }

    private void setButtonAndProgressBarPreset(Button button, ProgressBar progressBar, TextView textView)
    {
        presetHashMap.get(IDFactory.getLogicID(button.getId())).setButtonProgressBarAndTextView(button, progressBar, textView);
    }

    private void setButtonAndProgressBarPerformance(Button button, ProgressBar progressBar, TextView textView)
    {
        performanceHashMap.get(IDFactory.getLogicID(button.getId())).setButtonProgressBarAndTextView(button, progressBar, textView);
    }

    public AbstractPerformance getGenericAbstractPerformance(int id)
    {
        if(performanceHashMap.containsKey(id))
            return performanceHashMap.get(id);
        else
            return presetHashMap.get(id);
    }

}
