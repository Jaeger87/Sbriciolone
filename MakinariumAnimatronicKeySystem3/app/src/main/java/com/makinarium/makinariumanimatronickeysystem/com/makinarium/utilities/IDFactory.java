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

        textToButtonLogicID.put(R.id.eyelidText01, getLogicID(R.id.eyelid_01));
        textToButtonLogicID.put(R.id.eyelidText02, getLogicID(R.id.eyelid_02));
        textToButtonLogicID.put(R.id.eyelidText03, getLogicID(R.id.eyelid_03));
        textToButtonLogicID.put(R.id.eyelidText04, getLogicID(R.id.eyelid_04));
        textToButtonLogicID.put(R.id.eyelidText05, getLogicID(R.id.eyelid_05));
        textToButtonLogicID.put(R.id.eyelidText06, getLogicID(R.id.eyelid_06));
        textToButtonLogicID.put(R.id.eyelidText07, getLogicID(R.id.eyelid_07));
        textToButtonLogicID.put(R.id.eyelidText08, getLogicID(R.id.eyelid_08));
        textToButtonLogicID.put(R.id.eyelidText09, getLogicID(R.id.eyelid_09));
        textToButtonLogicID.put(R.id.eyelidText10, getLogicID(R.id.eyelid_10));
        textToButtonLogicID.put(R.id.eyelidText11, getLogicID(R.id.eyelid_11));
        textToButtonLogicID.put(R.id.eyelidText12, getLogicID(R.id.eyelid_12));

        textToButtonLogicID.put(R.id.eyesText01, getLogicID(R.id.eyes_01));
        textToButtonLogicID.put(R.id.eyesText02, getLogicID(R.id.eyes_02));
        textToButtonLogicID.put(R.id.eyesText03, getLogicID(R.id.eyes_03));
        textToButtonLogicID.put(R.id.eyesText04, getLogicID(R.id.eyes_04));
        textToButtonLogicID.put(R.id.eyesText05, getLogicID(R.id.eyes_05));
        textToButtonLogicID.put(R.id.eyesText06, getLogicID(R.id.eyes_06));
        textToButtonLogicID.put(R.id.eyesText07, getLogicID(R.id.eyes_07));
        textToButtonLogicID.put(R.id.eyesText08, getLogicID(R.id.eyes_08));
        textToButtonLogicID.put(R.id.eyesText09, getLogicID(R.id.eyes_09));
        textToButtonLogicID.put(R.id.eyesText10, getLogicID(R.id.eyes_10));
        textToButtonLogicID.put(R.id.eyesText11, getLogicID(R.id.eyes_11));
        textToButtonLogicID.put(R.id.eyesText12, getLogicID(R.id.eyes_12));

        textToButtonLogicID.put(R.id.noseText01, getLogicID(R.id.nose_01));
        textToButtonLogicID.put(R.id.noseText02, getLogicID(R.id.nose_02));
        textToButtonLogicID.put(R.id.noseText03, getLogicID(R.id.nose_03));
        textToButtonLogicID.put(R.id.noseText04, getLogicID(R.id.nose_04));
        textToButtonLogicID.put(R.id.noseText05, getLogicID(R.id.nose_05));
        textToButtonLogicID.put(R.id.noseText06, getLogicID(R.id.nose_06));
        textToButtonLogicID.put(R.id.noseText07, getLogicID(R.id.nose_07));
        textToButtonLogicID.put(R.id.noseText08, getLogicID(R.id.nose_08));
        textToButtonLogicID.put(R.id.noseText09, getLogicID(R.id.nose_09));
        textToButtonLogicID.put(R.id.noseText10, getLogicID(R.id.nose_10));
        textToButtonLogicID.put(R.id.noseText11, getLogicID(R.id.nose_11));
        textToButtonLogicID.put(R.id.noseText12, getLogicID(R.id.nose_12));

        textToButtonLogicID.put(R.id.mouthText01, getLogicID(R.id.mouth_01));
        textToButtonLogicID.put(R.id.mouthText02, getLogicID(R.id.mouth_02));
        textToButtonLogicID.put(R.id.mouthText03, getLogicID(R.id.mouth_03));
        textToButtonLogicID.put(R.id.mouthText04, getLogicID(R.id.mouth_04));
        textToButtonLogicID.put(R.id.mouthText05, getLogicID(R.id.mouth_05));
        textToButtonLogicID.put(R.id.mouthText06, getLogicID(R.id.mouth_06));
        textToButtonLogicID.put(R.id.mouthText07, getLogicID(R.id.mouth_07));
        textToButtonLogicID.put(R.id.mouthText08, getLogicID(R.id.mouth_08));
        textToButtonLogicID.put(R.id.mouthText09, getLogicID(R.id.mouth_09));
        textToButtonLogicID.put(R.id.mouthText10, getLogicID(R.id.mouth_10));
        textToButtonLogicID.put(R.id.mouthText11, getLogicID(R.id.mouth_11));
        textToButtonLogicID.put(R.id.mouthText12, getLogicID(R.id.mouth_12));

    }


    public static int getLogicID(int rID)
    {
        return buttonsLogicID.get(rID);
    }

    public static int convertTextToButton(int trID)
    {
        return textToButtonLogicID.get(trID);
    }
}
