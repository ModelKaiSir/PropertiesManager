package org.qiukai.properties.manager.controller;

import javafx.beans.property.Property;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class DataBinder {

    private HashMap<Class, DataBinding> bind = new HashMap<>();

    public void register(Class target, DataBinding data) {

        bind.put(target, data);
    }

    public Property start(Class target) {
        return this.bind.get(target).start();
    }

    public void end(Class taget, Property property) {
        this.bind.get(taget).end(property);
        this.bind.remove(taget);
    }
}
