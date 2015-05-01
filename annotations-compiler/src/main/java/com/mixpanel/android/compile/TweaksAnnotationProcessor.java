package com.mixpanel.android.compile;

import com.mixpanel.android.build.BooleanDefault;
import com.mixpanel.android.build.Tweak;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
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
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;

@SupportedAnnotationTypes({"*"})
public class TweaksAnnotationProcessor extends AbstractProcessor {

    public TweaksAnnotationProcessor() {
        super();
        mTweakApplier = new TweakApplier();
        mGeneratedClasses = new HashSet<String>();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "Generating Registrar Index");
            generateClassIndex();
        } else {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "Generating Registrars");
            generateRegistrars(roundEnv);
        }

        return false;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    private void generateRegistrars(RoundEnvironment roundEnv) {
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

            Writer classWriter = null;
            try {
                final String className = name.toString() + ".$$TWEAK_REGISTRAR";
                final JavaFileObject file = filer.createSourceFile(className, elementArgs);
                classWriter = file.openWriter();
                classWriter.write(classContents);
                mGeneratedClasses.add(className);
            } catch (IOException e) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
            } finally {
                if (null != classWriter) {
                    try {
                        classWriter.close();
                    } catch (IOException e) {
                        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
                    }
                }
            }
        }
    }

    private void generateClassIndex() {
        final String indexName = "mixpanel/tweak_registrars";
        final Filer filer = processingEnv.getFiler();
        final Set<String> classesToWrite = new HashSet<String>(mGeneratedClasses);

        BufferedReader oldClassesReader = null;
        try {
            final FileObject oldFile = filer.getResource(StandardLocation.CLASS_OUTPUT, "", indexName);
            final InputStream oldFileStream = oldFile.openInputStream();
            oldClassesReader = new BufferedReader(new InputStreamReader(oldFileStream));
            while (true) {
                final String oldClass = oldClassesReader.readLine();
                if (null == oldClass) {
                    break;
                }
                classesToWrite.add(oldClass);
            }
        } catch (IOException e) {
            // This is ok - the file just doesn't exist.
        } finally {
            if (null != oldClassesReader) {
                try {
                    oldClassesReader.close();
                } catch (IOException tooLate) {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Can't close existing registrar index");
                }
            }
        }

        processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "Going to write resource file with " + mGeneratedClasses);

        Writer outWriter = null;
        if (classesToWrite.size() > 0) {
            try {
                final FileObject fileObject = filer.createResource(StandardLocation.CLASS_OUTPUT, "", indexName);
                final OutputStream outStream = fileObject.openOutputStream();
                outWriter = new OutputStreamWriter(outStream);
                for (final String className : classesToWrite) {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "Writing registrar class " + className + " to " + indexName + "(" + fileObject.getName() + " :: " + fileObject.toUri() + ")");
                    outWriter.write(className);
                    outWriter.write("\n");
                }
            } catch (IOException e) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Can't write to registrar index: " + e.getMessage());
            } finally {
                if (null != outWriter) {
                    try {
                        outWriter.close();
                    } catch (IOException e) {
                        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Can't close registrar index: " + e.getMessage());
                    }
                }
            }
        }
    }

    private final TweakApplier mTweakApplier;
    private final Set<String> mGeneratedClasses;
}
