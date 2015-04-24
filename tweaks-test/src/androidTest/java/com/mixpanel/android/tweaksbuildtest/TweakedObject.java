package com.mixpanel.android.tweaksbuildtest;

import com.mixpanel.android.build.BooleanDefault;
import com.mixpanel.android.build.DoubleDefault;
import com.mixpanel.android.build.LongDefault;
import com.mixpanel.android.build.StringDefault;
import com.mixpanel.android.build.Tweak;

public class TweakedObject {
    @Tweak(name="bananas")
    @StringDefault(value="Default Value")
    public void setBananas(String bananas) {
        stringBanana = bananas;
    }

    @Tweak(name="boolean tweak")
    @BooleanDefault(true)
    public void setBooleanTweak(boolean t) {
        booleanTweak = t;
    }

    @Tweak(name="double tweak")
    @DoubleDefault(22.3)
    public void setDoubleTweak(double t) {
        doubleTweak = t;
    }

    @Tweak(name="long tweak")
    @LongDefault(22)
    public void setLongTweak(long t) {
        longTweak = t;
    }

    public String stringBanana = "Before Registration";
    public Boolean booleanTweak = null;
    public Double doubleTweak = null;
    public Long longTweak = null;

    public class InnerA {
        @Tweak(name="parent")
        @StringDefault("Parent Default")
        public void setParent(String s) {
            parentTweak = s + " at Parent";
        }

        public void setChild(String s) {
            childTweak = s + " at Parent";
        }

        public String parentTweak = "Before Registration";
        public String childTweak = "Before Registration";
    }

    public class InnerB extends InnerA {
        @Override
        public void setParent(String s) {
            parentTweak = s + " at Child";
        }

        @Tweak(name="child")
        @StringDefault("Child Default")
        @Override
        public void setChild(String s) {
            childTweak = s + " at Child";
        }
    }
}
