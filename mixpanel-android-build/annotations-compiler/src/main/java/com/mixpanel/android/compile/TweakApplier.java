package com.mixpanel.android.compile;

import com.mixpanel.android.build.BooleanDefault;
import com.mixpanel.android.build.DoubleDefault;
import com.mixpanel.android.build.LongDefault;
import com.mixpanel.android.build.StringDefault;
import com.mixpanel.android.build.Tweak;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

public class TweakApplier {

    public TweakApplier() {
        mTweakedElements = new HashMap<String, ExecutableElement>();
    }

    public AppliedTweak readTweakApplication(Types typeUtils, Elements elementUtils, Element tweakedElement)
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
        for (Element container = tweakedElement; null != container; container = container.getEnclosingElement()) {
            final ElementKind kind = container.getKind();
            if (kind == ElementKind.CLASS || kind == ElementKind.INTERFACE) {
                if (null == tweakedType) { // We tweak the innermost class or interface, but nesting is ok.
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

        final TypeMirror charSequenceType = elementUtils.getTypeElement("java.lang.CharSequence").asType();
        final TypeMirror doubleType = elementUtils.getTypeElement("java.lang.Double").asType();
        final TypeMirror booleanType = elementUtils.getTypeElement("java.lang.Boolean").asType();
        final TypeMirror longType = elementUtils.getTypeElement("java.lang.Long").asType();

        AppliedTweak.ParameterType tweakParameterType = null;
        switch (intendedParamType.getKind()) {
            case BOOLEAN:
                tweakParameterType = AppliedTweak.ParameterType.BOOLEAN_TWEAK;
                break;
            case DOUBLE:
                tweakParameterType = AppliedTweak.ParameterType.DOUBLE_TWEAK;
                break;
            case LONG:
                tweakParameterType = AppliedTweak.ParameterType.LONG_TWEAK;
                break;
            case DECLARED:
                if (typeUtils.isAssignable(intendedParamType, booleanType)) {
                    tweakParameterType = AppliedTweak.ParameterType.BOOLEAN_TWEAK;
                } else if (typeUtils.isAssignable(intendedParamType, doubleType)) {
                    tweakParameterType = AppliedTweak.ParameterType.DOUBLE_TWEAK;
                } else if (typeUtils.isAssignable(intendedParamType, longType)) {
                    tweakParameterType = AppliedTweak.ParameterType.LONG_TWEAK;
                } else if (typeUtils.isAssignable(intendedParamType, charSequenceType)) {
                    tweakParameterType = AppliedTweak.ParameterType.STRING_TWEAK;
                } else {
                    throw new IllegalTweakException("The parameter to a tweaked method " + tweakedElement + " must be assignable from String, boolean, double, or long", tweakedElement);
                }
                break;
            default:
                throw new IllegalTweakException("The parameter to a tweaked method " + tweakedElement + " must be assignable from String, boolean, double, or long", tweakedElement);
        }

        final Tweak tweak = tweakedMethod.getAnnotation(Tweak.class);
        final String tweakName = tweak.name();
        if (null == tweak) {
            throw new RuntimeException("Tweak processor was applied to an untweaked element");
        }

        if (mTweakedElements.containsKey(tweakName)) {
            throw new IllegalTweakException("Tweaks must have unique names- already encountered a Tweak named " + tweakName + " on " + mTweakedElements.get(tweakName), tweakedMethod);
        }

        boolean bValue = false;
        double dValue = 0.0;
        long lValue = 0;
        String sValue = "";

        final BooleanDefault booleanDefault = tweakedMethod.getAnnotation(BooleanDefault.class);
        if (null != booleanDefault) {
            if (tweakParameterType != AppliedTweak.ParameterType.BOOLEAN_TWEAK) {
                throw new IllegalTweakException("Tweak with a boolean default type is applied to a method that takes a " + tweakParameterType.getTypeName() + " argument", tweakedMethod);
            }
            bValue = booleanDefault.value();
        }

        final DoubleDefault doubleDefault = tweakedMethod.getAnnotation(DoubleDefault.class);
        if (null != doubleDefault) {
            if (tweakParameterType != AppliedTweak.ParameterType.DOUBLE_TWEAK) {
                throw new IllegalTweakException("Tweak with a double default type is applied to a method that takes a " + tweakParameterType.getTypeName() + " argument", tweakedMethod);
            }
            dValue = doubleDefault.value();
        }

        final LongDefault longDefault = tweakedMethod.getAnnotation(LongDefault.class);
        if (null != longDefault) {
            if (tweakParameterType != AppliedTweak.ParameterType.LONG_TWEAK) {
                throw new IllegalTweakException("Tweak with a long default type is applied to a method that takes a " + tweakParameterType.getTypeName() + " argument", tweakedMethod);
            }
            lValue = longDefault.value();
        }

        final StringDefault stringDefault = tweakedMethod.getAnnotation(StringDefault.class);
        if (null != stringDefault) {
            if (tweakParameterType != AppliedTweak.ParameterType.STRING_TWEAK) {
                throw new IllegalTweakException("Tweak with a String default type is applied to a method that takes a " + tweakParameterType.getTypeName() + " argument", tweakedMethod);
            }
            sValue = stringDefault.value();
        }

        final AppliedTweak.TweakInfo tweakInfo = new AppliedTweak.TweakInfo(tweakName, bValue, dValue, lValue, sValue);
        return new AppliedTweak(tweakInfo, tweakedMethod, tweakedType, tweakParameterType, tweakedPackage);
    }

    private final Map<String, ExecutableElement> mTweakedElements;
}
