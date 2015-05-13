package com.mixpanel.android.compile;

import java.util.Collection;


public class TweakClassFormatter {
    public String tweaksClassAsString(String packageName, Collection<AppliedTweak> appliedTweaks) {
        final StringBuilder ret = new StringBuilder();
        final String header = String.format(CLASS_HEADER_TEMPLATE, packageName);
        ret.append(header);

        ret.append(DECLARATION_HEADER);
        for (final AppliedTweak application:appliedTweaks) {
            final AppliedTweak.TweakInfo tweak = application.getTweak();
            final AppliedTweak.ParameterType parameterType = application.getParameterType();
            final String block = String.format(DECLARATION_BODY_TEMPLATE, tweak.name(), parameterType.getFormattedDefaultValue(tweak));
            ret.append(block);
        }
        ret.append(DECLARATION_FOOTER);

        ret.append(REGISTRATION_HEADER);
        for (final AppliedTweak application:appliedTweaks) {
            final AppliedTweak.TweakInfo tweak = application.getTweak();
            final AppliedTweak.ParameterType parameterType = application.getParameterType();
            final String block = String.format(
                    REGISTRATION_BODY_TEMPLATE,
                    application.getTweakedType().getQualifiedName(),
                    tweak.name(),
                    parameterType.getFormattedDefaultValue(tweak),
                    parameterType.getTypeName(),
                    parameterType.getAccessorName(),
                    application.getTweakedMethod().getSimpleName()
            );
            ret.append(block);
        }
        ret.append(REGISTRATION_FOOTER);

        ret.append(CLASS_FOOTER);
        return ret.toString();
    }

    private static final String CLASS_HEADER_TEMPLATE =
        "package %1$s;\n" + // PACKAGE OF ENCLOSING CLASS
        "\n" +
        "import com.mixpanel.android.mpmetrics.Tweaks;\n" +
        "\n" +
        "public class $$TWEAK_REGISTRAR implements Tweaks.TweakRegistrar {\n" +
        "\n";

    private static final String DECLARATION_HEADER =
        "    @Override\n" +
        "    public void declareTweaks(final Tweaks t) {\n" +
        "\n";

    private static final String DECLARATION_BODY_TEMPLATE =
        "        t.defineTweak(\"%1$s\", %2$s);\n"; // (TWEAK NAME, TWEAKED DEFAULT VALUE AS LEGAL JAVA LITERAL)

    private static final String DECLARATION_FOOTER =
        "    }\n" +
        "\n";

    private static final String REGISTRATION_HEADER =
        "    @Override\n" +
        "    public void registerObjectForTweaks(final Tweaks t, final Object registrant) {\n" +
        "\n";

    private static final String REGISTRATION_BODY_TEMPLATE =
        "        if (registrant instanceof %1$s) {\n" + // TWEAKED CLASS
        "            final String tweakName = \"%2$s\";\n" + // TWEAK NAME
        "            final %4$s tweakDefault = %3$s;\n" + // PARAM TYPE, TWEAKED DEFAULT VALUE AS LEGAL JAVA LITERAL
        "\n" +
        "            final %1$s typedRegistrant = (%1$s) registrant;\n" + // TWEAKED CLASS, TWEAKED CLASS
        "            t.defineTweak(tweakName, tweakDefault);\n" +
        "            t.bind(tweakName, registrant, new Tweaks.TweakChangeCallback() {\n" +
        "                @Override\n" +
        "                public void onChange(Object _ignored) {\n" +
        "                    final %4$s tweakValue = t.%5$s(tweakName);\n" + // PARAM TYPE, PARAM TYPE
        "                    typedRegistrant.%6$s(tweakValue);\n" + // TWEAKED METHOD NAME
        "                }\n" +
        "            }); // bind()\n" +
        "        }\n" +
        "\n";

    private static final String REGISTRATION_FOOTER =
        "    }\n" +
        "\n";

    private static final String CLASS_FOOTER =
        "    public static final $$TWEAK_REGISTRAR TWEAK_REGISTRAR = new $$TWEAK_REGISTRAR();\n" +
        "} // class\n";

}
