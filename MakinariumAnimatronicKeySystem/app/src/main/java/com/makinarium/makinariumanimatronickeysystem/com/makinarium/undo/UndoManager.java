package com.makinarium.makinariumanimatronickeysystem.com.makinarium.undo;


import com.makinarium.makinariumanimatronickeysystem.com.makinarium.presetthings.ButtonPerformance;
import com.makinarium.makinariumanimatronickeysystem.com.makinarium.presetthings.ButtonsContainer;
import com.makinarium.makinariumanimatronickeysystem.com.makinarium.presetthings.PresetPerformance;

import java.util.Stack;

public class UndoManager <T>{

    private Stack<EditForUndo> stack;
    private ButtonsContainer<T> container;

    public UndoManager(ButtonsContainer<T> container)
    {
        stack = new Stack<>();
        this.container = container;
    }

    public void addLastEdit(ButtonPerformance<T> buttonPerformance)
    {
        stack.push(new EditPerformance<T>(buttonPerformance));
    }

    public void addLastEdit(PresetPerformance<T> preset)
    {
        stack.push(new EditPreset<T>(preset, container));
    }


    public boolean undo()
    {
        if(stack.isEmpty())
            return false;
        EditForUndo e = stack.pop();
        e.applyUndo();
        return true;
    }

}