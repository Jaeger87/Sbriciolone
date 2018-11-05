package com.makinarium.makinariumanimatronickeysystem.com.makinarium.undo;

import com.makinarium.makinariumanimatronickeysystem.com.makinarium.presetthings.ButtonPerformance;
import com.makinarium.makinariumanimatronickeysystem.com.makinarium.presetthings.PerformancePiece;

import java.util.List;

public class EditPerformance implements EditForUndo {

    private ButtonPerformance buttonPerformance;
    private List<PerformancePiece<byte[]>> pieces;


    public EditPerformance(ButtonPerformance buttonPerformance) {
        this.buttonPerformance = buttonPerformance;
        this.pieces = buttonPerformance.getPerformance();
    }

    @Override
    public void applyUndo() {

        buttonPerformance.deletePerformance();
        for (PerformancePiece<byte[]> piece : pieces)
            buttonPerformance.addPerformancePiece(piece.getAction(), piece.getMillisToAction());

        buttonPerformance.updateColor();
    }
}
