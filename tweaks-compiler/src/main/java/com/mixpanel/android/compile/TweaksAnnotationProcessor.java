package com.mixpanel.android.compile;

import com.mixpanel.android.build.Tweak;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

@SupportedAnnotationTypes({"*"})
public class TweaksAnnotationProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        final Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Tweak.class);
        final Map<Name, Collection<AppliedTweak>> packageApplications = new HashMap<Name, Collection<AppliedTweak>>();

        System.out.println("Tweaks Annotation Processor Running on Annotations on " + annotations.size() + " elements");
        for (TypeElement elem:annotations) {
            System.out.println("    annotated: " + elem);
        }

        for (Element elem:roundEnv.getRootElements()) {
            System.out.println("    root: " + elem);
        }

        try {
            for (Element el:elements) {
                final AppliedTweak application = AppliedTweak.readTweakApplication(processingEnv.getTypeUtils(), processingEnv.getElementUtils(), el);
                final Name packageName = application.getPackage().getQualifiedName();
                if (!packageApplications.containsKey(packageName)) {
                    packageApplications.put(packageName, new ArrayList<AppliedTweak>());
                }

                final Collection<AppliedTweak> group = packageApplications.get(packageName);
                group.add(application);
            }
        } catch (IllegalTweakException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage(), e.getElement());
        }

        System.out.println("Found " + elements.size() + " => " + packageApplications.size() + " Annotations");

        final TweakClassWriter writer = new TweakClassWriter();

        for (Map.Entry<Name, Collection<AppliedTweak>> packaged:packageApplications.entrySet()) {
            final String classContents = writer.tweaksClassAsString(packaged.getKey(), packaged.getValue());
            System.out.println(classContents);
        }

        return false;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
