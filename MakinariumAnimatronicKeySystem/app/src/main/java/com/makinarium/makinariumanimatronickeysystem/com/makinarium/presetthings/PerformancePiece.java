package com.makinarium.makinariumanimatronickeysystem.com.makinarium.presetthings;

public class PerformancePiece <T>{

    private T action;
    private int millisToAction = -1;

    public PerformancePiece(T action, int millisToAction)
    {
        this.action = action;
        this.millisToAction = millisToAction;
    }

    public PerformancePiece(PerformancePiece<T> toCopy)
    {
        this.action = toCopy.getAction();
        this.millisToAction = toCopy.getMillisToAction();
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
