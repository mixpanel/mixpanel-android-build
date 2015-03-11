package com.mixpanel.android.tweaksbuildtest;

import android.test.AndroidTestCase;

import com.mixpanel.android.mpmetrics.Tweaks;

public class TweaksFunctionalTest extends AndroidTestCase {
    public void testThisRuns() {
        assertTrue(true);
    }

    public void testTweakResults() {
        final TweakedObject subject = new TweakedObject();
        final Tweaks tweaks = new Tweaks();
        tweaks.registerForTweaks(subject);

        assertEquals(subject.stringBanana, "Default Value");
        assertEquals(subject.doubleBanana, 0.0);

        tweaks.set("bananas", 2.3);
        tweaks.set("bananas", "A B C");

        assertEquals(subject.doubleBanana, 2.3);
        assertEquals(subject.stringBanana, "A B C");
    }
}
