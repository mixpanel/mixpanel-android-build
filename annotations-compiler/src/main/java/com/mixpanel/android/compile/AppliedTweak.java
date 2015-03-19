package com.mixpanel.android.compile;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;


public class AppliedTweak {

    public static enum ParameterType {
        BOOLEAN_TWEAK("bool") {
            @Override
            public String getFormattedDefaultValue(TweakInfo t) {
                return Boolean.toString(t.getDefaultBoolean());
            }

            @Override
            public String getAccessorName() {
                return "getBoolean";
            }
        },
        DOUBLE_TWEAK("double") {
            @Override
            public String getFormattedDefaultValue(TweakInfo t) {
                return Double.toString(t.getDefaultDouble());
            }

            @Override
            public String getAccessorName() {
                return "getDouble";
            }
        },
        LONG_TWEAK("long") {
            @Override
            public String getFormattedDefaultValue(TweakInfo t) {
                return Long.toString(t.getDefaultLong());
            }

            @Override
            public String getAccessorName() {
                return "getLong";
            }
        },
        STRING_TWEAK("String") {
            @Override
            public String getFormattedDefaultValue(TweakInfo t) {
                return JavaStringEscape.escape(t.getDefaultString());
            }

            @Override
            public String getAccessorName() {
                return "getString";
            }
        };

        ParameterType(String typeName) {
            mTypeName = typeName;
        }

        public String getTypeName() {
            return mTypeName;
        }

        public abstract String getFormattedDefaultValue(TweakInfo t);
        public abstract String getAccessorName();

        private final String mTypeName;
    }

    public static class TweakInfo {
        public TweakInfo(String tweakName, boolean defaultBoolean, double defaultDouble, long defaultLong, String defaultString) {
            mName = tweakName;
            mDefaultBoolean = defaultBoolean;
            mDefaultDouble = defaultDouble;
            mDefaultLong = defaultLong;
            mDefaultString = defaultString;
        }

        public String name() {
            return mName;
        }

        public boolean getDefaultBoolean() {
            return mDefaultBoolean;
        }

        public double getDefaultDouble() {
            return mDefaultDouble;
        }

        public long getDefaultLong() {
            return mDefaultLong;
        }

        public String getDefaultString() {
            return mDefaultString;
        }

        private final String mName;
        private final boolean mDefaultBoolean;
        private final double mDefaultDouble;
        private final long mDefaultLong;
        private final String mDefaultString;

    }

    public AppliedTweak(TweakInfo tweak, ExecutableElement tweakedMethod, TypeElement tweakedType, ParameterType paramType, PackageElement tweakedPackage) {
        mTweak = tweak;
        mTweakedMethod = tweakedMethod;
        mTweakedType = tweakedType;
        mParameterType = paramType;
        mTweakedPackage = tweakedPackage;
    }

    public TweakInfo getTweak() {
        return mTweak;
    }

    public ExecutableElement getTweakedMethod() {
        return mTweakedMethod;
    }

    public PackageElement getPackage() {
        return mTweakedPackage;
    }

    public TypeElement getTweakedType() {
        return mTweakedType;
    }

    public ParameterType getParameterType() {
        return mParameterType;
    }

    public PackageElement getTweakedPackage() {
        return mTweakedPackage;
    }

    private final TweakInfo mTweak;
    private final ExecutableElement mTweakedMethod;
    private final TypeElement mTweakedType;
    private final ParameterType mParameterType;
    private final PackageElement mTweakedPackage;
}
