package org.qiukai.properties.manager.controller;

import de.felixroske.jfxsupport.FXMLController;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import java.net.URL;
import java.util.ResourceBundle;
@FXMLController
@Scope("prototype")
public class PropertiesController implements Initializable {

    @FXML
    TableView<String> properties;

    @FXML
    TextField name;

    @Autowired
    DataBinder dataBinder;

    private ObjectProperty result = new SimpleObjectProperty();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        dataBinder.get().bind(result);
    }
}
