package com.mixpanel.android.tweaksbuildtest;

import android.test.AndroidTestCase;

import com.mixpanel.android.mpmetrics.MixpanelAPI;

public class TweaksFunctionalTest extends AndroidTestCase {
    public void testThisRuns() {
        MixpanelAPI mixpanel = MixpanelAPI.getInstance(getContext(), "TEST TOKEN0");
        assertTrue(true);
    }

    /***
     *
     *
     * -
     -    @Test
     -    public void testTweakResults() {
     -        final TweakableSubject subject = new TweakableSubject();
     -        MixpanelAPI mixpanel = MixpanelAPI.getInstance("TEST TOKEN");
     -        mixpanel.registerForTweaks(subject);
     -
     -        assertEquals(subject.stringBanana, "Default Value");
     -        assertEquals(subject.doubleBanana, 0.0);
     -
     -        Tweaks tweaks = mixpanel.getTweaks();
     -        tweaks.set("bananas", 2.3);
     -        tweaks.set("bananas", "A B C");
     -
     -        assertEquals(doubleBanana, 2.3);
     -        assertEquals(stringBanana, "A B C");
     -    }
     -
     -    public static class TweakableSubject {
     -        @Tweak(defaultString="Default Value")
     -        public void setBananas(String bananas) {
     -            stringBanana = bananas;
     -        }
     -
     -        @Tweak(defaultNumber=0.0)
     -        public void setBananas(double bananas) {
     -            doubleBanana = bananas;
     -        }
     -
     -        public String stringBanana = "Before Registration";
     -        public double doubleBanana = -1.0;
     -    }
     */
}
