package com.makinarium.makinariumanimatronickeysystem.com.makinarium.presetthings;

import com.makinarium.makinariumanimatronickeysystem.MessageTypes;
import com.makinarium.makinariumanimatronickeysystem.com.makinarium.utilities.Constants;

public class PerformancePiece <T>{

    private T action;
    private transient String stringVersion;
    private transient MessageTypes type;
    private transient int channelPin;
    private transient int analogValue;
    private transient boolean toErase = false;
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
        switch(type)
        {
            case SERVO:
                String[] array = stringVersion.split(""+Constants.SEPARATOR);
                try{
                    channelPin = Integer.valueOf(array[1]);
                    analogValue = Integer.valueOf(array[2]);
                }
                catch(Exception e)
                {

                }
                break;
            case STATUSCHANGE:
                break;
            case EVENT:
                break;
            case ERROR:
                break;
            default:
                break;
        }
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

    public void addMillis(int millis)
    {
        millisToAction += millis;
    }

    public int getChannelPin() {
        return channelPin;
    }

    public int getAnalogValue() {
        return analogValue;
    }

    public boolean isToErase() {
        return toErase;
    }

    public void eraseThis()
    {
        toErase = true;
    }

    public MessageTypes getType() {
        return type;
    }
}
