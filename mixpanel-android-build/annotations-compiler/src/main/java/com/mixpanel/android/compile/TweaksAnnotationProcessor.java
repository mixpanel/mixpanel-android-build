package com.mixpanel.android.compile;

import com.mixpanel.android.build.BooleanDefault;
import com.mixpanel.android.build.Tweak;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
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
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        final Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Tweak.class);
        final Map<Name, Collection<AppliedTweak>> packageApplications = new HashMap<Name, Collection<AppliedTweak>>();

        for (Element el:elements) {
            try {
                final AppliedTweak application = mTweakApplier.readTweakApplication(
                        processingEnv.getTypeUtils(),
                        processingEnv.getElementUtils(),
                        el
                );
                final Name packageName = application.getPackage().getQualifiedName();
                if (!packageApplications.containsKey(packageName)) {
                    packageApplications.put(packageName, new ArrayList<AppliedTweak>());
                }

                final Collection<AppliedTweak> group = packageApplications.get(packageName);
                group.add(application);

            } catch (IllegalTweakException e) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage(), e.getElement());
            }
        }

        final Filer filer = processingEnv.getFiler();
        final TweakClassFormatter formatter = new TweakClassFormatter();
        for (Map.Entry<Name, Collection<AppliedTweak>> packaged:packageApplications.entrySet()) {
            final Name name = packaged.getKey();
            final Collection<AppliedTweak> applications = packaged.getValue();
            final String classContents = formatter.tweaksClassAsString(name, applications);
            final Element[] elementArgs = new Element[applications.size()];
            int i = 0;
            for (final AppliedTweak application:applications) {
                elementArgs[i] = application.getTweakedMethod();
                i++;
            }

            Writer writer = null;
            try {
                final String className = name.toString() + ".$$TWEAK_REGISTRAR";
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

        return false;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    private final TweakApplier mTweakApplier;
}
