package com.mixpanel.android.tweaksbuildtest;

import android.test.AndroidTestCase;

import com.mixpanel.android.mpmetrics.Tweaks;
import com.mixpanel.android.tweaksbuildtest.manual.ManuallyTweakedObject;

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

    public void testUntweaked() {
        final TweakedObject subject = new TweakedObject();
        final Tweaks tweaks = new Tweaks(new SynchronousHandler(), "$$TWEAK_REGISTRAR");
        assertNotNull(tweaks);
        assertEquals("Before Registration", subject.stringBanana);
        assertNull(subject.booleanTweak);
        assertNull(subject.doubleTweak);
        assertNull(subject.longTweak);
    }

    public void testTweakResults() {
        final TweakedObject subject = new TweakedObject();
        final Tweaks tweaks = new Tweaks(new SynchronousHandler(), "$$TWEAK_REGISTRAR");
        tweaks.registerForTweaks(subject);

        assertEquals("Default Value", subject.stringBanana);
        assertEquals(22.3, subject.doubleTweak);
        assertEquals(new Long(22), subject.longTweak);
        assertEquals(new Double(22.3), subject.doubleTweak);

        tweaks.set("bananas", "A B C");
        tweaks.set("double tweak", 11.1d);
        tweaks.set("long tweak", 111l);
        tweaks.set("boolean tweak", false);

        assertEquals("A B C", subject.stringBanana);
        assertEquals(new Boolean(false), subject.booleanTweak);
        assertEquals(new Double(11.1), subject.doubleTweak);
        assertEquals(new Long(111), subject.longTweak);
    }

    public void testNumberTypesWork() {
        final TweakedObject subject = new TweakedObject();
        final Tweaks tweaks = new Tweaks(new SynchronousHandler(), "$$TWEAK_REGISTRAR");
        tweaks.registerForTweaks(subject);

        tweaks.set("double tweak", 11.1f);
        tweaks.set("long tweak", (short)111);

        assertEquals(new Double(11.1), subject.doubleTweak, 0.01);
        assertEquals(new Long(111), subject.longTweak);
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
