package org.qiukai.properties.manager.views;

import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLView;
import org.springframework.context.annotation.Scope;

@FXMLView(value = "/views/update.fxml", title = "修改配置")
@Scope("prototype")
public class UpdateView extends AbstractFxmlView {
}
