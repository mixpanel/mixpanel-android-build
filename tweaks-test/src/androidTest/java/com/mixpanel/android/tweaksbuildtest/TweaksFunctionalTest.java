package com.mixpanel.android.tweaksbuildtest;

import android.test.AndroidTestCase;

import com.mixpanel.android.build.Tweak;
import com.mixpanel.android.mpmetrics.Tweaks;

import java.util.HashMap;

public class TweaksFunctionalTest extends AndroidTestCase {
    public void testThisRuns() {
        assertTrue(true);
    }

    public void testManualTweaksCode() {
        final ManuallyTweakedObject subject = new ManuallyTweakedObject();
        final Tweaks tweaks = new Tweaks(new SynchronousHandler());
        tweaks.registerForTweaks(subject);

        assertEquals("Default Value", subject.tweakedString);

        tweaks.set("bananas", "A B C");
        assertEquals("A B C", subject.tweakedString);
    }

    public void testTweakResults() {
        final TweakedObject subject = new TweakedObject();
        final Tweaks tweaks = new Tweaks(new SynchronousHandler());
        tweaks.registerForTweaks(subject);

        assertEquals("Default Value", subject.stringBanana);
        assertEquals(0.0, subject.doubleBanana);

        tweaks.set("bananas", 2.3);
        tweaks.set("bananas", "A B C");

        assertEquals(2.3, subject.doubleBanana);
        assertEquals("A B C", subject.stringBanana);
    }

    public void testUntweakedClasses() {
        final Tweaks tweaks = new Tweaks(new SynchronousHandler());
        final Object ob = new Object();
        try {
            tweaks.registerForTweaks(ob);
            fail("An untweakable object should throw an exception");
        } catch (IllegalStateException e) {
            // ok
        }

        final HashMap<String, Integer> m = new HashMap<String, Integer>();
        try {
            tweaks.registerForTweaks(m);
            fail("An untweakable object should throw an exception");
        } catch (IllegalStateException e) {
            // ok
        }
    }
}
