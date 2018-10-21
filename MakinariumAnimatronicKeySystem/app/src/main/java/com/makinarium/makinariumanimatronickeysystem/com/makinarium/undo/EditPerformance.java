package com.makinarium.makinariumanimatronickeysystem.com.makinarium.undo;

import com.makinarium.makinariumanimatronickeysystem.com.makinarium.presetthings.ButtonPerformance;
import com.makinarium.makinariumanimatronickeysystem.com.makinarium.presetthings.PerformancePiece;

import java.util.List;

public class EditPerformance<T> implements EditForUndo {

    private ButtonPerformance<T> buttonPerformance;
    private List<PerformancePiece<T>> pieces;


    public EditPerformance(ButtonPerformance<T> buttonPerformance) {
        this.buttonPerformance = buttonPerformance;
        this.pieces = buttonPerformance.getPerformance();
    }

    @Override
    public void applyUndo() {

        buttonPerformance.deletePerformance();
        for (PerformancePiece<T> piece : pieces)
            buttonPerformance.addPerformancePiece(piece.getAction(), piece.getMillisToAction());

        buttonPerformance.updateColor();
    }
}
