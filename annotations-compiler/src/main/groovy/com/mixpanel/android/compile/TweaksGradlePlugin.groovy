package com.mixpanel.android.compile;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

class TweaksGradlePlugin implements Plugin<Project> {
    @Override
    void apply(final Project project) {
        project.afterEvaluate {
            if (project.android.hasProperty("libraryVariants")) {
                project.android.libraryVariants.all { variant ->
                    variant.javaCompile.options.compilerArgs += [
                            '-processor', 'com.mixpanel.android.compile.TweaksAnnotationProcessor'
                    ]
                }
            }

            if (project.android.hasProperty("testVariants")) {
                project.android.testVariants.all { variant ->
                    variant.javaCompile.options.compilerArgs += [
                            '-processor', 'com.mixpanel.android.compile.TweaksAnnotationProcessor'
                    ]
                }
            }

            if (project.android.hasProperty("applicationVariants")) {
                project.android.applicationVariants.all { variant ->
                    variant.javaCompile.options.compilerArgs += [
                            '-processor', 'com.mixpanel.android.compile.TweaksAnnotationProcessor'
                    ]
                }
            }
        }
    }
}