package com.makinarium.makinariumanimatronickeysystem.com.makinarium.undo;

import com.makinarium.makinariumanimatronickeysystem.com.makinarium.presetthings.ButtonsContainer;
import com.makinarium.makinariumanimatronickeysystem.com.makinarium.presetthings.PresetPerformance;

import java.util.List;

public class EditPreset<T> implements EditForUndo {

    private PresetPerformance preset;
    private List<Integer> exButtons;
    private ButtonsContainer container;

    public EditPreset(PresetPerformance preset, ButtonsContainer<T> container) {
        this.preset = preset;
        this.exButtons = preset.getButtonsToPress();
        this.container = container;
    }

    @Override
    public void applyUndo() {
        preset.deletePerformance();

        for(Integer i : exButtons)
            preset.setButtonPerformance(container.getButtonPerformance(i));

        preset.updateColor();
    }
}
