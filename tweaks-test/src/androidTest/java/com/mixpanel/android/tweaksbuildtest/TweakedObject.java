package com.mixpanel.android.tweaksbuildtest;

import com.mixpanel.android.build.Tweak;

class TweakedObject {
    @Tweak(name="bananas", defaultString="Default Value")
    public void setBananas(String bananas) {
        stringBanana = bananas;
    }

    @Tweak(name="bananas", defaultDouble=0.0)
    public void setBananas(double bananas) {
        doubleBanana = bananas;
    }

    public String stringBanana = "Before Registration";
    public double doubleBanana = -1.0;

    public class Inner {
        @Tweak(name="inner", defaultString="A String")
        public void setInner(String s) {

        }

        public String innerTweak = "Before Registration";
    }
}
