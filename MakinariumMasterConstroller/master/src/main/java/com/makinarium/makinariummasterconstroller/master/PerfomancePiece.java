package com.makinarium.makinariummasterconstroller.master;

public class PerfomancePiece {

    private String action;
    private int millisToNext = -1;

    public PerfomancePiece(String action)
    {
        this.action = action;
    }

    public PerfomancePiece(String action, int millisToNext)
    {
        this.action = action;
        this.millisToNext = millisToNext;
    }
}
