package com.mixpanel.android.tweaksbuildtest;

import android.test.AndroidTestCase;

import com.mixpanel.android.mpmetrics.Tweaks;
import com.mixpanel.android.tweaksbuildtest.ManualTweakClass; // TODO this won't work!

import java.lang.reflect.Field;
import java.util.HashMap;

public class TweaksFunctionalTest extends AndroidTestCase {
    public void testThisRuns() {
        assertTrue(true);
    }

    public void testManualTweaksCode() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        final ManuallyTweakedObject subject = new ManuallyTweakedObject();
        final Tweaks tweaks = new Tweaks(new SynchronousHandler(), "ManualTweakClass");
        tweaks.registerForTweaks(subject);

        assertEquals("Default Value", subject.tweakedString);

        tweaks.set("bananas", "A B C");
        assertEquals("A B C", subject.tweakedString);
    }

    public void testTweakResults() {
        final TweakedObject subject = new TweakedObject();
        final Tweaks tweaks = new Tweaks(new SynchronousHandler(), "$$TWEAK_REGISTRAR");
        tweaks.registerForTweaks(subject);

        assertEquals("Default Value", subject.stringBanana);
        assertEquals(0.0, subject.doubleBanana);

        tweaks.set("bananas", 2.3);
        tweaks.set("bananas", "A B C");

        assertEquals(2.3, subject.doubleBanana);
        assertEquals("A B C", subject.stringBanana);
    }

    public void testUntweakedClasses() {
        final Tweaks tweaks = new Tweaks(new SynchronousHandler(), "$$TWEAK_REGISTRAR");
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

    public void testTweaksInMultipleSuperclasses() {
        fail("Need to test C extends B extends A with tweaks on B and A classes");
    }

    public void testMultipleTweaksOnTheSameClass() {
        fail("Need to resolve what the deal is with Tweaked A.setX and B.setX when both have separate tweaks");

    }
}
