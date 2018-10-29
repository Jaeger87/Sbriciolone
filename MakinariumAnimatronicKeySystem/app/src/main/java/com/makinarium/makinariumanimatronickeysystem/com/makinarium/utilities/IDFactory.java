package com.makinarium.makinariumanimatronickeysystem.com.makinarium.utilities;

import com.makinarium.makinariumanimatronickeysystem.R;

import java.util.HashMap;

public class IDFactory {

    private static HashMap<Integer,Integer> buttonsLogicID;
    private static HashMap<Integer,Integer> textToButtonLogicID;

    public static void initializeButtons()
    {
        CounterClick counter = new CounterClick();


        buttonsLogicID = new HashMap<>();
        buttonsLogicID.put(R.id.preset_01, counter.click());
        buttonsLogicID.put(R.id.preset_02, counter.click());
        buttonsLogicID.put(R.id.preset_03, counter.click());
        buttonsLogicID.put(R.id.preset_04, counter.click());
        buttonsLogicID.put(R.id.preset_05, counter.click());
        buttonsLogicID.put(R.id.preset_06, counter.click());
        buttonsLogicID.put(R.id.preset_07, counter.click());
        buttonsLogicID.put(R.id.preset_08, counter.click());
        buttonsLogicID.put(R.id.preset_09, counter.click());
        buttonsLogicID.put(R.id.preset_10, counter.click());
        buttonsLogicID.put(R.id.preset_11, counter.click());
        buttonsLogicID.put(R.id.preset_12, counter.click());

        buttonsLogicID.put(R.id.eyebrow_01, counter.click());
        buttonsLogicID.put(R.id.eyebrow_02, counter.click());
        buttonsLogicID.put(R.id.eyebrow_03, counter.click());
        buttonsLogicID.put(R.id.eyebrow_04, counter.click());
        buttonsLogicID.put(R.id.eyebrow_05, counter.click());
        buttonsLogicID.put(R.id.eyebrow_06, counter.click());
        buttonsLogicID.put(R.id.eyebrow_07, counter.click());
        buttonsLogicID.put(R.id.eyebrow_08, counter.click());
        buttonsLogicID.put(R.id.eyebrow_09, counter.click());
        buttonsLogicID.put(R.id.eyebrow_10, counter.click());
        buttonsLogicID.put(R.id.eyebrow_11, counter.click());
        buttonsLogicID.put(R.id.eyebrow_12, counter.click());

        buttonsLogicID.put(R.id.eyelid_01, counter.click());
        buttonsLogicID.put(R.id.eyelid_02, counter.click());
        buttonsLogicID.put(R.id.eyelid_03, counter.click());
        buttonsLogicID.put(R.id.eyelid_04, counter.click());
        buttonsLogicID.put(R.id.eyelid_05, counter.click());
        buttonsLogicID.put(R.id.eyelid_06, counter.click());
        buttonsLogicID.put(R.id.eyelid_07, counter.click());
        buttonsLogicID.put(R.id.eyelid_08, counter.click());
        buttonsLogicID.put(R.id.eyelid_09, counter.click());
        buttonsLogicID.put(R.id.eyelid_10, counter.click());
        buttonsLogicID.put(R.id.eyelid_11, counter.click());
        buttonsLogicID.put(R.id.eyelid_12, counter.click());

        buttonsLogicID.put(R.id.eyes_01, counter.click());
        buttonsLogicID.put(R.id.eyes_02, counter.click());
        buttonsLogicID.put(R.id.eyes_03, counter.click());
        buttonsLogicID.put(R.id.eyes_04, counter.click());
        buttonsLogicID.put(R.id.eyes_05, counter.click());
        buttonsLogicID.put(R.id.eyes_06, counter.click());
        buttonsLogicID.put(R.id.eyes_07, counter.click());
        buttonsLogicID.put(R.id.eyes_08, counter.click());
        buttonsLogicID.put(R.id.eyes_09, counter.click());
        buttonsLogicID.put(R.id.eyes_10, counter.click());
        buttonsLogicID.put(R.id.eyes_11, counter.click());
        buttonsLogicID.put(R.id.eyes_12, counter.click());

        buttonsLogicID.put(R.id.nose_01, counter.click());
        buttonsLogicID.put(R.id.nose_02, counter.click());
        buttonsLogicID.put(R.id.nose_03, counter.click());
        buttonsLogicID.put(R.id.nose_04, counter.click());
        buttonsLogicID.put(R.id.nose_05, counter.click());
        buttonsLogicID.put(R.id.nose_06, counter.click());
        buttonsLogicID.put(R.id.nose_07, counter.click());
        buttonsLogicID.put(R.id.nose_08, counter.click());
        buttonsLogicID.put(R.id.nose_09, counter.click());
        buttonsLogicID.put(R.id.nose_10, counter.click());
        buttonsLogicID.put(R.id.nose_11, counter.click());
        buttonsLogicID.put(R.id.nose_12, counter.click());

        buttonsLogicID.put(R.id.mouth_01, counter.click());
        buttonsLogicID.put(R.id.mouth_02, counter.click());
        buttonsLogicID.put(R.id.mouth_03, counter.click());
        buttonsLogicID.put(R.id.mouth_04, counter.click());
        buttonsLogicID.put(R.id.mouth_05, counter.click());
        buttonsLogicID.put(R.id.mouth_06, counter.click());
        buttonsLogicID.put(R.id.mouth_07, counter.click());
        buttonsLogicID.put(R.id.mouth_08, counter.click());
        buttonsLogicID.put(R.id.mouth_09, counter.click());
        buttonsLogicID.put(R.id.mouth_10, counter.click());
        buttonsLogicID.put(R.id.mouth_11, counter.click());
        buttonsLogicID.put(R.id.mouth_12, counter.click());



        textToButtonLogicID = new HashMap<>();

        textToButtonLogicID.put(R.id.presetText01, getLogicID(R.id.preset_01));
        textToButtonLogicID.put(R.id.presetText02, getLogicID(R.id.preset_02));
        textToButtonLogicID.put(R.id.presetText03, getLogicID(R.id.preset_03));
        textToButtonLogicID.put(R.id.presetText04, getLogicID(R.id.preset_04));
        textToButtonLogicID.put(R.id.presetText05, getLogicID(R.id.preset_05));
        textToButtonLogicID.put(R.id.presetText06, getLogicID(R.id.preset_06));
        textToButtonLogicID.put(R.id.presetText07, getLogicID(R.id.preset_07));
        textToButtonLogicID.put(R.id.presetText08, getLogicID(R.id.preset_08));
        textToButtonLogicID.put(R.id.presetText09, getLogicID(R.id.preset_09));
        textToButtonLogicID.put(R.id.presetText10, getLogicID(R.id.preset_10));
        textToButtonLogicID.put(R.id.presetText11, getLogicID(R.id.preset_11));
        textToButtonLogicID.put(R.id.presetText12, getLogicID(R.id.preset_12));


        textToButtonLogicID.put(R.id.eyebrowText01, getLogicID(R.id.eyebrow_01));
        textToButtonLogicID.put(R.id.eyebrowText02, getLogicID(R.id.eyebrow_02));
        textToButtonLogicID.put(R.id.eyebrowText03, getLogicID(R.id.eyebrow_03));
        textToButtonLogicID.put(R.id.eyebrowText04, getLogicID(R.id.eyebrow_04));
        textToButtonLogicID.put(R.id.eyebrowText05, getLogicID(R.id.eyebrow_05));
        textToButtonLogicID.put(R.id.eyebrowText06, getLogicID(R.id.eyebrow_06));
        textToButtonLogicID.put(R.id.eyebrowText07, getLogicID(R.id.eyebrow_07));
        textToButtonLogicID.put(R.id.eyebrowText08, getLogicID(R.id.eyebrow_08));
        textToButtonLogicID.put(R.id.eyebrowText09, getLogicID(R.id.eyebrow_09));
        textToButtonLogicID.put(R.id.eyebrowText10, getLogicID(R.id.eyebrow_10));
        textToButtonLogicID.put(R.id.eyebrowText11, getLogicID(R.id.eyebrow_11));
        textToButtonLogicID.put(R.id.eyebrowText12, getLogicID(R.id.eyebrow_12));
    }


    public static int getLogicID(int rID)
    {
        return buttonsLogicID.get(rID);
    }
}
