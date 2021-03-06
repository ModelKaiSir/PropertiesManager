package org.qiukai.properties.manager.controller;

import de.felixroske.jfxsupport.FXMLController;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.qiukai.properties.manager.root.AppMain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import java.net.URL;
import java.util.AbstractMap;
import java.util.Map;
import java.util.ResourceBundle;

@FXMLController
@Scope("prototype")
public class PropertiesController implements Initializable {

    @FXML
    TextArea properties;

    @FXML
    TextField name;

    @Autowired
    DataBinder dataBinder;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Property property = dataBinder.start(this.getClass());

        if (null != property) {
            properties.setText((String) property.getValue());
        }
    }

    @FXML
    void confirm(ActionEvent event){

        dataBinder.end(this.getClass(), new SimpleObjectProperty(new AbstractMap.SimpleEntry(name.getText(), properties.getText())));
    }
}
