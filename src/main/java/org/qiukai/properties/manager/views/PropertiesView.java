package org.qiukai.properties.manager.views;

import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLView;
import org.springframework.context.annotation.Scope;

@FXMLView(value = "/views/properties.fxml", title = "复制配置")
@Scope("prototype")
public class PropertiesView extends AbstractFxmlView {

}
