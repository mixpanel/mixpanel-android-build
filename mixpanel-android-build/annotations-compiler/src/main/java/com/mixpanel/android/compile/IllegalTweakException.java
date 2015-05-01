package com.mixpanel.android.compile;

import javax.lang.model.element.Element;

public class IllegalTweakException extends Exception {
    public IllegalTweakException(String why, Element element) {
        super(why);
        mElement = element;
    }

    public Element getElement() {
        return mElement;
    }

    private Element mElement;
}
