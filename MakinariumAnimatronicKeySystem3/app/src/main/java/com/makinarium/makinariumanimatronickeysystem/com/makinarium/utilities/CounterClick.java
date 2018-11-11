package com.makinarium.makinariumanimatronickeysystem.com.makinarium.utilities;

public class CounterClick {

    private int counter = 0;

    public int click()
    {
        counter++;
        return counter;
    }

    public void reset()
    {
        counter = 0;
    }
}
