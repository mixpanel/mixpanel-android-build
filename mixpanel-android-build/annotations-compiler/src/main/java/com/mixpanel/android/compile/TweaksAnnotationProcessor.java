package com.mixpanel.android.compile;

import com.mixpanel.android.build.BooleanDefault;
import com.mixpanel.android.build.Tweak;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

@SupportedAnnotationTypes({"*"})
public class TweaksAnnotationProcessor extends AbstractProcessor {

    public TweaksAnnotationProcessor() {
        super();
        mTweakApplier = new TweakApplier();
        mTweaks = new ArrayList<AppliedTweak>();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (!roundEnv.processingOver()) {
            accumulateApplications(roundEnv);
        } else {
            final Map<String, String> options = processingEnv.getOptions();
            final String outputPackage = options.get("com.mixpanel.android.compiler.RegistrarPackage");
            if (null == outputPackage) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Mixpanel build tools require -Acom.mixpanel.android.compiler.RegistrarPackage option", null);
            }
            emitTweakRegistrar(outputPackage); // TODO this emits a spurious warning about not processing the generated file.
        }

        return false;
    }

    public void accumulateApplications(RoundEnvironment roundEnv) {
        final Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Tweak.class);
        for (Element el : elements) {
            try {
                final AppliedTweak application = mTweakApplier.readTweakApplication(
                        processingEnv.getTypeUtils(),
                        processingEnv.getElementUtils(),
                        el
                );
                mTweaks.add(application);

            } catch (IllegalTweakException e) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage(), e.getElement());
            }
        }
    }

    public void emitTweakRegistrar(final String outputPackage) {
        if (mTweaks.isEmpty()) {
            return; // Nothing to emit.
        }

        final Filer filer = processingEnv.getFiler();
        final TweakClassFormatter formatter = new TweakClassFormatter();
        final String classContents = formatter.tweaksClassAsString(outputPackage, mTweaks);
        final Element[] elementArgs = new Element[mTweaks.size()];
        int i = 0;
        for (final AppliedTweak application:mTweaks) {
            elementArgs[i] = application.getTweakedMethod();
            i++;
        }

        Writer writer = null;
        try {
            final String className = outputPackage + ".$$TWEAK_REGISTRAR";
            final JavaFileObject file = filer.createSourceFile(className, elementArgs);
            writer = file.openWriter();
            writer.write(classContents);
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
        } finally {
            if (null != writer) {
                try {
                    writer.close();
                } catch (IOException e) {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
                }
            }
        }
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedOptions() {
        final Set<String> ret = new HashSet<String>();
        ret.add("com.mixpanel.android.compiler.RegistrarPackage");
        return ret;
    }

    private final TweakApplier mTweakApplier;
    private final List<AppliedTweak> mTweaks;
}
