package com.mixpanel.android.build;

import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Parameterizable;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

@SupportedAnnotationTypes("com.mixpanel.android.build.Tweak")
public class TweaksAnnotationProcessor extends AbstractProcessor {

    // TODO REQUIRED CHECKS
    //

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Tweak.class);
        // we should only be associated with methods, and those with classes

        for (Element el:elements) {
            processTweakableElement(el);

        }

        return false; // TODO this should be true if we're only passed Tweak annotations?
    }

    private void processTweakableElement(Element el) {
        if (! (el instanceof ExecutableElement)) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "@Tweak is only applicable to public methods", el);
        }

        if (! el.getModifiers().contains(Modifier.PUBLIC)) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "@Tweak is only applicable to public methods", el);
        }

        // Do we need the class to be public, too? Will this work on interfaces? (It should)
        Element enclosure = el.getEnclosingElement();
        while(enclosure.getKind() != ElementKind.PACKAGE) {
            if (!enclosure.getModifiers().contains(Modifier.PUBLIC)) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "@Tweak is inside of non-public element \"" + enclosure.getSimpleName() + "\"");
            }
        }

        // TODO REQUIRED CHECKS
        // - TWEAK NAME + TYPE IS UNIQUE
        // - THE TWEAK TYPE IS ASSIGNABLE from String, double, long, or boolean (Which means NOT A TYPE PARAMETER)

        System.out.println("OK WITH TWEAKABLE ELEMENT " + el);
    }
}
