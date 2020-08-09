package org.qiukai.properties.manager.views;

import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLView;
import org.qiukai.properties.manager.controller.MainController;

import javax.annotation.PreDestroy;

@FXMLView(value = "/views/main.fxml", title = "配置文件管理")
public class MainView extends AbstractFxmlView {


    @PreDestroy
    public void destroy() {

        MainController controller =  (MainController) this.getPresenter();
        controller.destroy();
    }
}
