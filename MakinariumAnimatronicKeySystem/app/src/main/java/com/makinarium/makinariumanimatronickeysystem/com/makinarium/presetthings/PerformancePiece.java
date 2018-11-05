package com.makinarium.makinariumanimatronickeysystem.com.makinarium.presetthings;

import com.makinarium.makinariumanimatronickeysystem.MessageTypes;

public class PerformancePiece <T>{

    private T action;
    private transient String stringVersion;
    private transient MessageTypes type;
    private int millisToAction = -1;

    public PerformancePiece(T action, int millisToAction)
    {
        this.action = action;
        this.millisToAction = millisToAction;
    }

    public PerformancePiece(T action, int millisToAction, String message)
    {
        this.action = action;
        this.millisToAction = millisToAction;
        this.stringVersion = message;
        parseMessage();
    }

    private void parseMessage()
    {
        type = MessageTypes.fromChar(stringVersion.charAt(1));
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
