# Mixpanel Build Tools for Android

This project contains tools that make it easier to to integrate Mixpanel into your application using
Gradle and Android Studio.

## Mixpanel Tweaks Annotations

Mixpanel Tweaks are a way for you to A/B test different settings and configuration values in your
application. You can use tweaks directly in your code using the Mixpanel library, but you can
also take advantage of tweaks using the @Tweak annotation on your Java methods.

To use @Tweak, annotate a public method of a public class in your code like this:

```java
public class MyGreatClass {
    @Tweak("greeting")
    public void setGreeting(String greeting) {
        ...
    }
    ...
}
```

Later, when you create an instance of `MyGreatClass`, register it with Mixpanel with

```
MyGreatClass great = new MyGreatClass();
mixpanel.registerForTweaks(great);
```

### Building with Tweak Annotations

Tweak annotations are currently supported in Gradle and Android Studio via a Gradle plugin.

To add Tweak annotations to your build, add the following buildscript dependency to your build.gradle file

```groovy
buildscript {
    dependencies {
        // Add the line below as a dependency of your buildscript
        classpath 'com.mixpanel.android:annotations-compiler:0.1.0'
    }
}

```

Then, apply the Mixpanel annotations plugin to your project by adding the following line
to your gradle file

```groovy
apply plugin: 'mixpanel-android-annotations'
```

That's all you have to do! The mixpanel-android-annotations plugin will use your annotations and
call your methods when new tweak values are received from the Mixpanel API.
