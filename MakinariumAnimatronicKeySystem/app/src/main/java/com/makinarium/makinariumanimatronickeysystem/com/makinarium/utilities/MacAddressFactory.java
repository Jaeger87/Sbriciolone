package com.makinarium.makinariumanimatronickeysystem.com.makinarium.utilities;

public class MacAddressFactory {

    private String mouth;
    private String eyes;
    private String head01;
    private String head02;

    public MacAddressFactory()
    {
        mouth = Constants.macMouthBT;
        eyes = Constants.macEyesBT;
        head01 = Constants.macHead01BT;
        head02 = Constants.macHead02BT;
    }


    public String getMouth() {
        return mouth;
    }

    public void setMouth(String mouth) {
        this.mouth = mouth;
    }

    public String getEyes() {
        return eyes;
    }

    public void setEyes(String eyes) {
        this.eyes = eyes;
    }

    public String getHead01() {
        return head01;
    }

    public void setHead01(String head01) {
        this.head01 = head01;
    }

    public String getHead02() {
        return head02;
    }

    public void setHead02(String head02) {
        this.head02 = head02;
    }
}
