package com.mixpanel.android.tweaksbuildtest;

import com.mixpanel.android.mpmetrics.Tweaks;

public class ManuallyTweakedObject {
    public void manualStringTweak(String s) {
        tweakedString = s;
    }

    public String tweakedString = "UNSET";

    static {
        final Class TWEAKED_CLASS = ManuallyTweakedObject.class; // GENERATE
        final String TWEAK_NAME = "bananas"; // GENERATE
        final String TWEAK_DEFAULT = "Default Value"; // GENERATE With TYPE

        Tweaks.setRegistrar(TWEAKED_CLASS, new Tweaks.TweakRegistrar() {
            @Override
            public void registerObjectForTweaks(final Tweaks t, final Object registrant) {
                final ManuallyTweakedObject typedRegistrant = (ManuallyTweakedObject) registrant; // $(TweakedClass} typedRegistrant = (${TweakedClass}) registrant;
                t.bind(TWEAK_NAME, TWEAK_DEFAULT, new Tweaks.TweakChangeCallback() {
                    @Override
                    public void onChange(Object _ignored) {
                        final String tweakValue = t.getString(TWEAK_NAME, TWEAK_DEFAULT); // final ${TweakType} tweakValue = t.get${TweakType}(TWEAK_NAME, TWEAK_DEFAULT);
                        typedRegistrant.manualStringTweak(tweakValue); // typedRegistrant.${TweakMethodName}(tweakValue);
                    }
                });
            }
        });
    }
}
