package com.mixpanel.android.tweaksbuildtest;

import android.test.AndroidTestCase;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.mixpanel.android.mpmetrics.Tweaks;

public class TweaksFunctionalTest extends AndroidTestCase {
    public void testThisRuns() {
        MixpanelAPI mixpanel = MixpanelAPI.getInstance(getContext(), "TEST TOKEN0");
        assertTrue(true);
    }

    public void testTweakResults() {
        final TweakedObject subject = new TweakedObject();
        MixpanelAPI mixpanel = MixpanelAPI.getInstance(getContext(), "TEST TOKEN1");
        mixpanel.registerForTweaks(subject);

        assertEquals(subject.stringBanana, "Default Value");
        assertEquals(subject.doubleBanana, 0.0);

        Tweaks tweaks = mixpanel.getTweaks();
        tweaks.set("bananas", 2.3);
        tweaks.set("bananas", "A B C");

        assertEquals(subject.doubleBanana, 2.3);
        assertEquals(subject.stringBanana, "A B C");
    }
}
