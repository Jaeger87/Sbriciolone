package com.makinarium.makinariumanimatronickeysystem.com.makinarium.undo;

import com.makinarium.makinariumanimatronickeysystem.com.makinarium.presetthings.AbstractPerformance;

public class EditName implements EditForUndo{


    private String oldName;
    private AbstractPerformance button;


    public EditName(String oldName, AbstractPerformance button)
    {
        this.oldName = oldName;
        this.button = button;
    }

    @Override
    public void applyUndo() {
        button.setName(oldName);
    }
}
