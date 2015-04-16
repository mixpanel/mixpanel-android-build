package com.mixpanel.android.tweaksbuildtest.manual; // PACKAGE OF ENCLOSING CLASS

import com.mixpanel.android.mpmetrics.Tweaks; // Constant

// PackageTweaks bit won't work, we'll need to do this in the known class...
public class ManualTweakClass implements Tweaks.TweakRegistrar { // This will be named TweakedClass$$TWEAK_REGISTRAR

    @Override
    public void registerObjectForTweaks(final Tweaks t, final Object registrant) {
        if (registrant instanceof ManuallyTweakedObject) {
            final String tweakName = "bananas";
            final String tweakDefault = "Default Value";

            final ManuallyTweakedObject typedRegistrant = (ManuallyTweakedObject) registrant;
            t.defineTweak(tweakName, tweakDefault);
            t.bind(tweakName, registrant, new Tweaks.TweakChangeCallback() {
                @Override
                public void onChange(Object _ignored) {
                    final String tweakValue = t.getString(tweakName);
                    typedRegistrant.manualStringTweak(tweakValue); // typedRegistrant.${TweakMethodName}(tweakValue);
                }
            }); // bind()
        }
    } // registerObjectForTweaks

    public static final ManualTweakClass TWEAK_REGISTRAR = new ManualTweakClass();
}
