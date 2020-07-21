package org.qiukai.properties.manager.root;

import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import org.qiukai.properties.manager.views.MainView;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "org.qiukai.properties.manager.*")
public class AppMain extends AbstractJavaFxApplicationSupport {

    public static void main(String[] args) {

        launch(AppMain.class, MainView.class, args);
    }
}
