package com.mixpanel.android.mpmetrics;

import android.test.AndroidTestCase;

import com.mixpanel.android.mpmetrics.manual.ManualTweakClass;
import com.mixpanel.android.mpmetrics.manual.ManuallyTweakedObject;

public class TweaksFunctionalTest extends AndroidTestCase {
    public void setUp() {
        mRegistrar = Tweaks.findRegistrar("com.mixpanel.android.tweaksbuildtest.test");
        mTweaks = new Tweaks(new SynchronousHandler(), mRegistrar);
    }

    public void testRegistrarFound() {
        assertNotNull(mRegistrar);
    }

    public void testManualTweaksCode() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        final ManuallyTweakedObject subject = new ManuallyTweakedObject();
        final Tweaks tweaks = new Tweaks(new SynchronousHandler(), ManualTweakClass.TWEAK_REGISTRAR);
        tweaks.registerForTweaks(subject);

        assertEquals("DECLARED DEFAULT", subject.tweakedString);

        tweaks.set("bananas", "A B C");
        assertEquals("A B C", subject.tweakedString);
    }

    public void testUntweaked() {
        final TweakedObject subject = new TweakedObject();
        assertEquals("Before Registration", subject.stringBanana);
        assertNull(subject.booleanTweak);
        assertNull(subject.doubleTweak);
        assertNull(subject.longTweak);
    }

    public void testTweakResults() {
        final TweakedObject subject = new TweakedObject();
        mTweaks.registerForTweaks(subject);

        assertEquals("Default Value", subject.stringBanana);
        assertEquals(22.3, subject.doubleTweak);
        assertEquals(new Long(22), subject.longTweak);
        assertEquals(new Double(22.3), subject.doubleTweak);

        mTweaks.set("bananas", "A B C");
        mTweaks.set("double tweak", 11.1d);
        mTweaks.set("long tweak", 111l);
        mTweaks.set("boolean tweak", false);

        assertEquals("A B C", subject.stringBanana);
        assertEquals(new Boolean(false), subject.booleanTweak);
        assertEquals(new Double(11.1), subject.doubleTweak);
        assertEquals(new Long(111), subject.longTweak);
    }

    public void testNumberTypesWork() {
        final TweakedObject subject = new TweakedObject();
        mTweaks.registerForTweaks(subject);

        mTweaks.set("double tweak", 11.1f);
        mTweaks.set("long tweak", (short)111);

        assertEquals(new Double(11.1), subject.doubleTweak, 0.01);
        assertEquals(new Long(111), subject.longTweak);
    }

    public void testTweaksSuperclasses() {
        final TweakedObject subject = new TweakedObject();

        {
            final TweakedObject.InnerB childSubject = subject.new InnerB();
            final Tweaks tweaks = new Tweaks(new SynchronousHandler(), mRegistrar);
            tweaks.registerForTweaks(childSubject);

            assertEquals("Parent Default at Child", childSubject.parentTweak);
            assertEquals("Child Default at Child", childSubject.childTweak);

            tweaks.set("parent", "Parent Set");
            tweaks.set("child", "Child Set");

            assertEquals("Parent Set at Child", childSubject.parentTweak);
            assertEquals("Child Set at Child", childSubject.childTweak);
        }

        {
            final TweakedObject.InnerA parentSubject = subject.new InnerA();
            final Tweaks tweaks = new Tweaks(new SynchronousHandler(), mRegistrar);
            tweaks.registerForTweaks(parentSubject);

            assertEquals("Parent Default at Parent", parentSubject.parentTweak);
            assertEquals("Before Registration", parentSubject.childTweak);


            tweaks.set("parent", "Parent Set");
            tweaks.set("child", "Child Set");

            assertEquals("Parent Set at Parent", parentSubject.parentTweak);
            assertEquals("Before Registration", parentSubject.childTweak);
        }
    }

    private TweakRegistrar mRegistrar;
    private Tweaks mTweaks;
}
