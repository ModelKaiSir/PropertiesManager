package org.qiukai.properties.manager.controller;

import javafx.beans.property.Property;
import org.qiukai.properties.manager.bean.PropertiesBean;

public interface DataBinding {

    Property start();

    void end(Property property);
}
