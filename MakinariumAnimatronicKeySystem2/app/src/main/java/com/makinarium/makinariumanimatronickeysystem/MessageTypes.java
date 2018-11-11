package com.makinarium.makinariumanimatronickeysystem;

public enum MessageTypes {

    SERVO('S'),STATUSCHANGE('C'), EVENT('e'), ERROR('0');
    private char str;
    /**
     *
     */



    private MessageTypes(char str)
    {
        this.str=str;
    }

    @Override
    public String toString()
    {
        return "" + str;
    }

    public static MessageTypes fromChar(char c) {
        for (MessageTypes p : MessageTypes.values()) {
            if (c == p.str) {
                return p;
            }
        }

        return MessageTypes.ERROR;
    }
}
