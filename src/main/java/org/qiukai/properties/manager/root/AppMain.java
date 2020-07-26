package org.qiukai.properties.manager.root;

import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import de.felixroske.jfxsupport.FXMLView;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.qiukai.properties.manager.views.MainView;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "org.qiukai.properties.manager.*")
public class AppMain extends AbstractJavaFxApplicationSupport {

    public static void main(String[] args) {

        launch(AppMain.class, MainView.class, args);
    }
}
