package com.mixpanel.android.compile;

import com.mixpanel.android.build.Tweak;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;


public class AppliedTweak {

    public static enum ParameterType {
        BOOLEAN_TWEAK("bool") {
            @Override
            public String getFormattedDefaultValue(Tweak t) {
                return Boolean.toString(t.defaultBoolean());
            }
        },
        DOUBLE_TWEAK("double") {
            @Override
            public String getFormattedDefaultValue(Tweak t) {
                return Double.toString(t.defaultDouble());
            }
        },
        LONG_TWEAK("long") {
            @Override
            public String getFormattedDefaultValue(Tweak t) {
                return Long.toString(t.defaultLong());
            }
        },
        STRING_TWEAK("String") {
            @Override
            public String getFormattedDefaultValue(Tweak t) {
                return JavaStringEscape.escape(t.defaultString());
            }
        };

        ParameterType(String typeName) {
            mTypeName = typeName;
        }

        public String getTypeName() {
            return mTypeName;
        }

        public abstract String getFormattedDefaultValue(Tweak t);
        private final String mTypeName;
    }

    public static AppliedTweak readTweakApplication(Types types, Elements elements, Element tweakedElement)
        throws IllegalTweakException {
        if (tweakedElement.getKind() != ElementKind.METHOD) {
            throw new IllegalTweakException("Only methods can be tweaked (not) " + tweakedElement, tweakedElement);
        }

        if (!tweakedElement.getModifiers().contains(Modifier.PUBLIC)) {
            throw new IllegalTweakException("Only public methods may be Tweaked, " + tweakedElement + " is not public", tweakedElement);
        }

        final ExecutableElement tweakedMethod = (ExecutableElement) tweakedElement;
        PackageElement tweakedPackage = null;
        TypeElement tweakedType = null;
        {
            Element container = tweakedElement;
            while (null != container) {
                container = container.getEnclosingElement();
                if (container.getKind() == ElementKind.CLASS || container.getKind() == ElementKind.INTERFACE) {
                    if (null != tweakedType) { // We tweak the innermost class or interface, but nesting is ok.
                        tweakedType = (TypeElement) container;
                    }

                    if(!container.getModifiers().contains(Modifier.PUBLIC)) {
                        throw new IllegalTweakException("All classes or interfaces containing a tweak must be public (and " + container + " is not)", tweakedElement);
                    }
                }

                if (container.getKind() == ElementKind.PACKAGE) {
                    tweakedPackage = (PackageElement) container; // nested packages are impossible, so we should find at most one of these.
                }
            }
        }

        if (null == tweakedPackage) {
            throw new IllegalTweakException("Tweaked method " + tweakedElement + " does not appear to be part of any package, and default package tweaks are not currently supported.", tweakedElement);
        }

        final List<? extends VariableElement> params = tweakedMethod.getParameters();
        if (params.size() != 1) {
            throw new IllegalTweakException("Tweaked method " + tweakedElement + " must take exactly one parameter", tweakedElement);
        }

        if ("".equals(tweakedType)) {
            throw new IllegalTweakException("Anonymous or Local classes cannot have tweaked methods. Tweak the method of a superclass or an interface", tweakedElement);
        }

        final VariableElement param = params.get(0);
        final TypeMirror intendedParamType = param.asType();

        // TODO inbound tweaks must NEVER BE NULL

        final TypeMirror charSequenceType = elements.getTypeElement("java.lang.CharSequence").asType();
        final TypeMirror doubleType = elements.getTypeElement("java.lang.Double").asType();
        final TypeMirror booleanType = elements.getTypeElement("java.lang.Boolean").asType();
        final TypeMirror longType = elements.getTypeElement("java.lang.Long").asType();

        ParameterType tweakParameterType = null;
        switch (intendedParamType.getKind()) {
            case BOOLEAN:
                tweakParameterType = ParameterType.BOOLEAN_TWEAK;
                break;
            case DOUBLE:
                tweakParameterType = ParameterType.DOUBLE_TWEAK;
                break;
            case LONG:
                tweakParameterType = ParameterType.LONG_TWEAK;
                break;
            case DECLARED:
                if (types.isAssignable(intendedParamType, booleanType)) {
                    tweakParameterType = ParameterType.BOOLEAN_TWEAK;
                } else if (types.isAssignable(intendedParamType, doubleType)) {
                    tweakParameterType = ParameterType.DOUBLE_TWEAK;
                } else if (types.isAssignable(intendedParamType, longType)) {
                    tweakParameterType = ParameterType.LONG_TWEAK;
                } else if (types.isAssignable(intendedParamType, charSequenceType)) {
                    tweakParameterType = ParameterType.STRING_TWEAK;
                }
                break;
            default:
                throw new IllegalTweakException("The parameter to a tweaked method " + tweakedElement + " must be String, Boolean, Double, or Long", tweakedElement);
        }

        final Tweak tweak = tweakedMethod.getAnnotation(Tweak.class);
        return new AppliedTweak(tweak, tweakedMethod, tweakedType, tweakParameterType, tweakedPackage);
    }

    public AppliedTweak(Tweak tweak, ExecutableElement tweakedMethod, TypeElement tweakedType, ParameterType paramType, PackageElement tweakedPackage) {
        mTweak = tweak;
        mTweakedMethod = tweakedMethod;
        mTweakedType = tweakedType;
        mParameterType = paramType;
        mTweakedPackage = tweakedPackage;
    }

    public Tweak getTweak() {
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

    private final Tweak mTweak;
    private final ExecutableElement mTweakedMethod;
    private final TypeElement mTweakedType;
    private final ParameterType mParameterType;
    private final PackageElement mTweakedPackage;
}
