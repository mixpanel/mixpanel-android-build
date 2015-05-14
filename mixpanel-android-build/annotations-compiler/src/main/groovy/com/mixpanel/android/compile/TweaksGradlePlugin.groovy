package com.mixpanel.android.compile;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

class TweaksGradlePlugin implements Plugin<Project> {

    // This must match the value of "version" in gradle.properties
    // or you'll be installing old versions
    private static final TARGET_VERSION = '0.1.0-RC1';

    @Override
    void apply(final Project project) {
        project.getDependencies().add('compile', 'com.mixpanel.android:annotations:' + TARGET_VERSION)
        project.getDependencies().add('provided', 'com.mixpanel.android:annotations-compiler:' + TARGET_VERSION)

        project.afterEvaluate {
            if (project.android.hasProperty("libraryVariants")) {
                project.android.libraryVariants.all { variant ->
                    variant.javaCompile.options.compilerArgs += [
                            '-processor', 'com.mixpanel.android.compile.TweaksAnnotationProcessor',
                            '-Acom.mixpanel.android.compiler.RegistrarPackage=' + variant.applicationId
                    ]
                }
            }

            if (project.android.hasProperty("testVariants")) {
                project.android.testVariants.all { variant ->
                    variant.applicationId
                    variant.javaCompile.options.compilerArgs += [
                            '-processor', 'com.mixpanel.android.compile.TweaksAnnotationProcessor',
                            '-Acom.mixpanel.android.compiler.RegistrarPackage=' + variant.applicationId
                    ]
                }
            }

            if (project.android.hasProperty("applicationVariants")) {
                project.android.applicationVariants.all { variant ->
                    variant.javaCompile.options.compilerArgs += [
                            '-processor', 'com.mixpanel.android.compile.TweaksAnnotationProcessor',
                            '-Acom.mixpanel.android.compiler.RegistrarPackage=' + variant.applicationId
                    ]
                }
            }
        }
    }
}