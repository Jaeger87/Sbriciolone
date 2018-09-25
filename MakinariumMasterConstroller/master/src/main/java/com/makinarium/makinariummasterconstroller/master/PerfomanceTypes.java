package com.makinarium.makinariummasterconstroller.master;

public enum PerfomanceTypes {
    MOUTH('M'),NOISE('N'),EYES('E'),EYEBROWS('B'),PRESET('P'),EYELIDS('L'),ERROR('0');


    private char str;
    /**

     */
    private PerfomanceTypes(char str)
    {
        this.str=str;
    }

    @Override
    public String toString()
    {
        return "" + str;
    }

    public static PerfomanceTypes fromChar(char c) {
            for (PerfomanceTypes p : PerfomanceTypes.values()) {
                if (c == p.str) {
                    return p;
                }
            }

        return PerfomanceTypes.ERROR;
    }
}
