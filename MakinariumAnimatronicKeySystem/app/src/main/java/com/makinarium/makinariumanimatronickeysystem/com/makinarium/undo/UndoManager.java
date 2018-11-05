package com.makinarium.makinariumanimatronickeysystem.com.makinarium.undo;


import com.makinarium.makinariumanimatronickeysystem.com.makinarium.presetthings.AbstractPerformance;
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

    public void addLastEdit(ButtonPerformance buttonPerformance)
    {
        stack.push(new EditPerformance(buttonPerformance));
    }

    public void addLastEdit(PresetPerformance preset)
    {
        stack.push(new EditPreset<T>(preset, container));
    }

    public void addLastEdit(AbstractPerformance ap, String name)
    {
        stack.push(new EditName(name, ap));
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
