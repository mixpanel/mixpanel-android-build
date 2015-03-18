package com.mixpanel.android.tweaksbuildtest;

import android.test.AndroidTestCase;

import com.mixpanel.android.mpmetrics.Tweaks;

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

        tweaks.set("bananas", "A B C");
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

    public void testTweaksSuperclasses() {
        final TweakedObject tObj = new TweakedObject();

        {
            final TweakedObject.InnerB childSubject = tObj.new InnerB();
            final Tweaks tweaks = new Tweaks(new SynchronousHandler(), "$$TWEAK_REGISTRAR");
            tweaks.registerForTweaks(childSubject);

            assertEquals("Parent Default at Child", childSubject.parentTweak);
            assertEquals("Child Default at Child", childSubject.childTweak);

            tweaks.set("parent", "Parent Set");
            tweaks.set("child", "Child Set");

            assertEquals("Parent Set at Child", childSubject.parentTweak);
            assertEquals("Child Set at Child", childSubject.childTweak);
        }

        {
            final TweakedObject.InnerA parentSubject = tObj.new InnerA();
            final Tweaks tweaks = new Tweaks(new SynchronousHandler(), "$$TWEAK_REGISTRAR");
            tweaks.registerForTweaks(parentSubject);

            assertEquals("Parent Default at Parent", parentSubject.parentTweak);
            assertEquals("Before Registration", parentSubject.childTweak);


            tweaks.set("parent", "Parent Set");
            tweaks.set("child", "Child Set");

            assertEquals("Parent Set at Parent", parentSubject.parentTweak);
            assertEquals("Before Registration", parentSubject.childTweak);
        }
    }
}
