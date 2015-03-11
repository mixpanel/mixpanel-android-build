package com.mixpanel.android.tweaksbuildtest;

import com.mixpanel.android.build.Tweak;

class TweakedObject {
    @Tweak(defaultString="Default Value")
    public void setBananas(String bananas) {
        stringBanana = bananas;
    }

    @Tweak(defaultNumber=0.0)
    public void setBananas(double bananas) {
        doubleBanana = bananas;
    }

    public String stringBanana = "Before Registration";
    public double doubleBanana = -1.0;
}
