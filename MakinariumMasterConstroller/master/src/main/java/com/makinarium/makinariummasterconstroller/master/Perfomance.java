package com.makinarium.makinariummasterconstroller.master;

import java.util.ArrayList;
import java.util.List;


public class Perfomance {

    private PerfomanceTypes type;
    private List<PerfomancePiece> pieces;

    public Perfomance(PerfomanceTypes type)
    {
        this.type = type;
        pieces = new ArrayList<PerfomancePiece>();
    }

}
