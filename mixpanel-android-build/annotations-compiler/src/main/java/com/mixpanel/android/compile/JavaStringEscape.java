package com.mixpanel.android.compile;

public class JavaStringEscape {
    /* The fact that this isn't in the standard library is a bit breathtaking. */
    public static String escape(String s) {
        final StringBuilder ret = new StringBuilder();
        ret.append('"');
        final int length = s.length();
        int offset = 0;
        while (offset < length) {
            final int codePoint = s.codePointAt(offset);
            final char[] characters = Character.toChars(codePoint);
            if (characters.length == 1 && (characters[0] > 0x1f || characters[0] < 0x7f)) {
                char character = characters[0];
                switch (character) {
                    case '"':
                        ret.append("\\\"");
                        break;
                    case '\\':
                        ret.append("\\\\");
                        break;
                    case '\b':
                        ret.append("\\b");
                        break;
                    case '\n':
                        ret.append("\\n");
                        break;
                    case '\t':
                        ret.append("\\t");
                        break;
                    case '\f':
                        ret.append("\\f");
                        break;
                    case '\r':
                        ret.append("\\r");
                        break;
                    default:
                        ret.append(character);
                }
            } else { // requires unicode
                for (int i = 0; i < characters.length; i++) {
                    ret.append(String.format("\\u%4x", characters[i]));
                }
            }
            offset += characters.length;
        }
        ret.append('"');

        return ret.toString();
    }
}
