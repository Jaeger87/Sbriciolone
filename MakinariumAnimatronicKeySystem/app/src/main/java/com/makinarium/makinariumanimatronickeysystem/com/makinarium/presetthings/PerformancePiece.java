package com.makinarium.makinariumanimatronickeysystem.com.makinarium.presetthings;

public class PerformancePiece <T>{

    private T action;
    private int millisToAction = -1;

    public PerformancePiece(T action)
    {
        this.action = action;
    }

    public PerformancePiece(T action, int millisToAction)
    {
        this.action = action;
        this.millisToAction = millisToAction;
    }

    public T getAction()
    {
        return action;
    }


    public int getMillisToAction()
    {
        return millisToAction;
    }

}
