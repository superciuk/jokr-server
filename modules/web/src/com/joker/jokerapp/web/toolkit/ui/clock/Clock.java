package com.joker.jokerapp.web.toolkit.ui.clock;

import com.vaadin.ui.AbstractJavaScriptComponent;
import com.vaadin.annotations.JavaScript;

@JavaScript({"clock-connector.js", "clock.js"})
public class Clock extends AbstractJavaScriptComponent {
    public Clock() {
    }

    @Override
    protected ClockState getState() {
        return (ClockState) super.getState();
    }
}