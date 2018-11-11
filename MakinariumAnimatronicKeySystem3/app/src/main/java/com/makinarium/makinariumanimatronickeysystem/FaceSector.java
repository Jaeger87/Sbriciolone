package com.makinarium.makinariumanimatronickeysystem;

public enum FaceSector {
    MOUTH('M'),NOSE('N'),EYES('E'),EYELIDS('L'), EYEBROWS('B'),PRESET('P'),ERROR('0');


    private char str;
    /**

     */
    private FaceSector(char str)
    {
        this.str=str;
    }

    @Override
    public String toString()
    {
        return "" + str;
    }

    public static FaceSector fromChar(char c) {
            for (FaceSector p : FaceSector.values()) {
                if (c == p.str) {
                    return p;
                }
            }

        return FaceSector.ERROR;
    }
}
