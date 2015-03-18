package com.mixpanel.android.tweaksbuildtest;

import com.mixpanel.android.build.StringDefault;
import com.mixpanel.android.build.Tweak;

public class TweakedObject {
    @Tweak(name="bananas")
    @StringDefault(value="Default Value")
    public void setBananas(String bananas) {
        stringBanana = bananas;
    }

    public String stringBanana = "Before Registration";

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
