package com.makinarium.makinariumanimatronickeysystem.com.makinarium.presetthings;

public class PerformancePiece {

    private byte[] action;
    private int millisToAction = -1;

    public PerformancePiece(byte[] action)
    {
        this.action = action;
    }

    public PerformancePiece(byte[] action, int millisToAction)
    {
        this.action = action;
        this.millisToAction = millisToAction;
    }

    public byte[] getAction()
    {
        return action;
    }


    public int getMillisToAction()
    {
        return millisToAction;
    }

}
